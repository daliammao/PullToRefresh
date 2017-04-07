package com.daliammao.ptr.indicator;

import android.graphics.PointF;

/**
 * @author: zhoupengwei
 * @time:15/12/2-上午10:27
 * @Email: 496946423@qq.com
 * @desc: 用来提供偏移量的各种方法
 */
public class PtrIndicator {
    //初使位置
    public final static int POS_START = 0;

    //触发刷新时移动的位置高度
    protected int mOffsetToRefresh = 0;
    //触发加载时移动到位置高度
    protected int mOffsetToLoad = 0;

    //记录最后一次触摸事件的位置
    private PointF mPtLastMove = new PointF();
    //上一次触发触屏事件的X偏移量
    private float mOffsetX;
    //上一次触发触屏事件的Y偏移量(Y偏移量会受阻尼系数影响)
    private float mOffsetY;

    //当前位置,大于0表示头部正在显示,小于0表示脚部正在显示.
    private int mCurrentPos = 0;
    //并非当前,最近一次mCurrentPos的值
    private int mLastPos = 0;
    //当前触屏事件按下的位置
    private int mPressedPos = 0;

    //头部高度
    private int mHeaderHeight;
    //脚部高度
    private int mFooterHeight;

    //触发刷新时移动的位置比例,移动达到头部高度1.2倍时可触发刷新操作
    private float mRatioOfHeightToRefresh = 1.2f;
    //触发加载时移动的位置比例,移动达到脚部高度1.2倍时可触发加载操作
    private float mRatioOfHeightToLoad = 1.2f;
    //阻尼系数,越大，感觉下拉时越吃力.
    private float mResistance = 1.7f;

    //是否正在被触屏
    private boolean mIsUnderTouch = false;
    private int mOffsetToKeepHeaderWhileRefreshing = -1;
    private int mOffsetToKeepFooterWhileLoading = -1;

    //////////////////////////////////////////////////////

    /**
     * 用户触屏事件按下时触发
     *
     * @param x
     * @param y
     */
    public void onPressDown(float x, float y) {
        mIsUnderTouch = true;
        mPressedPos = mCurrentPos;
        mPtLastMove.set(x, y);
    }

    /**
     * 用户触屏事件提起时触发
     *
     * @param x
     * @param y
     */
    public final void onMove(float x, float y) {
        float offsetX = x - mPtLastMove.x;
        float offsetY = y - mPtLastMove.y;
        processOnMove(x, y, offsetX, offsetY);
        mPtLastMove.set(x, y);
    }

    /**
     * 用户触屏事件结束时触发
     */
    public void onRelease() {
        mIsUnderTouch = false;
    }

    //刷新完成会调用主要用于子类继承
    public void onUIRefreshComplete() {
    }

    //加载完成会调用主要用于子类继承
    public void onUILoadComplete() {
    }

    ///////////////////////////////////////////////////////

    //用于继承
    protected void processOnMove(float currentX, float currentY, float offsetX, float offsetY) {
        setOffset(offsetX, offsetY / mResistance);
    }


    public boolean isUnderTouch() {
        return mIsUnderTouch;
    }

    //记录上一次触摸事件触发的偏移量(Y偏移量会受阻尼系数影响)
    protected void setOffset(float x, float y) {
        mOffsetX = x;
        mOffsetY = y;
    }

    public float getOffsetX() {
        return mOffsetX;
    }

    public float getOffsetY() {
        return mOffsetY;
    }

    public float getResistance() {
        return mResistance;
    }

    public void setResistance(float resistance) {
        mResistance = resistance;
    }

    public void convertFrom(PtrIndicator ptrSlider) {
        mCurrentPos = ptrSlider.mCurrentPos;
        mLastPos = ptrSlider.mLastPos;
        mHeaderHeight = ptrSlider.mHeaderHeight;
        mFooterHeight = ptrSlider.mFooterHeight;
    }

    ////////////////////////header部分////////////////////////
    public int getHeaderHeight() {
        return mHeaderHeight;
    }

    public void setHeaderHeight(int height) {
        mHeaderHeight = height;
        mOffsetToRefresh = (int) (mRatioOfHeightToRefresh * height);
    }

    public float getRatioOfHeightToRefresh() {
        return mRatioOfHeightToRefresh;
    }

    public void setRatioOfHeightToRefresh(float ratio) {
        mRatioOfHeightToRefresh = ratio;
        mOffsetToRefresh = (int) (mHeaderHeight * ratio);
    }

    public int getOffsetToRefresh() {
        return mOffsetToRefresh;
    }

    public void setOffsetToRefresh(int offset) {
        mOffsetToRefresh = offset;
        mRatioOfHeightToRefresh = mHeaderHeight * 1f / offset;
    }

    /**
     * 刷新时展现的头部高度
     *
     * @return
     */
    public int getOffsetToKeepHeaderWhileRefreshing() {
        return mOffsetToKeepHeaderWhileRefreshing >= 0 ? mOffsetToKeepHeaderWhileRefreshing : mHeaderHeight;
    }

    public void setOffsetToKeepHeaderWhileRefreshing(int offset) {
        mOffsetToKeepHeaderWhileRefreshing = offset;
    }

    /**
     * 当前位置是否超过触发的刷新高度
     *
     * @return
     */
    public boolean isOverOffsetToRefresh() {
        return mCurrentPos >= getOffsetToRefresh();
    }

