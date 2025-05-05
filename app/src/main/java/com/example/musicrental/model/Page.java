package com.example.musicrental.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** Универсальная обёртка под post-page ответы Spring-Data. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Page<T> {
    public List<T> content;

    /* поля «для красоты» – используются в пагинации */
    public int totalPages;
    public int number;       // номер текущей страницы
    public int size;         // размер страницы
}
