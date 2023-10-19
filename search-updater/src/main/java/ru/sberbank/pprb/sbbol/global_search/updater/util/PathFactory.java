package ru.sberbank.pprb.sbbol.global_search.updater.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Фабрика для создания классов, использующих работу с файловой системой (например, {@link File}, {@link URL}).
 * <p>
 * Создана для упрощенной миграции серверов с Windows на Linux. В ситуации, когда путь задан в Windows-формате,
 * а сервер работает на *nix, или наоборот, заменяет часть ОС-зависимого пути файла для корректной работы в
 * текущей среде.
 * <p>
 * Путь в линукс может начинаться только с базового пути. Базовый путь по умолчанию равен "/bird/config/", может
 * настраиваться через параметр jvm -Dlinux.base.path
 * <p>
 * Для преобразования итогового пути к единому виду
 * например /bird/config/share.ca.sbrf.ru/path -> //bird/config/share/path
 * может быть использован дополнительный справочник автоподмен путей
 * Путь к справочнику задается в параметре -Dconvert.dict.path
 * пример справочника /correqts/core/DboShared/src/test/resource/ru/sbrf/sbbol/dboshared/io/convert_dict.properties
 */
public final class PathFactory {
    private static final Logger log = LoggerFactory.getLogger(PathFactory.class);

    private static final String BASE_LINUX_PATH_MASK = "%BASE_LINUX_PATH%";

    private static boolean linux;
    private static String baseLinuxPath;
    private static String localBaseLinuxPath;
    private static Map<String, String> convertDict;

    //пути формата c:/<path> , /c:/<path>
    private static final Pattern DRIVE_PATTERN = Pattern.compile("\\/?(\\w)\\:(\\/.*)");
    //пути формата file:////<path> , file;//<path>
    private static final Pattern SCHEME_PATTERN = Pattern.compile("(\\w.*):(\\/{2,3})(.*)");
    //пути формата //share/<path>
    private static final Pattern SHARE_WIN_PATTERN = Pattern.compile("\\/{2}(\\w+)(\\/?.*)");
    //пути формата c/<path>
    private static final Pattern LINUX_DRIVE_MATCHER = Pattern.compile("(\\w{1})(\\/{1}.*)?");

    static final String BASE_PATH_LINUX_DEF = "/bird/config/";
    static final String LOCAL_POSTFIX_LINUX = "local/";
    static final String FILE_SCHEMA = "file";


    static {
        String os = System.getProperty("os.name").toLowerCase();
        String baseLinuxPathParam = System.getProperty("linux.base.path");
        String convertDictPathParam = System.getProperty("convert.dict.path");
        init(os, baseLinuxPathParam, convertDictPathParam);
    }

