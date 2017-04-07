package com.daliammao.ptr;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.TextView;

import com.daliammao.ptr.handler.PtrHandler;
import com.daliammao.ptr.handler.PtrUIFooterHandler;
import com.daliammao.ptr.handler.PtrUIHandler;
import com.daliammao.ptr.handler.PtrUIHeaderHandler;
import com.daliammao.ptr.handler.holder.PtrUIHandlerHolder;
import com.daliammao.ptr.indicator.PtrIndicator;

/**
 * @author: zhoupengwei
 * @time:15/12/2-上午10:24
 * @Email: 496946423@qq.com
 * @desc: 这个下拉刷新支持所有的view, 你可以包含所有你想要包含的.
 * 实现 {@link PtrUIHandler}, 便可以实现自定义用户界面.
 */
public class PtrFrameLayout extends ViewGroup {
    // status enum
    //普通状态
    public final static byte PTR_STATUS_INIT = 1;
    //满足条件,可以刷新
    public final static byte PTR_STATUS_PREPARE_REFRESH = 2;
    //正在刷新
    public final static byte PTR_STATUS_REFRESHING = 3;
    //满足条件,可以加载
    public final static byte PTR_STATUS_PREPARE_LOAD = 4;
    //正在加载
    public final static byte PTR_STATUS_LOADING = 5;
    //完成刷新
    public final static byte PTR_STATUS_COMPLETE = 6;

    private byte mStatus = PTR_STATUS_INIT;

    // 自动更新(加载)状态值
    //马上自动刷新
    private static byte FLAG_AUTO_REFRESH_AT_ONCE = 0x01;
    //晚点自动刷新
    private static byte FLAG_AUTO_REFRESH_BUT_LATER = 0x01 << 1;
    //马上自动加载
    private static byte FLAG_AUTO_LOAD_AT_ONCE = 0x01 << 2;
    //晚点自动刷新
    private static byte FLAG_AUTO_LOAD_BUT_LATER = 0x01 << 3;
    //内容View原地不动
    private static byte FLAG_PIN_CONTENT = 0x01 << 4;
    //头部View原地不动
    private static byte FLAG_PIN_HEADER = 0x01 << 5;
    //脚部View原地不动
    private static byte FLAG_PIN_FOOTER = 0x01 << 6;
    private static byte MASK_AUTO_REFRESH = 0x03;
    private static byte MASK_AUTO_LOAD = 0x0C;
    private int mFlag = 0x00;

    private PtrIndicator mPtrIndicator;

    // 可选配置在XML文件中定义标题和内容
    private int mHeaderId = 0;
    private int mContainerId = 0;
    private int mFooterId = 0;

    private View mHeaderView;
    private View mContainerView;
    private View mFooterView;
    //保存头部需要触发的事件的链表
    private PtrUIHandlerHolder mPtrUIHeaderHandlerHolder = PtrUIHandlerHolder.create();
    //保存头部需要触发的事件的链表
    private PtrUIHandlerHolder mPtrUIFooterHandlerHolder = PtrUIHandlerHolder.create();
    //封装组件对外的一些接口
    private PtrHandler mPtrHandler;

    private OnPositionChangeListener mOnPositionChangeListener;

    // config
    //回弹延时,默认 200ms，回弹到刷新高度所用时间
    private int mDurationToClose = 200;
    //头部回弹时间
    private int mDurationToCloseHeaderOrFooter = 1000;
    //刷新是否保持头部
    private boolean mKeepHeaderWhenRefresh = true;
    //刷新是否保持头部
    private boolean mKeepFooterWhenLoad = true;
    //下拉刷新/释放刷新,默认为释放刷新
    private boolean mPullOrRelease = false;
    //在下拉过程中是否考虑横向滑动(对于viewpager非常有用)
    private boolean mDisableWhenHorizontalMove = false;

    // working parameters
    private ScrollChecker mScrollChecker;
    //水平滑动距离大于该值才可以触发分页
    private int mPagingTouchSlop;

    //记录本次触屏事件是否为横向
    private boolean mPreventForHorizontal = false;
    //记录本次触屏事件过程中有没有发送取消事件
    private boolean mHasSendCancelEvent = false;
    //记录最后一次移动的事件
    private MotionEvent mLastMoveEvent;
    //记录刷新开始的时间
    private long mRefreshingStartTime = 0;
    //最短刷新时间,开始刷新到刷新完成这段时间到最小值
    private int mRefreshingMinTime = 500;
    //记录加载开始的时间
    private long mLoadingStartTime = 0;
    //最短加载时间,开始加载到加载结束这段时间的最小值
    private int mLoadingMinTime = 500;
    //如果未达到最小刷新或加载时间,则postDelay这个Runnable来延长时间
    private Runnable mPerformActionCompleteDelay = new Runnable() {
        @Override
        public void run() {
            performActionComplete();
        }
    };

    public PtrFrameLayout(Context context) {
        this(context, null);
    }

