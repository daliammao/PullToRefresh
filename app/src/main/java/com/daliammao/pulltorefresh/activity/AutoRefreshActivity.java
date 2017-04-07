package com.daliammao.pulltorefresh.activity;

import com.daliammao.ptr.PtrClassicFrameLayout;

/**
 * @author: zhoupengwei
 * @time:15/12/8-下午5:24
 * @Email: 496946423@qq.com
 * @desc:
 */
public class AutoRefreshActivity extends WithGridViewActivity{
    @Override
    protected void setupViews(final PtrClassicFrameLayout ptrFrame) {
        ptrFrame.setRefreshingMinTime(3000);
        ptrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrFrame.autoRefresh(true);
            }
        }, 150);
    }
}
