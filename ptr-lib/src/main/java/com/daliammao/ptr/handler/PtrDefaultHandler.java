package com.daliammao.ptr.handler;

import android.view.View;

import com.daliammao.ptr.PtrFrameLayout;

public abstract class PtrDefaultHandler implements PtrHandler {

    //view可以滑动,而且上面还有内容返回true
    public static boolean canChildScrollUp(View view) {
        return view.canScrollVertically(-1);
    }

    //view可以滑动,而且下面还有内容返回true
    public static boolean canChildScrollDown(View view) {
        return view.canScrollVertically(1);
    }
    /**
     * 默认情况下检查可否下拉刷新
     *
     * @param frame
     * @param content
     * @param header
     * @return
     */
    public static boolean checkContentCanBePulledDown(PtrFrameLayout frame, View content, View header) {
        return !canChildScrollUp(content);
    }

    /**
     * 默认情况下检查可否上拉加载
     *
     * @param frame
     * @param content
     * @param header
     * @return
     */
    public static boolean checkContentCanBePulledUp(PtrFrameLayout frame, View content, View header) {
        return !canChildScrollDown(content);
    }

    @Override
    public boolean checkCanDoDownRefresh(PtrFrameLayout frame, View content, View header) {
        return checkContentCanBePulledDown(frame, content, header);

    }

    @Override
    public boolean checkCanDoUpLoad(PtrFrameLayout frame, View content, View footer) {
        return checkContentCanBePulledUp(frame, content, footer);

    }
}