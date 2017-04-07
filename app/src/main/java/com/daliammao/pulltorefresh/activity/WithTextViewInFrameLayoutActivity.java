package com.daliammao.pulltorefresh.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.daliammao.ptr.PtrClassicFrameLayout;
import com.daliammao.ptr.PtrFrameLayout;
import com.daliammao.ptr.handler.PtrHandler;
import com.daliammao.pulltorefresh.R;

/**
 * @author: zhoupengwei
 * @time:15/12/8-下午2:56
 * @Email: 496946423@qq.com
 * @desc:
 */
public class WithTextViewInFrameLayoutActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_frame);

        final PtrClassicFrameLayout ptrFrame = (PtrClassicFrameLayout)findViewById(R.id.fragment_rotate_header_with_view_group_frame);
        ptrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ptrFrame.refreshComplete();
                    }
                }, 1800);
            }

            @Override
            public void onLoadBegin(PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ptrFrame.loadComplete();
                    }
                }, 1800);
            }

            @Override
            public boolean checkCanDoDownRefresh(PtrFrameLayout frame, View content, View header) {
                return true;
            }

            @Override
            public boolean checkCanDoUpLoad(PtrFrameLayout frame, View content, View header) {
                return true;
            }
        });
        ptrFrame.setLastUpdateTimeRelateObject(this);

        // the following are default settings
        ptrFrame.setResistance(1.7f);
        ptrFrame.setRatioOfHeightToLoad(1.2f);
        ptrFrame.setDurationToClose(200);
        ptrFrame.setDurationToCloseHeaderOrFooter(1000);
        // default is false
        ptrFrame.setPullOrRelease(false);
        // default is true
        ptrFrame.setKeepHeaderWhenRefresh(true);

        setupViews(ptrFrame);
    }

    protected void setupViews(final PtrClassicFrameLayout ptrFrame) {

    }
}
