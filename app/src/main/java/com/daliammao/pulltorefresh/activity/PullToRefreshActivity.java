package com.daliammao.pulltorefresh.activity;

import com.daliammao.ptr.PtrClassicFrameLayout;
import com.daliammao.pulltorefresh.activity.WithTextViewInFrameLayoutActivity;

/**
 * @author: zhoupengwei
 * @time:15/12/8-下午5:20
 * @Email: 496946423@qq.com
 * @desc:
 */
public class PullToRefreshActivity extends WithTextViewInFrameLayoutActivity {
    @Override
    protected void setupViews(PtrClassicFrameLayout ptrFrame) {
        ptrFrame.setPullOrRelease(true);
    }
}
