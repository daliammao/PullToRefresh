package com.daliammao.pulltorefresh.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.daliammao.ptr.PtrFrameLayout;
import com.daliammao.ptr.handler.PtrHandler;
import com.daliammao.pulltorefresh.R;
import com.daliammao.pulltorefresh.header.RentalsSunHeaderView;
import com.daliammao.pulltorefresh.utils.LocalDisplay;
import com.squareup.picasso.Picasso;

/**
 * @author: zhoupengwei
 * @time:15/12/9-上午11:34
 * @Email: 496946423@qq.com
 * @desc:
 */
public class RentalsStyleActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_house);

        // loading image
        ImageView imageView = (ImageView) findViewById(R.id.store_house_ptr_image);
        Picasso.with(RentalsStyleActivity.this)
                .load("http://img5.duitang.com/uploads/item/201406/28/20140628122218_fLQyP.thumb.jpeg")
                .into(imageView);

        final PtrFrameLayout frame = (PtrFrameLayout) findViewById(R.id.store_house_ptr_frame);
        // header
        final RentalsSunHeaderView header = new RentalsSunHeaderView(RentalsStyleActivity.this);
        header.setPadding(0, LocalDisplay.dp2px(15), 0, LocalDisplay.dp2px(10));
        header.setUp(frame);

        frame.setDurationToCloseHeaderOrFooter(1500);
        frame.setHeaderView(header);
        frame.addPtrUIHeaderHandler(header);
        frame.postDelayed(new Runnable() {
            @Override
            public void run() {
                frame.autoRefresh(false);
            }
        }, 1000);

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
