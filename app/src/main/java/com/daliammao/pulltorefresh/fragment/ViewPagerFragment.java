package com.daliammao.pulltorefresh.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daliammao.pulltorefresh.R;

/**
 * @author: zhoupengwei
 * @time:15/12/9-下午1:56
 * @Email: 496946423@qq.com
 * @desc:
 */
public class ViewPagerFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_pager, null);
        Bundle data = getArguments();
        int index = data.getInt("index");
        TextView view_pager_fragment_text= (TextView)view.findViewById(R.id.view_pager_fragment_text);
        view_pager_fragment_text.setText(String.valueOf(index));
        return view;
    }
}
