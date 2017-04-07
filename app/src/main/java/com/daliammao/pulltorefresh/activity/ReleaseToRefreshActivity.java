package com.daliammao.pulltorefresh.activity;

import com.daliammao.ptr.PtrClassicFrameLayout;

/**
 * @author: zhoupengwei
 * @time:15/12/8-下午5:13
 * @Email: 496946423@qq.com
 * @desc:
 */
public class ReleaseToRefreshActivity extends WithTextViewInFrameLayoutActivity {

    @Override
    protected void setupViews(PtrClassicFrameLayout ptrFrame) {
        ptrFrame.setPullOrRelease(false);
    }
}
