package com.example.musicrental.ui.catalog;

/** Храним текущие параметры поиска / фильтра / сортировки. */
public class FilterState {

    public String  query;
    public String  category;
    public Double  minPrice;
    public Double  maxPrice;
    public String  orderBy = "title,asc";

    /** Быстро копируем всё, что не null, из другой структуры. */
    public void copyFrom(FilterState s) {
        query     = s.query;
        category  = s.category;
        minPrice  = s.minPrice;
        maxPrice  = s.maxPrice;
        orderBy   = s.orderBy;
    }
}
