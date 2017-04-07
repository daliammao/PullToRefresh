package com.daliammao.pulltorefresh.header;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.daliammao.ptr.PtrFrameLayout;
import com.daliammao.ptr.handler.PtrUIHeaderHandler;
import com.daliammao.ptr.indicator.PtrIndicator;
import com.daliammao.pulltorefresh.PtrTensionIndicator2;
import com.daliammao.pulltorefresh.R;

/**
 * @author: zhoupengwei
 * @time:16/3/4-下午2:41
 * @Email: 496946423@qq.com
 * @desc: 默认动画header
 */
public class PtrAnimationDefaultHeader extends FrameLayout implements PtrUIHeaderHandler {

    private PtrFrameLayout mPtrFrameLayout;
    private PtrTensionIndicator2 mPtrTensionIndicator;

    private AnimationDrawable mFramAnimation;
    private ImageView mAnimationView;

    //拖拽中的动画,类型为图片ID的数组
    private int[] mDragingAnimation;
    //加载中的动画,类型为drawable的animation-list的ID
    private int mLoadingAnimationId;
    //完成后的动画,类型为图片ID的数组
    private int[] mCompleteAnimation;

    //记录当前展示的图片资源id,该变量主要是为了防止重复设置相同的图片资源
    private int mTempResource = -1;
    private int mImageHeight = 0;
    private int mImageWidth = 0;

    public PtrAnimationDefaultHeader(Context context) {
        super(context);
        initViews(null);
    }

    public PtrAnimationDefaultHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(attrs);
    }

    public PtrAnimationDefaultHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews(attrs);
    }

    protected void initViews(AttributeSet attrs) {

        View header = LayoutInflater.from(getContext()).inflate(R.layout.ptr_animation_default_header, this);

        mAnimationView = (ImageView) header.findViewById(R.id.ptr_animation_header_view);

        resetView();
    }

    public void setUp(PtrFrameLayout ptrFrameLayout) {
        mPtrFrameLayout = ptrFrameLayout;
        mPtrTensionIndicator = new PtrTensionIndicator2();
        mPtrFrameLayout.setPtrIndicator(mPtrTensionIndicator);
    }

    private void resetView() {
        mAnimationView.setImageResource(0);
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {

    }

    @Override
    public void onUIPrepare(PtrFrameLayout frame) {
        mImageHeight = mAnimationView.getHeight();
        mImageWidth = mAnimationView.getWidth();
        mPtrTensionIndicator.setOffsetToKeepHeaderWhileRefreshing(mImageHeight);
    }

    @Override
    public void onUIBegin(PtrFrameLayout frame) {
        startLoadingAnimation();
    }

    @Override
    public void onUIComplete(PtrFrameLayout frame) {
        stopLoadingAnimation();
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        int imageTargetHeight = Math.max(mPtrTensionIndicator.getCurrentPos(), mImageHeight);

        if(imageTargetHeight!=mImageHeight){
            ViewGroup.LayoutParams params = mAnimationView.getLayoutParams();
            params.height = imageTargetHeight;
            mAnimationView.setLayoutParams(params);
        }

        if (mFramAnimation != null && mFramAnimation.isRunning()) {
            return;
        }

        final int toKeepHeader = ptrIndicator.getOffsetToKeepHeaderWhileRefreshing();
        final int currentPos = ptrIndicator.getCurrentPos();

        if (status == PtrFrameLayout.PTR_STATUS_PREPARE_REFRESH) {
            if (mDragingAnimation == null || mDragingAnimation.length == 0) {
                return;
            }
            final int animationSpace = toKeepHeader / mDragingAnimation.length;
            int imgIndex = currentPos / animationSpace;
            if (imgIndex >= 0 && imgIndex < mDragingAnimation.length && mTempResource != mDragingAnimation[imgIndex]) {
                mTempResource = mDragingAnimation[imgIndex];
                mAnimationView.setImageResource(mTempResource);
            }
        } else {
            if (mCompleteAnimation == null || mCompleteAnimation.length == 0) {
                return;
            }
            final int animationSpace = toKeepHeader / mCompleteAnimation.length;
            int imgIndex = mCompleteAnimation.length - 1 - currentPos / animationSpace;
            if (imgIndex >= 0 && imgIndex < mCompleteAnimation.length && mTempResource != mCompleteAnimation[imgIndex]) {
                mTempResource = mCompleteAnimation[imgIndex];
                mAnimationView.setImageResource(mTempResource);
            }
        }
    }

    private void startLoadingAnimation() {
        mAnimationView.setImageResource(mLoadingAnimationId);

        try {
            mFramAnimation = (AnimationDrawable) mAnimationView.getDrawable();
        } catch (Exception e) {
            mFramAnimation = null;
        }
        if (mFramAnimation == null) {
            return;
        }

        mFramAnimation.start();
    }

    private void stopLoadingAnimation() {
        if (mFramAnimation == null) {
            return;
        }
        mFramAnimation.stop();
    }

    public void setLoadingAnimationDrawable(int mLoadingAnimationId) {
        this.mLoadingAnimationId = mLoadingAnimationId;
        stopLoadingAnimation();
        mFramAnimation = null;
    }

    public void setDragingAnimation(int[] dragingAnimation) {
        this.mDragingAnimation = dragingAnimation;
    }

    public void seCompleteAnimationt(int[] completeAnimation) {
        this.mCompleteAnimation = completeAnimation;
    }
}
