package com.example.musicrental.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** Универсальная обёртка под post-page ответы Spring-Data. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Page<T> {
    public List<T> content;

    public int totalPages;
    public int number;
    public int size;
}
