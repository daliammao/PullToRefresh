package com.daliammao.pulltorefresh.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.daliammao.ptr.PtrFrameLayout;
import com.daliammao.ptr.handler.PtrHandler;
import com.daliammao.ptr.handler.PtrUIHeaderHandler;
import com.daliammao.ptr.indicator.PtrIndicator;
import com.daliammao.pulltorefresh.R;
import com.daliammao.pulltorefresh.header.StoreHouseHeader;
import com.daliammao.pulltorefresh.utils.LocalDisplay;
import com.squareup.picasso.Picasso;

/**
 * @author: zhoupengwei
 * @time:15/12/8-下午5:49
 * @Email: 496946423@qq.com
 * @desc:
 */
public class StoreHouseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_house);

        // loading image
        ImageView imageView = (ImageView) findViewById(R.id.store_house_ptr_image);
        Picasso.with(StoreHouseActivity.this)
                .load("http://img5.duitang.com/uploads/item/201406/28/20140628122218_fLQyP.thumb.jpeg")
                .into(imageView);

        final PtrFrameLayout frame = (PtrFrameLayout) findViewById(R.id.store_house_ptr_frame);
        final StoreHouseHeader header = new StoreHouseHeader(StoreHouseActivity.this);
        header.setPadding(0, LocalDisplay.dp2px(15), 0, 0);

        // using string array from resource xml file
        header.initWithStringArray(R.array.storehouse);

        frame.setDurationToCloseHeaderOrFooter(1500);
        frame.setHeaderView(header);
        frame.addPtrUIHeaderHandler(header);
        frame.postDelayed(new Runnable() {
            @Override
            public void run() {
                frame.autoRefresh(false);
            }
        }, 100);

        // change header after loaded
        frame.addPtrUIHeaderHandler(new PtrUIHeaderHandler() {

            private int mLoadTime = 0;

            @Override
            public void onUIReset(PtrFrameLayout frame) {
                mLoadTime++;
                if (mLoadTime % 2 == 0) {
                    header.setScale(1);
                    header.initWithStringArray(R.array.storehouse);
                } else {
                    header.setScale(0.5f);
                    header.initWithStringArray(R.array.akta);
                }
            }

            @Override
            public void onUIPrepare(PtrFrameLayout frame) {

            }

            @Override
            public void onUIBegin(PtrFrameLayout frame) {

            }

            @Override
            public void onUIComplete(PtrFrameLayout frame) {

            }

            @Override
            public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

            }
        });

        frame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoDownRefresh(PtrFrameLayout frame, View content, View header) {
                return true;
            }

            @Override
            public boolean checkCanDoUpLoad(PtrFrameLayout frame, View content, View header) {
                return true;
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        frame.refreshComplete();
                    }
                }, 2000);
            }

            @Override
            public void onLoadBegin(final PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        frame.loadComplete();
                    }
                }, 2000);
            }
        });
    }
}
