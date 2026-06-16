package com.read.api.api.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse<T> {

    List<T> content;

    boolean empty;
    boolean first;
    boolean last;

    int number;
    int numberOfElements;
    int size;

    long totalElements;
    int totalPages;
}