    static void init(String os, String baseLinuxPathParam, String convertDictPathParam) {
        linux = !os.startsWith("windows");

        if (baseLinuxPathParam != null) {
            baseLinuxPathParam = baseLinuxPathParam.trim();
            if (!baseLinuxPathParam.endsWith("/")) {
                baseLinuxPathParam = baseLinuxPathParam + "/";
            }
        }
        baseLinuxPath = baseLinuxPathParam != null && baseLinuxPathParam.length() > 0 ? baseLinuxPathParam : BASE_PATH_LINUX_DEF;
        localBaseLinuxPath = getLocalBaseLinuxPath(baseLinuxPath);

        convertDict = loadConvertDict(convertDictPathParam, baseLinuxPath);

        StringBuilder fileFactoryInfo = new StringBuilder("FILE_FACTORY_INFO:\n");
        fileFactoryInfo.append("linux=").append(linux).append("\n");
        fileFactoryInfo.append("baseLinuxPath=").append(baseLinuxPath).append("\n");
        fileFactoryInfo.append("localBaseLinuxPath=").append(localBaseLinuxPath).append("\n");
        fileFactoryInfo.append("convertDict:\n");
        for (Map.Entry<String, String> entry : convertDict.entrySet()) {
            fileFactoryInfo.append("\t").append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        log.warn(fileFactoryInfo.toString());
    }

    public static File getFileInstance(final String path) {
        return new File(convertPath(path));
    }

    public static File getFileInstance(final String path, final String child) {
        return new File(convertPath(path), child);
    }

    public static File getFileInstance(final File file, final String child) {
        return new File(file, child);
    }

    public static File getFileInstance(final URI uri) {
        File file = new File(uri);
        String path = file.getAbsolutePath();
        return getFileInstance(path);
    }

    public static FileInputStream getFileInputStreamInstance(String path) throws FileNotFoundException {
        return new FileInputStream(convertPath(path));
    }

    public static FileInputStream getFileInputStreamInstance(File file) throws FileNotFoundException {
        return new FileInputStream(file);
    }

    public static FileInputStream getFileInputStreamInstance(final File file, final OnCloseCallback onCloseCallback) throws FileNotFoundException {

        return new FileInputStream(file) {
            @Override
            public void close() throws IOException {
                try {
                    super.close();
                } finally {
                    onCloseCallback.onClose(file);
                }
            }
        };
    }

    public static FileOutputStream getFileOutputStreamInstance(String path) throws FileNotFoundException {
        return new FileOutputStream(convertPath(path));
    }

    public static FileOutputStream getFileOutputStreamInstance(File file) throws FileNotFoundException {
        return new FileOutputStream(file);
    }

    public static FileOutputStream getFileOutputStreamInstance(String fileName, boolean append) throws FileNotFoundException {
        return new FileOutputStream(convertPath(fileName), append);
    }

    public static FileOutputStream getFileOutputStreamInstance(File file, boolean append) throws FileNotFoundException {
        return new FileOutputStream(file, append);
    }

    public static URL getURLInstance(String path) throws MalformedURLException {
        return new URL(convertPath(path));
    }

    public static URL getURLInstance(URL context, String spec) throws MalformedURLException {
        return new URL(context, spec);
    }

    public static URL getURLInstance(String protocol, String host, String file) throws MalformedURLException {
        URL url = new URL(protocol, host, file);
        if (protocol.equals(FILE_SCHEMA)) {
            return new URL(convertPath(url.toExternalForm()));
        }
        return url;
    }

    public static URL getURLInstance(String protocol, String host, int port, String file) throws MalformedURLException {
        URL url = new URL(protocol, host, port, file);
        if (protocol.equals(FILE_SCHEMA)) {
            return new URL(convertPath(url.toExternalForm()));
        }
        return url;
    }

    public static String convertPath(final String path) {
        log.debug("process path {} for {}", path, linux ? "linux" : "windows");

        String normolizedPath = path.trim().replace("\\", "/");

        String convertedPath = normolizedPath;
        String scheme = getScheme(convertedPath);
        if (scheme != null) {
            if (!scheme.equals(FILE_SCHEMA)) {
                return path;
            }
            convertedPath = removeScheme(convertedPath);
        }

        if (isLinuxPath(convertedPath) == linux) {
            return path;
        }

        if (linux) {
            convertedPath = convertToLinuxPath(convertedPath);
        } else {
            convertedPath = convertToWindowsPath(convertedPath);
        }

        convertedPath = addScheme(convertedPath, scheme);
        if (normolizedPath.equals(convertedPath)) {
            return path;
        }

        log.warn("convert {} -> {}", path, convertedPath);

        return convertedPath;
    }

    private static Map<String, String> loadConvertDict(String convertDictPathParam, String baseLinuxPath) {
        Map<String, String> dict = new HashMap<String, String>();

        if (convertDictPathParam != null) {
            File file = new File(convertDictPathParam);
            if (file.exists()) {
                try (InputStream is = new FileInputStream(file)) {
                    Properties properties = new Properties();
                    properties.load(is);

                    for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                        String path = (String) entry.getKey();

                        String key = path.trim().replace(BASE_LINUX_PATH_MASK, baseLinuxPath);
                        String value = ((String) entry.getValue()).trim().replace(BASE_LINUX_PATH_MASK, baseLinuxPath);
                        dict.put(key, value);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return Collections.unmodifiableMap(dict);
    }

    static String getLocalBaseLinuxPath(String baseLinuxPathParam) {
        return baseLinuxPathParam + LOCAL_POSTFIX_LINUX;
    }

    private static String convertToWindowsPath(final String path) {
        String result = path;

        if (result.startsWith(localBaseLinuxPath)) {
            Matcher matcher = LINUX_DRIVE_MATCHER.matcher(result.substring(localBaseLinuxPath.length()));
            if (matcher.matches()) {
                String drive = matcher.group(1);
                result = drive + ":" + matcher.group(2);
                result = convertByDict(result);
            }
        } else if (result.startsWith(baseLinuxPath)) {
            String subPath = result.substring(baseLinuxPath.length());
            result = convertByDict("//" + subPath);
        }
        return result;
    }

    private static String convertToLinuxPath(final String path) {
        String result = path;


        Matcher matcher = DRIVE_PATTERN.matcher(result);
        if (matcher.matches()) {
            String drive = matcher.group(1);
            String subPath = drive.toLowerCase() + matcher.group(2);
            return convertByDict(localBaseLinuxPath + subPath);
        }

        matcher = SHARE_WIN_PATTERN.matcher(result);
        if (matcher.matches()) {
            String shareName = matcher.group(1);
            String subPath = shareName.toLowerCase() + matcher.group(2);
            result = convertByDict(baseLinuxPath + subPath);
        }

        return result;
    }

    private static String convertByDict(String path) {
        for (Map.Entry<String, String> entry : convertDict.entrySet()) {
            if (path.startsWith(entry.getKey())) {
                return entry.getValue() + path.substring(entry.getKey().length());
            }
        }
        return path;
    }

    private static String addScheme(String path, String scheme) {
        if (scheme == null) {
            return path;
        }

        if (path.startsWith("//")) {
            return scheme + ":" + path;
        } else if (path.startsWith("/")) {
            return scheme + "://" + path;
        } else {
            return scheme + ":///" + path;
        }
    }

    private static String removeScheme(String path) {
        Matcher matcher = SCHEME_PATTERN.matcher(path);
        if (matcher.matches()) {
            //если путь локальный, оставляем только один /
            if ("///".equals(matcher.group(2))) {
                return "/" + matcher.group(3);
            } else {
                return matcher.group(2) + matcher.group(3);
            }
        }
        return path;
    }

    private static boolean isLinuxPath(String path) {
        return path.startsWith(baseLinuxPath);
    }

    private static String getScheme(String path) {
        Matcher matcher = SCHEME_PATTERN.matcher(path);
        if (matcher.matches()) {
            return matcher.group(1);
        }

        return null;
    }

    @FunctionalInterface
    public interface OnCloseCallback {
        void onClose(File file);
    }
}
