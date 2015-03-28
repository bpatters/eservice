package com.myl.eservice.model.impl;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.myl.eservice.model.IPage;
import com.myl.eservice.model.IPageInfo;

import java.lang.reflect.*;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by bpatterson on 3/14/15.
 */
public class Page<T> implements IPageInfo, InvocationHandler {
    public final List<T>  data;

    public int pageSize =0;
    public int pageNumber = 1;
    public long totalElements = 0;

    private Page(T... elements) {
        this.data = ImmutableList.copyOf(elements);
    }

    private Page(List<T> elements) {
        this.data = elements;
    }

    private Page(Iterable<T> elements) {
        this.data = ImmutableList.copyOf(elements);
    }

    public static <T> IPage<T> of(Iterable<T> elements) {
        return (IPage<T>) Proxy.newProxyInstance(Page.class.getClassLoader(), new Class[] { IPage.class}, new Page(elements));
    }

    public static <T> IPage<T> of(List<T> elements) {
       return (IPage<T>) Proxy.newProxyInstance(Page.class.getClassLoader(), new Class[] { IPage.class}, new Page(elements));
    }
    public static <T> IPage<T> of(T... elements) {
        return (IPage<T>) Proxy.newProxyInstance(Page.class.getClassLoader(), new Class[] { IPage.class}, new Page(elements));
    }

    public int getPageSize() {
        return this.pageSize;
    }
    public int getPageNumber() {
        return pageNumber;
    }
    public long getTotalElements() {
        return totalElements;
    }
    public IPageInfo setPageSize(int pageSize) {
        this.pageSize = pageSize;

        return this;
    }
    public IPageInfo setPageNumber(int pageNumber) {
       checkArgument(pageNumber > 0, "Page Number must be > 0");
       this.pageNumber = pageNumber;

        return this;
    }
    public IPageInfo setTotalElements(long totalElements) {
       this.totalElements = totalElements;

        return this;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if (method.getDeclaringClass().isAssignableFrom(this.getClass())) {
                return method.invoke(this, args);
            } else {
                return method.invoke(data,args);
            }
        } catch (InvocationTargetException ex) {
            Throwable targetExcThrowable = ex.getTargetException();
            if (targetExcThrowable instanceof RuntimeException)  {
                throw targetExcThrowable;
            } else {
                throw new RuntimeException(targetExcThrowable);
            }
        }
    }

    public static <FROM, TO> IPage<TO> transform(IPage<FROM> page, Function<FROM, TO> transform) {
        IPage<TO> result = Page.of(Lists.transform(page, transform));
        result.setPageSize(page.getPageSize());
        result.setPageNumber(page.getPageNumber());
        result.setTotalElements(page.getTotalElements());

        return result;
    }
}
