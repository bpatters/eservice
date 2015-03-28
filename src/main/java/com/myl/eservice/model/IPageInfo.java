package com.myl.eservice.model;

import java.util.Optional;

/**
 * Created by bpatterson on 3/14/15.
 */
public interface IPageInfo {

    public int getPageSize();
    public int getPageNumber();
    public long getTotalElements();

    public IPageInfo setPageSize(int pageSize);
    public IPageInfo setPageNumber(int pageNumber);
    public IPageInfo setTotalElements(long totalElements);
}
