package com.lcd.base.pager;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Chen on 2019/11/21.
 */
public class PageHelper<T> {

    private int total;
    private int pageIndex = 1;
    private List<T> mList = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;

    public PageHelper(RecyclerView.Adapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public T getItem(int position) {
        return mList == null ? null : mList.get(position);
    }

    public List<T> getList() {
        return mList;
    }

    public void setList(List<T> list) {
        this.mList.clear();
        this.mList.addAll(list);
    }

    public int getNextPageIndex() {
        return pageIndex + 1;
    }

    public boolean canLoadMore() {
        return getItemCount() < total;
    }

    public boolean isEmpty() {
        return mList.isEmpty();
    }

    private void refresh(Collection<T> collection) {
        mList.clear();
        mList.addAll(collection);
        mAdapter.notifyDataSetChanged();
    }

    private void loadMore(Collection<T> collection) {
        mList.addAll(collection);
        mAdapter.notifyDataSetChanged();
    }

    public void setPagedData(PagedData<T> pagedData) {
        if (pagedData.currentPage > pageIndex) {
            loadMore(pagedData.data);
            pageIndex = pagedData.currentPage;
        } else {
            refresh(pagedData.data);
            pageIndex = 1;
        }
        this.total = pagedData.total;
    }
}