    public PtrFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PtrFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mPtrIndicator = new PtrIndicator();

        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.PtrFrameLayout, 0, 0);
        if (arr != null) {

            mHeaderId = arr.getResourceId(R.styleable.PtrFrameLayout_ptr_header, mHeaderId);
            mContainerId = arr.getResourceId(R.styleable.PtrFrameLayout_ptr_content, mContainerId);
            mFooterId = arr.getResourceId(R.styleable.PtrFrameLayout_ptr_footer, mFooterId);

            mPtrIndicator.setResistance(
                    arr.getFloat(R.styleable.PtrFrameLayout_ptr_resistance, mPtrIndicator.getResistance()));

            mDurationToClose = arr.getInt(R.styleable.PtrFrameLayout_ptr_duration_to_close, mDurationToClose);
            mDurationToCloseHeaderOrFooter = arr.getInt(R.styleable.PtrFrameLayout_ptr_duration_to_close_header_or_footer, mDurationToCloseHeaderOrFooter);

            float ratio = mPtrIndicator.getRatioOfHeightToRefresh();
            ratio = arr.getFloat(R.styleable.PtrFrameLayout_ptr_ratio_of_header_height_to_refresh, ratio);
            mPtrIndicator.setRatioOfHeightToRefresh(ratio);

            ratio = mPtrIndicator.getRatioOfHeightToLoad();
            ratio = arr.getFloat(R.styleable.PtrFrameLayout_ptr_ratio_of_footer_height_to_load, ratio);
            mPtrIndicator.setRatioOfHeightToLoad(ratio);

            mKeepHeaderWhenRefresh = arr.getBoolean(R.styleable.PtrFrameLayout_ptr_keep_header_when_refresh, mKeepHeaderWhenRefresh);

            arr.recycle();
        }

        mScrollChecker = new ScrollChecker();

        final ViewConfiguration conf = ViewConfiguration.get(getContext());
        mPagingTouchSlop = conf.getScaledTouchSlop() / 4;
    }

    /**
     * 当View中所有的子控件均被映射成xml后触发
     */
    @Override
    protected void onFinishInflate() {
        final int childCount = getChildCount();
        if (childCount > 3) {
            throw new IllegalStateException("PtrFrameLayout only can host 3 elements");
        } else if (childCount == 3 || childCount == 2) {
            if (mHeaderId != 0 && mHeaderView == null) {
                mHeaderView = findViewById(mHeaderId);
                if (mHeaderView instanceof PtrUIHeaderHandler) {
                    addPtrUIHeaderHandler((PtrUIHeaderHandler) mHeaderView);
                }
            }
            if (mContainerId != 0 && mContainerView == null) {
                mContainerView = findViewById(mContainerId);
            }
            if (mFooterId != 0 && mFooterView == null) {
                mFooterView = findViewById(mFooterId);
            }

            if (mContainerView == null || mHeaderView == null || mFooterView == null) {
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    if (child instanceof PtrUIHeaderHandler) {
                        if (mHeaderView == null) {
                            mHeaderView = child;
                            addPtrUIHeaderHandler((PtrUIHeaderHandler) mHeaderView);
                        }
                    } else if (child instanceof PtrUIFooterHandler) {
                        if (mFooterView == null) {
                            mFooterView = child;
                            addPtrUIFooterHandler((PtrUIFooterHandler) mFooterView);
                        }
                    } else {
                        if (mContainerView == null) {
                            mContainerView = child;
                        }
                    }
                }
            }
        } else if (childCount == 1) {
            mContainerView = getChildAt(0);
        } else {
            TextView errorView = new TextView(getContext());
            errorView.setClickable(true);
            errorView.setTextColor(0xffff6600);
            errorView.setGravity(Gravity.CENTER);
            errorView.setTextSize(20);
            errorView.setText("The content view in PtrFrameLayout is empty. Do you forget to specify its id in xml layout file?");
            mContainerView = errorView;
            addView(mContainerView);
        }
        if (mHeaderView != null) {
            mHeaderView.bringToFront();
        }
        if (mFooterView != null) {
            mFooterView.bringToFront();
        }
        super.onFinishInflate();
    }

    /**
     * 销毁View的时候调用
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mScrollChecker != null) {
            mScrollChecker.destroy();
        }

        if (mPerformActionCompleteDelay != null) {
            removeCallbacks(mPerformActionCompleteDelay);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mHeaderView != null) {
            measureChildWithMargins(mHeaderView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = (MarginLayoutParams) mHeaderView.getLayoutParams();
            int mHeaderHeight = mHeaderView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            mPtrIndicator.setHeaderHeight(mHeaderHeight);
        }

        if (mContainerView != null) {
            measureChildWithMargins(mContainerView, widthMeasureSpec, 0, heightMeasureSpec, 0);
        }

        if (mFooterView != null) {
            measureChildWithMargins(mFooterView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = (MarginLayoutParams) mFooterView.getLayoutParams();
            int mFooterHeight = mFooterView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            mPtrIndicator.setFooterHeight(mFooterHeight);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int offsetY = mPtrIndicator.getCurrentPos();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        //例如正在下拉时,onLayout被调用,需要考虑offsetY这个偏移量,但mHeaderView只要考虑头部出现的情况下才计算偏移,
        //所以加入offsetY>=0这个条件
        if (mHeaderView != null && offsetY >= 0) {
            int headerOffsetY = offsetY;
            if (isPinHeader()) {
                headerOffsetY = mPtrIndicator.getHeaderHeight();
            }
            MarginLayoutParams lp = (MarginLayoutParams) mHeaderView.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = paddingTop + lp.topMargin + headerOffsetY - mPtrIndicator.getHeaderHeight();
            final int right = left + mHeaderView.getMeasuredWidth();
            final int bottom = top + mHeaderView.getMeasuredHeight();
            mHeaderView.layout(left, top, right, bottom);
        }
        if (mContainerView != null) {
            int containerOffsetY = offsetY;
            if (isPinContent()) {
                containerOffsetY = 0;
            }
            MarginLayoutParams lp = (MarginLayoutParams) mContainerView.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = paddingTop + lp.topMargin + containerOffsetY;
            final int right = left + mContainerView.getMeasuredWidth();
            final int bottom = top + mContainerView.getMeasuredHeight();
            mContainerView.layout(left, top, right, bottom);
        }
        if (mFooterView != null && offsetY <= 0) {
            int footerOffsetY = offsetY;
            if (isPinFooter()) {
                footerOffsetY = -mPtrIndicator.getFooterHeight();
            }
            MarginLayoutParams lp = (MarginLayoutParams) mFooterView.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = getMeasuredHeight() + footerOffsetY - paddingBottom;
            final int right = left + mFooterView.getMeasuredWidth();
            final int bottom = top + mFooterView.getMeasuredHeight();
            mFooterView.layout(left, top, right, bottom);
        }
    }

    public boolean dispatchTouchEventSuper(MotionEvent e) {
        return super.dispatchTouchEvent(e);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        if (!isEnabled() || mContainerView == null || (mHeaderView == null && mFooterView == null)) {
            return dispatchTouchEventSuper(e);
        }
        int action = e.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mPtrIndicator.onRelease();
                if (!mPtrIndicator.isInStartPosition()) {
                    onRelease(false);
                    if (mPtrIndicator.hasMovedAfterPressedDown()) {
                        sendCancelEvent();
                        return true;
                    }
                    return dispatchTouchEventSuper(e);
                } else {
                    return dispatchTouchEventSuper(e);
                }

            case MotionEvent.ACTION_DOWN:
                mHasSendCancelEvent = false;
                mPtrIndicator.onPressDown(e.getX(), e.getY());
                //触屏事件重新开始,停止并初始化ScrollChecker
                mScrollChecker.abortIfWorking();

                //记录是否为横向移动
                //初始化为false,move事件如果判断为横向移动则设为true,并交给父类处理事件.
                mPreventForHorizontal = false;
                dispatchTouchEventSuper(e);
                return true;

            case MotionEvent.ACTION_MOVE:
                mLastMoveEvent = e;
                mPtrIndicator.onMove(e.getX(), e.getY());
                float offsetX = mPtrIndicator.getOffsetX();
                float offsetY = mPtrIndicator.getOffsetY();
                //判断横向滑动不触发上下滑动
                //用户设置需要处理横向滑动(mDisableWhenHorizontalMove)
                //已经判断过而且结果为true就无需再判断(mPreventForHorizontal)
                //短时间内滑动了足够的横向距离((Math.abs(offsetX) > mPagingTouchSlop)
                //横向偏移量>纵向偏移量(Math.abs(offsetX) > Math.abs(offsetY))
                if (mDisableWhenHorizontalMove && !mPreventForHorizontal && (Math.abs(offsetX) > mPagingTouchSlop && Math.abs(offsetX) > Math.abs(offsetY))) {
                    //如果没有显示头部和脚部则处理横向移动
                    if (mPtrIndicator.isInStartPosition()) {
                        mPreventForHorizontal = true;
                    }
                }

                if (mPreventForHorizontal) {
                    return dispatchTouchEventSuper(e);
                }

                boolean moveDown = offsetY > 0;
                boolean moveUp = !moveDown;

                //判断mStatus!=PTR_STATUS_LOADING是为了避免正在加载状态下还能下拉进行刷新
                if (mHeaderView != null && mStatus != PTR_STATUS_LOADING) {
                    // 正在向下滑,但用户反馈不能下拉刷新,则跳过
                    if (moveDown && mPtrHandler != null && !mPtrHandler.checkCanDoDownRefresh(this, mContainerView, mHeaderView)) {
                        //do nothing
                    } else {
                        //true表示头部在显示,可以向上滑隐藏头部
                        boolean canMoveUp = mPtrIndicator.hasLeftStartPosition();
                        //头部已经显示而且正在向上滑或正在向下滑,则更新头部的位置
                        if ((moveUp && canMoveUp) || moveDown) {
                            movePos(offsetY);
                            return true;
                        }
                    }
                }
                //判断mStatus!=PTR_STATUS_REFRESHING是为了避免正在刷新状态下还能上拉进行加载
                if (mFooterView != null && mStatus != PTR_STATUS_REFRESHING) {
                    // 正在向上滑,但用户反馈不能上拉加载,则跳过
                    if (moveUp && mPtrHandler != null && !mPtrHandler.checkCanDoUpLoad(this, mContainerView, mFooterView)) {
                        //do nothing
                    } else {
                        //事件运行到这里,表示向下滑可以显示头部,向上滑可以显示脚部

                        //true表示脚部在显示,可以向下滑隐藏脚部
                        boolean canMoveDown = mPtrIndicator.hasRightStartPosition();
                        //脚部已经显示而且正在向下滑或正在向上滑,则更新脚部的位置
                        if ((moveDown && canMoveDown) || moveUp) {
                            movePos(offsetY);
                            return true;
                        }
                    }
                }
                return dispatchTouchEventSuper(e);
        }
        return dispatchTouchEventSuper(e);
    }

    /**
     * 移动容器View,大于0向下移,小于0向上移动
     * 在这个更新PtrIndicator记录的位置,并且派出位移越过0的情况
     *
     * @param deltaY
     */
    private void movePos(float deltaY) {
        int to = mPtrIndicator.getCurrentPos() + (int) deltaY;

        // 是否越过0,如果是则to=0
        if (mPtrIndicator.willOverTop(to)) {
            to = PtrIndicator.POS_START;
        }

        mPtrIndicator.setCurrentPos(to);
        int change = to - mPtrIndicator.getLastPos();
        updatePos(change);
    }

    private void updatePos(int change) {
        if (change == 0) {
            return;
        }
        //手指是否还在屏幕上
        boolean isUnderTouch = mPtrIndicator.isUnderTouch();

        // 一旦移动, 就给孩子发送取消触屏事件
        if (isUnderTouch && !mHasSendCancelEvent && mPtrIndicator.hasMovedAfterPressedDown()) {
            mHasSendCancelEvent = true;
            sendCancelEvent();
        }

        // 刚离开起始的位置
        if ((mPtrIndicator.hasJustAwayStartPosition() && mStatus == PTR_STATUS_INIT)) {

            if (mPtrIndicator.hasLeftStartPosition()) {
                mStatus = PTR_STATUS_PREPARE_REFRESH;
                mPtrUIHeaderHandlerHolder.onUIPrepare(this);
            } else {
                mStatus = PTR_STATUS_PREPARE_LOAD;
                mPtrUIFooterHandlerHolder.onUIPrepare(this);
            }
        }

        // 回到起始的位置
        if (mPtrIndicator.hasJustBackToStartPosition()) {
            tryToNotifyReset();
            // 恢复孩子的事件
            if (isUnderTouch) {
                sendDownEvent();
            }
        }

        if (mStatus == PTR_STATUS_PREPARE_REFRESH || mStatus == PTR_STATUS_PREPARE_LOAD) {
            // 触发释放刷行
            // 手指在屏幕上
            // 不是自动刷新或自动加载
            // 位置达到释放刷新的高度或加载的高度
            if (isUnderTouch && (!isAutoRefresh() && !isAutoLoad()) && mPullOrRelease
                    && (mPtrIndicator.crossRefreshLineFromTopToBottom() || mPtrIndicator.crossLoadLineFromBottomToTop())) {
                //触发下拉刷新和上拉加载
                tryToPerformRefreshOrLoad();
            }
            // 到达头部高度同时是自动刷新或者到达脚部高度同时是自动加载
            if ((isAutoRefreshButLater() && mPtrIndicator.hasJustReachedHeaderHeightFromTopToBottom())
                    || (isAutoLoadButLater() && mPtrIndicator.hasJustReachedFooterHeightFromBottomToTop())) {
                tryToPerformRefreshOrLoad();
            }
        }

        boolean updateHeader = false;
        boolean updateFooter = false;

        if (mPtrIndicator.hasLeftStartPosition() || mPtrIndicator.hasJustLeftBackToStartPosition()) {
            //如果位置大于0或刚从大于0到等于0
            updateHeader = true;
            if (!isPinHeader()) {
                mHeaderView.offsetTopAndBottom(change);
            }
        } else if (mPtrIndicator.hasRightStartPosition() || mPtrIndicator.hasJustRightBackToStartPosition()) {
            //如果位置小于0或刚从小于0到等于0
            updateFooter = true;
            if (!isPinFooter()) {
                mFooterView.offsetTopAndBottom(change);
            }
        }
        if (!isPinContent()) {
            mContainerView.offsetTopAndBottom(change);
        }
        invalidate();

        if (updateHeader && mPtrUIHeaderHandlerHolder.hasHandler()) {
            mPtrUIHeaderHandlerHolder.onUIPositionChange(this, isUnderTouch, mStatus, mPtrIndicator);
            if (mOnPositionChangeListener != null) {
                mOnPositionChangeListener.onPositionChange(this, isUnderTouch, mStatus, mPtrIndicator);
            }
        } else if (updateFooter && mPtrUIFooterHandlerHolder.hasHandler()) {
            mPtrUIFooterHandlerHolder.onUIPositionChange(this, isUnderTouch, mStatus, mPtrIndicator);
            if (mOnPositionChangeListener != null) {
                mOnPositionChangeListener.onPositionChange(this, isUnderTouch, mStatus, mPtrIndicator);
            }
        }
    }

    /**
     * 触发刷新或加载
     *
     * @param stayForLoading true 如果可以触发刷新,保留在当前位置刷新
     */
    private void onRelease(boolean stayForLoading) {

        tryToPerformRefreshOrLoad();

        if (mStatus == PTR_STATUS_REFRESHING) {
            // 刷新是否保持头部
            if (mKeepHeaderWhenRefresh) {
                // 超过刷新时展示头部高度而且参数要求不保留在原位置
                if (mPtrIndicator.isOverOffsetToKeepHeaderWhileRefreshing() && !stayForLoading) {
                    mScrollChecker.tryToScrollTo(mPtrIndicator.getOffsetToKeepHeaderWhileRefreshing(), mDurationToClose);
                }
            } else {
                tryScrollBackToTopWhileLoading();
            }
        } else if (mStatus == PTR_STATUS_LOADING) {
            if (mKeepFooterWhenLoad) {
                if (mPtrIndicator.isOverOffsetToKeepFooterWhileLoading() && !stayForLoading) {
                    mScrollChecker.tryToScrollTo(-mPtrIndicator.getOffsetToKeepFooterWhileLoading(), mDurationToClose);
                }
            } else {
                tryScrollBackToTopWhileLoading();
            }
        } else {
            tryScrollBackToTopAbortRefresh();
        }
    }

    /**
     * 如果在顶部和未装载，复位
     */
    private boolean tryToNotifyReset() {
        if ((mStatus == PTR_STATUS_COMPLETE || mStatus == PTR_STATUS_PREPARE_REFRESH || mStatus == PTR_STATUS_PREPARE_LOAD)
                && mPtrIndicator.isInStartPosition()) {
            if (mPtrIndicator.getLastPos() > 0 && mPtrUIHeaderHandlerHolder.hasHandler()) {
                mPtrUIHeaderHandlerHolder.onUIReset(this);
            } else if (mPtrIndicator.getLastPos() < 0 && mPtrUIFooterHandlerHolder.hasHandler()) {
                mPtrUIFooterHandlerHolder.onUIReset(this);
            }
            mStatus = PTR_STATUS_INIT;
            clearFlag();
            return true;
        }
        return false;
    }

    /**
     * 如果没有在触摸则滚动到起始位置
     */
    private void tryScrollBackToTop() {
        if (!mPtrIndicator.isUnderTouch()) {
            mScrollChecker.tryToScrollTo(PtrIndicator.POS_START, mDurationToCloseHeaderOrFooter);
        }
    }

    /**
     * just make easier to understand
     */
    private void tryScrollBackToTopWhileLoading() {
        tryScrollBackToTop();
    }

    /**
     * just make easier to understand
     */
    private void tryScrollBackToTopAfterComplete() {
        tryScrollBackToTop();
    }

    /**
     * just make easier to understand
     */
    private void tryScrollBackToTopAbortRefresh() {
        tryScrollBackToTop();
    }

    /**
     * 判断是否满足刷新条件,满足则改变状态,并触发PtrUIHandlerHolder和PtrHandler相应函数
     *
     * @return
     */
    private boolean tryToPerformRefreshOrLoad() {
        if (mStatus != PTR_STATUS_PREPARE_REFRESH && mStatus != PTR_STATUS_PREPARE_LOAD) {
            return false;
        }

        //满足刷新条件
        if (((mPtrIndicator.isEqualOffsetToKeepHeaderWhileRefreshing()) && isAutoRefresh()) || mPtrIndicator.isOverOffsetToRefresh()) {
            mStatus = PTR_STATUS_REFRESHING;
            performRefresh();
            return false;
        }
        //满足加载条件
        if (((mPtrIndicator.isEqualOffsetToKeepFooterWhileLoading()) && isAutoLoad()) || mPtrIndicator.isOverOffsetToLoad()) {
            mStatus = PTR_STATUS_LOADING;
            performLoad();
            return false;
        }
        return false;
    }

    private void performRefresh() {
        mRefreshingStartTime = System.currentTimeMillis();
        if (mPtrUIHeaderHandlerHolder.hasHandler()) {
            mPtrUIHeaderHandlerHolder.onUIBegin(this);
        }

        if (mPtrHandler != null) {
            mPtrHandler.onRefreshBegin(this);
        }
    }

    private void performLoad() {
        mLoadingStartTime = System.currentTimeMillis();
        if (mPtrUIFooterHandlerHolder.hasHandler()) {
            mPtrUIFooterHandlerHolder.onUIBegin(this);
        }
        if (mPtrHandler != null) {
            mPtrHandler.onLoadBegin(this);
        }
    }

    private void performActionComplete() {
        int mLastStatus = mStatus;
        mStatus = PTR_STATUS_COMPLETE;

        if (mLastStatus == PTR_STATUS_REFRESHING) {
            notifyUIRefreshComplete();
        } else if (mLastStatus == PTR_STATUS_LOADING) {
            notifyUILoadComplete();
        }

    }

    /**
     * 通知刷新完成
     */
    private void notifyUIRefreshComplete() {
        if (mPtrUIHeaderHandlerHolder.hasHandler()) {
            mPtrUIHeaderHandlerHolder.onUIComplete(this);
        }
        mPtrIndicator.onUIRefreshComplete();
        tryScrollBackToTopAfterComplete();
        tryToNotifyReset();
    }

    /**
     * 通知加载完成
     */
    private void notifyUILoadComplete() {
        if (mPtrUIFooterHandlerHolder.hasHandler()) {
            mPtrUIFooterHandlerHolder.onUIComplete(this);
        }
        mPtrIndicator.onUILoadComplete();
        tryScrollBackToTopAfterComplete();
        tryToNotifyReset();
    }

    private void clearFlag() {
        mFlag &= ~MASK_AUTO_REFRESH;
        mFlag &= ~MASK_AUTO_LOAD;
    }

    public boolean isAutoRefresh() {
        return (mFlag & MASK_AUTO_REFRESH) > 0;
    }

    private boolean isAutoRefreshButLater() {
        return (mFlag & FLAG_AUTO_REFRESH_BUT_LATER) > 0;
    }

    public boolean isAutoLoad() {
        return (mFlag & MASK_AUTO_LOAD) > 0;
    }

    private boolean isAutoLoadButLater() {
        return (mFlag & FLAG_AUTO_LOAD_BUT_LATER) > 0;
    }

    public boolean isPinContent() {
        return (mFlag & FLAG_PIN_CONTENT) > 0;
    }

    public boolean isPinHeader() {
        return (mFlag & FLAG_PIN_HEADER) > 0;
    }

    public boolean isPinFooter() {
        return (mFlag & FLAG_PIN_FOOTER) > 0;
    }

    /**
     * 设为真,表示内容视图原地不动
     *
     * @param pinContent
     */
    public void setPinContent(boolean pinContent) {
        if (pinContent) {
            mFlag = mFlag | FLAG_PIN_CONTENT;
        } else {
            mFlag = mFlag & ~FLAG_PIN_CONTENT;
        }
    }

    /**
     * 设为真,表示头部原地不动
     *
     * @param pinHeader
     */
    public void setPinHeader(boolean pinHeader) {
        if (pinHeader) {
            mFlag = mFlag | FLAG_PIN_HEADER;
        } else {
            mFlag = mFlag & ~FLAG_PIN_HEADER;
        }
    }

    /**
     * 设置真,表示脚部原地不动
     *
     * @param pinFooter
     */
    public void setPinFooter(boolean pinFooter) {
        if (pinFooter) {
            mFlag = mFlag | FLAG_PIN_FOOTER;
        } else {
            mFlag = mFlag & ~FLAG_PIN_FOOTER;
        }
    }

    public void autoRefresh() {
        autoRefresh(false, mDurationToCloseHeaderOrFooter);
    }

    public void autoRefresh(boolean atOnce) {
        autoRefresh(atOnce, mDurationToCloseHeaderOrFooter);
    }

    public void autoRefresh(boolean atOnce, int duration) {

        if (mStatus != PTR_STATUS_INIT || mHeaderView == null) {
            return;
        }

        mFlag |= atOnce ? FLAG_AUTO_REFRESH_AT_ONCE : FLAG_AUTO_REFRESH_BUT_LATER;

        mStatus = PTR_STATUS_PREPARE_REFRESH;
        if (mPtrUIHeaderHandlerHolder.hasHandler()) {
            mPtrUIHeaderHandlerHolder.onUIPrepare(this);
        }
        mScrollChecker.tryToScrollTo(mPtrIndicator.getOffsetToKeepHeaderWhileRefreshing(), duration);
        if (atOnce) {
            mStatus = PTR_STATUS_REFRESHING;
            performRefresh();
        }
    }

    public void autoRefreshPost() {
        post(new Runnable() {
            @Override
            public void run() {
                autoRefresh();
            }
        });
    }

    public void autoRefreshPost(final boolean atOnce) {
        post(new Runnable() {
            @Override
            public void run() {
                autoRefresh(atOnce);
            }
        });
    }

    public void autoRefreshPost(final boolean atOnce, final int duration) {
        post(new Runnable() {
            @Override
            public void run() {
                autoRefresh(atOnce, duration);
            }
        });
    }

    public void autoLoad() {
        autoLoad(true, mDurationToCloseHeaderOrFooter);
    }

    public void autoLoad(boolean atOnce) {
        autoLoad(atOnce, mDurationToCloseHeaderOrFooter);
    }

    public void autoLoad(boolean atOnce, int duration) {

        if (mStatus != PTR_STATUS_INIT || mFooterView == null) {
            return;
        }

        mFlag |= atOnce ? FLAG_AUTO_LOAD_AT_ONCE : FLAG_AUTO_LOAD_BUT_LATER;

        mStatus = PTR_STATUS_PREPARE_LOAD;
        if (mPtrUIFooterHandlerHolder.hasHandler()) {
            mPtrUIFooterHandlerHolder.onUIPrepare(this);
        }
        mScrollChecker.tryToScrollTo(-mPtrIndicator.getOffsetToKeepFooterWhileLoading(), duration);
        if (atOnce) {
            mStatus = PTR_STATUS_LOADING;
            performLoad();
        }
    }

    public void autoLoadPost() {
        post(new Runnable() {
            @Override
            public void run() {
                autoLoad();
            }
        });
    }

    public void autoLoadPost(final boolean atOnce) {
        post(new Runnable() {
            @Override
            public void run() {
                autoLoad(atOnce);
            }
        });
    }

    public void autoLoadPost(final boolean atOnce, final int duration) {
        post(new Runnable() {
            @Override
            public void run() {
                autoLoad(atOnce, duration);
            }
        });
    }

    /**
     * 刷新完成
     */
    final public void refreshComplete() {
        if (mStatus != PTR_STATUS_REFRESHING) {
            return;
        }
        int delay = (int) (mRefreshingMinTime - (System.currentTimeMillis() - mRefreshingStartTime));
        if (delay <= 0) {
            performActionComplete();
        } else {
            postDelayed(mPerformActionCompleteDelay, delay);
        }
    }

    /**
     * 加载完成
     */
    final public void loadComplete() {
        if (mStatus != PTR_STATUS_LOADING) {
            return;
        }
        int delay = (int) (mLoadingMinTime - (System.currentTimeMillis() - mLoadingStartTime));
        if (delay <= 0) {
            performActionComplete();
        } else {
            postDelayed(mPerformActionCompleteDelay, delay);
        }
    }

    protected void onPtrScrollAbort() {
        if ((mPtrIndicator.hasLeftStartPosition() && isAutoRefresh())
                || (mPtrIndicator.hasRightStartPosition() && isAutoLoad())) {
            onRelease(true);
        }
    }

    protected void onPtrScrollFinish() {
        if ((mPtrIndicator.hasLeftStartPosition() && isAutoRefresh())
                || (mPtrIndicator.hasRightStartPosition() && isAutoLoad())) {
            onRelease(true);
        }
    }


    public View getHeaderView() {
        return mHeaderView;
    }

    public void setHeaderView(View header) {
        if (mHeaderView != null && header != null && mHeaderView != header) {
            removeView(mHeaderView);
        }
        ViewGroup.LayoutParams lp = header.getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams(-1, -2);
            header.setLayoutParams(lp);
        }
        mHeaderView = header;
        addView(header);
    }

    public View getFooterView() {
        return mFooterView;
    }

    public void setFooterView(View footer) {
        if (mFooterView != null && footer != null && mFooterView != footer) {
            removeView(mFooterView);
        }
        ViewGroup.LayoutParams lp = footer.getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams(-1, -2);
            footer.setLayoutParams(lp);
        }
        mFooterView = footer;
        addView(footer);
    }

    public View getContentView() {
        return mContainerView;
    }

    public void setPtrHandler(PtrHandler ptrHandler) {
        mPtrHandler = ptrHandler;
    }

    public void addPtrUIHeaderHandler(PtrUIHeaderHandler ptrUIHeaderHandler) {
        PtrUIHandlerHolder.addHandler(mPtrUIHeaderHandlerHolder, ptrUIHeaderHandler);
    }

    public void removePtrUIHeaderHandler(PtrUIHeaderHandler ptrUIHeaderHandler) {
        mPtrUIHeaderHandlerHolder = PtrUIHandlerHolder.removeHandler(mPtrUIHeaderHandlerHolder, ptrUIHeaderHandler);
    }

    public void addPtrUIFooterHandler(PtrUIFooterHandler ptrUIFooterHandler) {
        PtrUIHandlerHolder.addHandler(mPtrUIFooterHandlerHolder, ptrUIFooterHandler);
    }

    public void removePtrUIFooterHandler(PtrUIFooterHandler ptrUIFooterHandler) {
        mPtrUIFooterHandlerHolder = PtrUIHandlerHolder.removeHandler(mPtrUIFooterHandlerHolder, ptrUIFooterHandler);
    }

    public void setPtrIndicator(PtrIndicator slider) {
        if (mPtrIndicator != null && mPtrIndicator != slider) {
            slider.convertFrom(mPtrIndicator);
        }
        mPtrIndicator = slider;
    }

    public boolean isPullToRelease() {
        return mPullOrRelease;
    }

    public void setPullOrRelease(boolean pullOrRelease) {
        mPullOrRelease = pullOrRelease;
    }

    public float getResistance() {
        return mPtrIndicator.getResistance();
    }

    public void setResistance(float resistance) {
        mPtrIndicator.setResistance(resistance);
    }

    public float getDurationToClose() {
        return mDurationToClose;
    }

    /**
     * The duration to return back to the refresh position
     *
     * @param duration
     */
    public void setDurationToClose(int duration) {
        mDurationToClose = duration;
    }

    public long getDurationToCloseHeaderOrFooter() {
        return mDurationToCloseHeaderOrFooter;
    }

    /**
     * The duration to close time
     *
     * @param duration
     */
    public void setDurationToCloseHeaderOrFooter(int duration) {
        mDurationToCloseHeaderOrFooter = duration;
    }

    public float getRatioOfHeightToRefresh() {
        return mPtrIndicator.getRatioOfHeightToRefresh();
    }

    public void setRatioOfHeightToRefresh(float ratio) {
        mPtrIndicator.setRatioOfHeightToRefresh(ratio);
    }

    public float getRatioOfHeightToLoad() {
        return mPtrIndicator.getRatioOfHeightToLoad();
    }

    public void setRatioOfHeightToLoad(float ratio) {
        mPtrIndicator.setRatioOfHeightToLoad(ratio);
    }

    public int getOffsetToRefresh() {
        return mPtrIndicator.getOffsetToRefresh();
    }

    public void setOffsetToRefresh(int offset) {
        mPtrIndicator.setOffsetToRefresh(offset);
    }

    /**
     * 刷新时展现的头部高度
     *
     * @return
     */
    public int getOffsetToKeepHeaderWhileRefreshing() {
        return mPtrIndicator.getOffsetToKeepHeaderWhileRefreshing();
    }

    public void setOffsetToKeepHeaderWhileRefreshing(int offset) {
        mPtrIndicator.setOffsetToKeepHeaderWhileRefreshing(offset);
    }

    /**
     * 加载时展现的脚部高度
     *
     * @return
     */
    public int getOffsetToKeepFooterWhileLoading() {
        return mPtrIndicator.getOffsetToKeepFooterWhileLoading();
    }

    public void setOffsetToKeepFooterWhileLoading(int offset) {
        mPtrIndicator.setOffsetToKeepFooterWhileLoading(offset);
    }

    public int getOffsetToLoad() {
        return mPtrIndicator.getOffsetToLoad();
    }

    public void setOffsetToLoad(int offset) {
        mPtrIndicator.setOffsetToLoad(offset);
    }

    public boolean isKeepHeaderWhenRefresh() {
        return mKeepHeaderWhenRefresh;
    }

    public void setKeepHeaderWhenRefresh(boolean keepOrNot) {
        mKeepHeaderWhenRefresh = keepOrNot;
    }

    public boolean isKeepFooterWhenLoad() {
        return mKeepFooterWhenLoad;
    }

    public void setKeepFooterWhenLoad(boolean keepOrNot) {
        mKeepFooterWhenLoad = keepOrNot;
    }

    /**
     * 对于viewpage非常有用
     *
     * @param disable
     */
    public void disableWhenHorizontalMove(boolean disable) {
        mDisableWhenHorizontalMove = disable;
    }

    public void setRefreshingMinTime(int time) {
        mRefreshingMinTime = time;
    }

    public void setLoadingMinTime(int time) {
        mLoadingMinTime = time;
    }

    public int getStatus() {
        return mStatus;
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p != null && p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public void setOnPositionChangeListener(OnPositionChangeListener onPositionChangeListener) {
        this.mOnPositionChangeListener = onPositionChangeListener;
    }

    private void sendCancelEvent() {
        // The ScrollChecker will update position and lead to send cancel event when mLastMoveEvent is null.
        if (mLastMoveEvent == null) {
            return;
        }
        MotionEvent last = mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime() + ViewConfiguration.getLongPressTimeout(), MotionEvent.ACTION_CANCEL, last.getX(), last.getY(), last.getMetaState());
        dispatchTouchEventSuper(e);
    }

    private void sendDownEvent() {
        final MotionEvent last = mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime(), MotionEvent.ACTION_DOWN, last.getX(), last.getY(), last.getMetaState());
        dispatchTouchEventSuper(e);
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        @SuppressWarnings({"unused"})
        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    class ScrollChecker implements Runnable {
        //最终滑到到距离Y
        private int mLastFlingY;
        private Scroller mScroller;
        private boolean mIsRunning = false;
        private int mStart;

        public ScrollChecker() {
            mScroller = new Scroller(getContext());
        }

        @Override
        public void run() {
            boolean finish = !mScroller.computeScrollOffset() || mScroller.isFinished();
            int curY = mScroller.getCurrY();
            int deltaY = curY - mLastFlingY;

            if (!finish) {
                mLastFlingY = curY;
                movePos(deltaY);
                post(this);
            } else {
                finish();
            }
        }

        private void finish() {
            reset();
            onPtrScrollFinish();
        }

        private void reset() {
            mIsRunning = false;
            mLastFlingY = 0;
            removeCallbacks(this);
        }

        private void destroy() {
            reset();
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
        }

        //正在运行就退出
        public void abortIfWorking() {
            if (mIsRunning) {
                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
                }
                onPtrScrollAbort();
                reset();
            }
        }

        public void tryToScrollTo(int to, int duration) {
            if (mPtrIndicator.isAlreadyHere(to)) {
                return;
            }
            mStart = mPtrIndicator.getCurrentPos();
            int distance = to - mStart;
            removeCallbacks(this);

            mLastFlingY = 0;

            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
            mScroller.startScroll(0, 0, 0, distance, duration);
            post(this);
            mIsRunning = true;
        }
    }

    public interface OnPositionChangeListener {
        void onPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator);
    }
}