    /**
     * 当前位置是否超过刷新保持高度
     *
     * @return
     */
    public boolean isOverOffsetToKeepHeaderWhileRefreshing() {
        return mCurrentPos > getOffsetToKeepHeaderWhileRefreshing();
    }

    /**
     * 当前位置是否是刷新保持高度
     *
     * @return
     */
    public boolean isEqualOffsetToKeepHeaderWhileRefreshing() {
        return mCurrentPos == getOffsetToKeepHeaderWhileRefreshing();
    }

    /**
     * 从上往下滑刚经过刷新高度
     *
     * @return
     */
    public boolean crossRefreshLineFromTopToBottom() {
        return mLastPos < getOffsetToRefresh() && mCurrentPos >= getOffsetToRefresh();
    }

    /**
     * 从上往下滑刚经过头部高度
     *
     * @return
     */
    public boolean hasJustReachedHeaderHeightFromTopToBottom() {
        return mLastPos < mHeaderHeight && mCurrentPos >= mHeaderHeight;
    }

    ////////////////////////footer部分////////////////////////
    public int getFooterHeight() {
        return mFooterHeight;
    }

    public void setFooterHeight(int height) {
        mFooterHeight = height;
        mOffsetToLoad = (int) (mRatioOfHeightToLoad * height);
    }

    public float getRatioOfHeightToLoad() {
        return mRatioOfHeightToLoad;
    }

    public void setRatioOfHeightToLoad(float ratio) {
        mRatioOfHeightToLoad = ratio;
        mOffsetToLoad = (int) (mFooterHeight * ratio);
    }

    public int getOffsetToLoad() {
        return mOffsetToLoad;
    }

    public void setOffsetToLoad(int offset) {
        mOffsetToLoad = offset;
        mRatioOfHeightToLoad = mFooterHeight * 1f / offset;
    }

    /**
     * 加载时展现的脚部高度
     *
     * @return
     */
    public int getOffsetToKeepFooterWhileLoading() {
        return mOffsetToKeepFooterWhileLoading >= 0 ? mOffsetToKeepFooterWhileLoading : mFooterHeight;
    }

    public void setOffsetToKeepFooterWhileLoading(int offset) {
        mOffsetToKeepFooterWhileLoading = offset;
    }

    /**
     * 当前位置是否超过触发加载的高度
     */
    public boolean isOverOffsetToLoad() {
        return -mCurrentPos >= getOffsetToLoad();
    }

    /**
     * 当前位置是否超过加载保持高度
     *
     * @return
     */
    public boolean isOverOffsetToKeepFooterWhileLoading() {
        return -mCurrentPos > getOffsetToKeepFooterWhileLoading();
    }

    /**
     * 当前位置是否是加载保持高度
     *
     * @return
     */
    public boolean isEqualOffsetToKeepFooterWhileLoading() {
        return -mCurrentPos == getOffsetToKeepFooterWhileLoading();
    }

    /**
     * 从下向上滑刚经过加载高度
     *
     * @return
     */
    public boolean crossLoadLineFromBottomToTop() {
        return -mLastPos < getOffsetToLoad() && -mCurrentPos >= getOffsetToLoad();
    }

    /**
     * 从下向上滑刚经过脚部高度
     *
     * @return
     */
    public boolean hasJustReachedFooterHeightFromBottomToTop() {
        return -mLastPos < mFooterHeight && -mCurrentPos >= mFooterHeight;
    }

    ////////////////////////位置计算和判断////////////////////////
    public int getLastPos() {
        return mLastPos;
    }

    public int getCurrentPos() {
        return mCurrentPos;
    }

    public final void setCurrentPos(int current) {
        mLastPos = mCurrentPos;
        mCurrentPos = current;
    }

    public boolean isInStartPosition() {
        return mCurrentPos == POS_START;
    }

    /**
     * 头部是否正在显示
     */
    public boolean hasLeftStartPosition() {
        return mCurrentPos > POS_START;
    }

    /**
     * 脚部是否正在显示
     *
     * @return
     */
    public boolean hasRightStartPosition() {
        return mCurrentPos < POS_START;
    }

    /**
     * 是否刚离开起始位置
     *
     * @return true表示刚离开起始位置
     */
    public boolean hasJustAwayStartPosition() {
        return mLastPos == POS_START;
    }

    /**
     * 是否刚回到起点
     *
     * @return
     */
    public boolean hasJustBackToStartPosition() {
        return mLastPos != POS_START && isInStartPosition();
    }

    /**
     * 是否从头部出现然后移回到起点
     *
     * @return
     */
    public boolean hasJustLeftBackToStartPosition() {
        return mLastPos > POS_START && isInStartPosition();
    }

    /**
     * 是否从脚部出现然后回到起点
     *
     * @return
     */
    public boolean hasJustRightBackToStartPosition() {
        return mLastPos < POS_START && isInStartPosition();
    }

    /**
     * 是否越过了0这个边界
     */
    public boolean willOverTop(int to) {
        if (mCurrentPos > 0) {
            return to < POS_START;
        } else if (mCurrentPos < 0) {
            return to > POS_START;
        }
        return false;
    }

    public boolean isAlreadyHere(int to) {
        return mCurrentPos == to;
    }

    /**
     * 再最近一次按下事件后有没有被移动过.
     */
    public boolean hasMovedAfterPressedDown() {
        return mCurrentPos != mPressedPos;
    }
}
