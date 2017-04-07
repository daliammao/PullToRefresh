package com.daliammao.pulltorefresh.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.daliammao.ptr.PtrClassicFrameLayout;
import com.daliammao.ptr.PtrFrameLayout;
import com.daliammao.ptr.handler.PtrHandler;
import com.daliammao.pulltorefresh.R;
import com.daliammao.pulltorefresh.adapter.ViewPagerAdapter;
import com.daliammao.pulltorefresh.fragment.ViewPagerFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhoupengwei
 * @time:15/12/9-下午2:45
 * @Email: 496946423@qq.com
 * @desc:
 */
public class WithViewPageActivity extends AppCompatActivity {
    ViewPager view_pager_view_pager;
    TabLayout view_pager_tab_indicator;
    ViewPagerAdapter mAdapter;
    List<Fragment> mFragments;

    private PtrFrameLayout mPtrFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_view_pager);

        view_pager_tab_indicator = (TabLayout)findViewById(R.id.view_pager_tab_indicator);
        view_pager_view_pager =(ViewPager)findViewById(R.id.view_pager_view_pager);

        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.view_pager_ptr_frame);
        mPtrFrame.disableWhenHorizontalMove(true);
        mPtrFrame.setPtrHandler(new PtrHandler() {
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

        mFragments = new ArrayList<>(5);
        for(int i=0;i<5;i++){
            Fragment fragment = new ViewPagerFragment();
            Bundle args = new Bundle();
            args.putInt("index", i);
            fragment.setArguments(args);
            mFragments.add(fragment);
        }
        mAdapter=new ViewPagerAdapter(getSupportFragmentManager(), mFragments);

        view_pager_view_pager.setAdapter(mAdapter);
        view_pager_view_pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(view_pager_tab_indicator));

        //将TabLayout和ViewPager关联起来。
        view_pager_tab_indicator.setupWithViewPager(view_pager_view_pager);
        //给Tabs设置适配器
        view_pager_tab_indicator.setTabsFromPagerAdapter(mAdapter);
    }
}
