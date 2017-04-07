package com.daliammao.pulltorefresh.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @author: zhoupengwei
 * @time:15/12/9-下午2:29
 * @Email: 496946423@qq.com
 * @desc:
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> mFragments;

    public ViewPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        mFragments = fragments;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments==null?0:mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return String.valueOf(position);
    }
}
