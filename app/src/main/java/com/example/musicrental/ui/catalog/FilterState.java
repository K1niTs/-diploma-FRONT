package com.example.musicrental.ui.catalog;

public class FilterState {

    public String  query;
    public String  category;
    public Double  minPrice;
    public Double  maxPrice;
    public String  orderBy = "title,asc";

    public void copyFrom(FilterState s) {
        query     = s.query;
        category  = s.category;
        minPrice  = s.minPrice;
        maxPrice  = s.maxPrice;
        orderBy   = s.orderBy;
    }
}
