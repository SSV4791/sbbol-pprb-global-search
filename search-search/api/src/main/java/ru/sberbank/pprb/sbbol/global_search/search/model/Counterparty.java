
package ru.sberbank.pprb.sbbol.global_search.search.model;


import ru.sberbank.pprb.sbbol.global_search.core.common.DefaultIndexNameResolvingStrategy;
import ru.sberbank.pprb.sbbol.global_search.core.entity.IndexNameResolvingStrategyRef;
import ru.sberbank.pprb.sbbol.global_search.core.entity.Queryable;
import ru.sberbank.pprb.sbbol.global_search.core.entity.SearchableEntity;
import ru.sberbank.pprb.sbbol.global_search.search.model.restrictions.DigitalIdRestriction;
import ru.sberbank.pprb.sbbol.global_search.search.model.restrictions.RestrictedAccess;

import java.util.Objects;
import java.util.UUID;

@SearchableEntity(
    name = Counterparty.ENTITY_NAME,
    indexNameResolvingStrategy = @IndexNameResolvingStrategyRef(type = DefaultIndexNameResolvingStrategy.class)
)
@RestrictedAccess(mandatoryRestrictions = DigitalIdRestriction.class)
public class Counterparty extends BaseSearchableEntity {

    static final String ENTITY_NAME = "counterparty";

    private String digitalId;

    @Queryable
    private String name;

    @Queryable
    private String account;

    @Queryable
    private String inn;

    @Queryable
    private String kpp;

    @Queryable
    private String bankBic;

    private String bankAccount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Counterparty that = (Counterparty) o;
        return Objects.equals(digitalId, that.digitalId) && Objects.equals(name, that.name) && Objects.equals(account, that.account) && Objects.equals(inn, that.inn) && Objects.equals(kpp, that.kpp) && Objects.equals(bankBic, that.bankBic) && Objects.equals(bankAccount, that.bankAccount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), digitalId, name, account, inn, kpp, bankBic, bankAccount);
    }

    @Override
    public String toString() {
        return "Counterparty{" +
            "digitalId='" + digitalId + '\'' +
            ", name='" + name + '\'' +
            ", account='" + account + '\'' +
            ", inn='" + inn + '\'' +
            ", kpp='" + kpp + '\'' +
            ", bankBic='" + bankBic + '\'' +
            ", bankAccount='" + bankAccount + '\'' +
            '}';
    }
}
