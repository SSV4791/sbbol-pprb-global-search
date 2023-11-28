package ru.sberbank.pprb.sbbol.global_search.search.mapper;

import ru.sberbank.pprb.sbbol.global_search.search.model.Account;
import ru.sberbank.pprb.sbbol.global_search.search.model.AccountEntity;

public class AccountEntityMapper implements SearchableEntityMapper<Account, AccountEntity> {

    @Override
    public Class<Account> getEntityClass() {
        return Account.class;
    }

    @Override
    public AccountEntity toSearchableEntity(Object entity) {
        var account = (Account) entity;
        return new AccountEntity()
            .entityId(account.getEntityId())
            .account(account.getAccount())
            .bic(account.getBic())
            .bankAccount(account.getBankAccount());
    }
}
