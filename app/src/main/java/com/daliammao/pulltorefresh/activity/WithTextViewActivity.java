package com.daliammao.pulltorefresh.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.daliammao.ptr.PtrClassicFrameLayout;
import com.daliammao.ptr.PtrFrameLayout;
import com.daliammao.ptr.handler.PtrDefaultHandler;
import com.daliammao.pulltorefresh.R;

/**
 * @author: zhoupengwei
 * @time:15/12/8-下午3:10
 * @Email: 496946423@qq.com
 * @desc:
 */
public class WithTextViewActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_text);
        final PtrClassicFrameLayout ptrFrame = (PtrClassicFrameLayout) findViewById(R.id.fragment_rotate_header_with_text_view_frame);
        ptrFrame.setLastUpdateTimeRelateObject(this);
        ptrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ptrFrame.refreshComplete();
                    }
                }, 1500);
            }

            @Override
            public void onLoadBegin(PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ptrFrame.loadComplete();
                    }
                }, 1500);
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
    }
}
