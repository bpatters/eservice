package com.myl.eservice.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.myl.eservice.model.IPage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by bpatterson on 3/14/15.
 */
public class PageDTO<T> {
    private int pageSize;
    private int pageNumber;
    private long totalElements;
    @JsonDeserialize(as = ArrayList.class)
    private List<T> elements;

    public PageDTO(int pageSize, int pageNumber, long totalElements, IPage<T> elements) {
        this.setPageSize(pageSize);
        this.setPageNumber(pageNumber);
        this.setTotalElements(totalElements);
        this.setElements(elements);
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public List<T> getElements() {
        return elements;
    }

    public void setElements(List<T> elements) {
        this.elements = elements;
    }
}
