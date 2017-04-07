package com.daliammao.ptr.handler;

import android.view.View;

import com.daliammao.ptr.PtrFrameLayout;

public interface PtrHandler {

    /**
     * 检查是否可以下拉刷新.
     * <p/>
     * {@link com.daliammao.ptr.handler.PtrDefaultHandler#checkContentCanBePulledDown}
     */
    public boolean checkCanDoDownRefresh(final PtrFrameLayout frame, final View content, final View header);

    /**
     * 检查是否可以上拉加载.
     * <p/>
     * {@link com.daliammao.ptr.handler.PtrDefaultHandler#checkContentCanBePulledUp}
     */
    public boolean checkCanDoUpLoad(final PtrFrameLayout frame, final View content, final View footer);
    /**
     * 开始下拉刷新时触发
     *
     * @param frame
     */
    public void onRefreshBegin(final PtrFrameLayout frame);

    /**
     * 开始上拉加载时触发
     *
     * @param frame
     */
    public void onLoadBegin(final PtrFrameLayout frame);
}