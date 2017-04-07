package com.daliammao.pulltorefresh.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.daliammao.ptr.PtrClassicFrameLayout;
import com.daliammao.ptr.PtrFrameLayout;
import com.daliammao.ptr.handler.PtrDefaultHandler;
import com.daliammao.ptr.handler.PtrHandler;
import com.daliammao.pulltorefresh.R;
import com.daliammao.pulltorefresh.adapter.Adapter;
import com.daliammao.pulltorefresh.bean.Data;
import com.daliammao.pulltorefresh.bean.Img;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * @author: zhoupengwei
 * @time:15/12/8-下午4:14
 * @Email: 496946423@qq.com
 * @desc:
 */
public class WithListViewAndEmptyViewActivity extends Activity {
    private AsyncHttpClient client = new AsyncHttpClient();
    private List<Img> mlist = new ArrayList<>();
    private PtrClassicFrameLayout mPtrFrame;
    private TextView mTextView;
    private Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_list_and_empty);

        mTextView = (TextView) findViewById(R.id.list_view_with_empty_view_fragment_empty_view);
        final ListView listView = (ListView) findViewById(R.id.list_view_with_empty_view_fragment_list_view);
        mAdapter = new Adapter(WithListViewAndEmptyViewActivity.this, mlist);
        listView.setAdapter(mAdapter);

        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.list_view_with_empty_view_fragment_ptr_frame);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                updateData();
            }

            @Override
            public void onLoadBegin(PtrFrameLayout frame) {
                updateData();
            }

            @Override
            public boolean checkCanDoDownRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, listView, header);
            }

            @Override
            public boolean checkCanDoUpLoad(PtrFrameLayout frame, View content, View footer) {
                return PtrDefaultHandler.checkContentCanBePulledUp(frame, listView, footer);
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

        mPtrFrame.setVisibility(View.INVISIBLE);
        mTextView.setVisibility(View.VISIBLE);

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPtrFrame.setVisibility(View.VISIBLE);
                mPtrFrame.autoRefresh();
            }
        });
    }

    protected void updateData() {
        String url = "http://cube-server.liaohuqiu.net/api_demo/image-list.php";
        client.post(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                try {
                    mTextView.setVisibility(View.GONE);
                    Data data = new Gson().fromJson(json.getJSONObject("data").toString(), Data.class);
                    mlist.clear();
                    mlist.addAll(data.getList());
                    mAdapter.notifyDataSetChanged();
                    if (mPtrFrame.getStatus() == PtrFrameLayout.PTR_STATUS_REFRESHING) {
                        mPtrFrame.refreshComplete();
                    } else if (mPtrFrame.getStatus() == PtrFrameLayout.PTR_STATUS_LOADING) {
                        mPtrFrame.loadComplete();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
