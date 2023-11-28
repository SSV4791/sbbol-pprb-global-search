package ru.sberbank.pprb.sbbol.global_search.search.mapper;

import ru.sberbank.pprb.sbbol.global_search.search.model.Partner;
import ru.sberbank.pprb.sbbol.global_search.search.model.PartnerEntity;

public class PartnerEntityMapper implements SearchableEntityMapper<Partner, PartnerEntity> {

    @Override
    public Class<Partner> getEntityClass() {
        return Partner.class;
    }

    @Override
    public PartnerEntity toSearchableEntity(Object entity) {
        var partner = (Partner) entity;
        return new PartnerEntity()
            .entityId(partner.getEntityId())
            .name(partner.getName())
            .inn(partner.getInn())
            .kpp(partner.getKpp());
    }
}
