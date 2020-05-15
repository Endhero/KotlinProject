package com.lcd.base.pager;

import java.util.List;

/**
 * Created by Chen on 2019/12/3.
 */
public class PagedData<T> {
    List<T> data;
    int currentPage;
    int total;

    public PagedData(List<T> data, int currentPage, int total) {
        this.data = data;
        this.currentPage = currentPage;
        this.total = total;
    }

    public boolean isRefresh() {
        return currentPage == 1;
    }

    public List<T> getData() {
        return data;
    }
}
