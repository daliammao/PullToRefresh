package com.daliammao.pulltorefresh.activity;

import android.view.View;
import android.widget.Toast;

import com.daliammao.ptr.PtrClassicFrameLayout;
import com.daliammao.ptr.PtrFrameLayout;
import com.daliammao.ptr.handler.PtrDefaultHandler;

/**
 * @author: zhoupengwei
 * @time:15/12/8-下午5:04
 * @Email: 496946423@qq.com
 * @desc:
 */
public class HideHeaderActivity extends WithTextViewInFrameLayoutActivity {
    @Override
    protected void setupViews(final PtrClassicFrameLayout ptrFrame) {
        ptrFrame.setKeepHeaderWhenRefresh(false);
        ptrFrame.setKeepFooterWhenLoad(false);

        ptrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(HideHeaderActivity.this,"刷新完成",Toast.LENGTH_SHORT).show();
                        ptrFrame.refreshComplete();
                    }
                }, 1500);
            }

            @Override
            public void onLoadBegin(PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(HideHeaderActivity.this,"加载完成",Toast.LENGTH_SHORT).show();
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
