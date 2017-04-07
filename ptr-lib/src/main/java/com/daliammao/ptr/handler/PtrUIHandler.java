package com.daliammao.ptr.handler;

import com.daliammao.ptr.PtrFrameLayout;
import com.daliammao.ptr.indicator.PtrIndicator;

/**
 *
 */
public interface PtrUIHandler {

    /**
     * View将复位(视图回到起始点).
     *
     * @param frame
     */
    public void onUIReset(PtrFrameLayout frame);

    /**
     * 准备加载(视图刚离开起始点)
     *
     * @param frame
     */
    public void onUIPrepare(PtrFrameLayout frame);

    /**
     * 开始更新
     */
    public void onUIBegin(PtrFrameLayout frame);

    /**
     * 更新完成
     */
    public void onUIComplete(PtrFrameLayout frame);

    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator);
}
