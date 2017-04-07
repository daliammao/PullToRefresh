package com.daliammao.pulltorefresh.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;

import com.daliammao.ptr.PtrClassicFrameLayout;
import com.daliammao.ptr.PtrFrameLayout;
import com.daliammao.ptr.handler.PtrDefaultHandler;
import com.daliammao.ptr.handler.PtrHandler;
import com.daliammao.pulltorefresh.R;

/**
 * @author: zhoupengwei
 * @time:15/12/8-下午4:47
 * @Email: 496946423@qq.com
 * @desc:
 */
public class WithScrollViewActivity extends Activity {
    private PtrClassicFrameLayout mPtrFrame;
    private ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_scroll);

        mScrollView = (ScrollView) findViewById(R.id.rotate_header_scroll_view);
        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.rotate_header_web_view_frame);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoDownRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mScrollView, header);
            }

            @Override
            public boolean checkCanDoUpLoad(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledUp(frame,content,header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mPtrFrame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPtrFrame.refreshComplete();
                    }
                }, 100);
            }

            @Override
            public void onLoadBegin(PtrFrameLayout frame) {
                mPtrFrame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPtrFrame.loadComplete();
                    }
                }, 100);
            }
        });

        // the following are default settings
        mPtrFrame.setResistance(1.7f);
        mPtrFrame.setRatioOfHeightToRefresh(1.2f);
        mPtrFrame.setDurationToClose(200);
        mPtrFrame.setDurationToCloseHeaderOrFooter(1000);
        // default is false
        mPtrFrame.setPullOrRelease(false);
        // default is true
        mPtrFrame.setKeepHeaderWhenRefresh(true);
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrame.autoRefresh();
            }
        }, 100);
    }
}
