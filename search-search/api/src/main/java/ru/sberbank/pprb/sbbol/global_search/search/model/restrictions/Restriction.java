package ru.sberbank.pprb.sbbol.global_search.search.model.restrictions;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

/**
 * Ограничение поискового фильтра
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, property="_class")
public interface Restriction extends Serializable {
}
