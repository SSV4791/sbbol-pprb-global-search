import ru.sbrf.ufs.pipeline.Const
import ru.sbrf.ufs.pipeline.docker.DockerRunBuilder
import ru.sbrf.ufs.pipeline.util.CollectionUtil

@Library(['ufs-jobs@master']) _

def bitbucketCredential = null
def bitbucketSshCredential = null
def settings = [:]
def credential = null
def repoName = null

pipeline {

    agent {
        label params.type == 'release' ? 'clearAgent' : (env.AGENT_LABEL ?: 'ufs-pr-check')
    }

    options {
        timestamps()
        skipDefaultCheckout()
    }

    stages {
        /**
         * Вычитывает envs из config file репозитории Jenkins
         */
        stage('Read jenkins folder configuration') {
            steps {
                script {
                    description.setForJob(
                        pipelineDescription: 'Pipeline предназначен для публикации библиотек в Nexus 3. В репозитории maven-lib-dev и maven-lib-release в зависимости от входного параметра `type`.'
                    )
                    configFileProvider([configFile(fileId: 'common', variable: 'CONFIG')]) {
                        def config = readYaml(file: CONFIG)
                        config.each { k, v -> env."${k}" = v }
                        bitbucketCredential = secman.makeCredMapWithEnvs(BITBUCKET_REST_TOKEN)
                        bitbucketSshCredential = env.SSH ? secman.makeCredMapWithEnvs(env.SSH) : Const.BITBUCKET_DBO_KEY_SECMAN
                    }
                }
            }
        }

        /**
         * Клонирует указанный git branch
         */
        stage('Checkout git and init envs') {
            steps {
                script {
                    git.checkoutRef(bitbucketSshCredential, GIT_PROJECT, GIT_REPOSITORY, params.branch ?: env.RELEASE_BRANCH ?: 'master')
                    settings = CollectionUtil.merge(
                        readYaml(file: 'jenkins/settings.yml'),
                        env.CUSTOM_SETTINGS_PATH ? readYaml(file: env.CUSTOM_SETTINGS_PATH) : [:]
                    )
                    credential = ['tuz_path': env.TUZ_PATH, 'approle_path': env.SECMAN_STORAGE_PATH + '/approle']
                    repoName = "${params.type}Repo"
                }
            }
        }

        stage('Update parameters') {
            steps {
                script {
                    properties([
                        parameters([
                            string(name: 'branch', defaultValue: env.RELEASE_BRANCH ?: 'master', description: 'Ветка для сборки образа', trim: true),
                            choice(name: 'type', choices: ['dev', 'release'].minus(settings.release.default_values?.type).plus(0, [settings.release.default_values?.type]).findAll(), description: 'Тип сборки'),
                            string(name: 'version', defaultValue: settings.release?.build_gradle?.version ?: '01.00.00', description: 'Версия библиотеки, по умолчанию берется из settings.yml')
                        ])
                    ])
                }
            }
        }

        /**
         * Публикуем артефакт в нексус
         */
        stage('Build Java and Publish') {
            steps {
                script {
                    vault.withUserPass([path              : credential.path, userVar: 'USER', passVar: 'PASSWORD',
                                        userField         : credential.user_field,
                                        passField         : credential.pass_field,
                                        tuz_path          : credential.tuz_path,
                                        approle_path      : credential.approle_path,
                                        approle_credential: credential.approle_credential]) {
                        withEnv(["TOKEN_NAME=${USER}", "TOKEN_PASSWORD=${PASSWORD}"]) {
                            def publishCommand = './gradlew ' +
                                '--info ' +  //необходимый флаг для распознавания сборки на clearAgent
                                "-PtokenName=${USER}${secman.getDomainName(credential)} " +
                                "-PtokenPassword=${PASSWORD} " +
                                "-Dgradle.wrapperUser=${USER}${secman.getDomainName(credential)} " +
                                "-Dgradle.wrapperPassword=${PASSWORD} " +
                                "-Pversion=${params.version} " +
                                "-Prepo=${repoName} " +
                                '-PpublishApi=true ' +
                                'build -x test ' +
                                'publish'
                            new DockerRunBuilder(this)
                                .registry(Const.NEXUS3_DEV_REGISTRY, credential)
                                .volume("${WORKSPACE}", '/build')
                                .extra('-w /build')
                                .cpu(settings.release?.build_gradle?.cpu ?: 1)
                                .memory(settings.release?.build_gradle?.memory ?: '2g')
                                .image(settings.docker?.images?.java ?: Const.DOCKER_JAVA)
                                .cmd(publishCommand)
                                .run()
                        }
                    }
                }
            }
        }

        /**
         * Добавляем git-tag
         */
        stage('Add git-tag') {
            steps {
                script {
                    def tag = "${repoName}_${params.version}"
                    def tagFromRepo = git.tags().find { it == tag }
                    if (tagFromRepo) {
                        git.rmtag(bitbucketSshCredential, tag, GIT_PROJECT, GIT_REPOSITORY)
                    }
                    git.tag(bitbucketSshCredential, tag, GIT_PROJECT, GIT_REPOSITORY)
                }
            }
        }
    }
    post {
        failure {
            script {
                description.setForBuild()
            }
        }
        /**
         * Обязательно подчищаем за собой
         */
        cleanup {
            cleanWs()
        }
    }
}
