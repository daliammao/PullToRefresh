package com.daliammao.pulltorefresh.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //列表项的数据
        String[] strs = {"包含GridView", "包含FrameLayout", "仅包含一个TextView", "包含ListView", "包含WebView", "Empty and ListView",
                "包含ScrollowView", "刷新时显示头部", "刷新时不显示头部", "释放刷新", "下拉刷新", "自动刷新", "自定义头", "自定义头2", "包含ViewPager"};
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, strs);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = null;
        switch (position - l.getHeaderViewsCount()) {
            case 0:
                intent = new Intent(MainActivity.this, WithGridViewActivity.class);
                break;
            case 1:
                intent = new Intent(MainActivity.this, WithTextViewInFrameLayoutActivity.class);
                break;
            case 2:
                intent = new Intent(MainActivity.this, WithTextViewActivity.class);
                break;
            case 3:
                intent = new Intent(MainActivity.this, WithListViewActivity.class);
                break;
            case 4:
                intent = new Intent(MainActivity.this, WithWebViewActivity.class);
                break;
            case 5:
                intent = new Intent(MainActivity.this, WithListViewAndEmptyViewActivity.class);
                break;
            case 6:
                intent = new Intent(MainActivity.this, WithScrollViewActivity.class);
                break;
            case 7:
                intent = new Intent(MainActivity.this, KeepHeaderActivity.class);
                break;
            case 8:
                intent = new Intent(MainActivity.this, HideHeaderActivity.class);
                break;
            case 9:
                intent = new Intent(MainActivity.this, ReleaseToRefreshActivity.class);
                break;
            case 10:
                intent = new Intent(MainActivity.this, PullToRefreshActivity.class);
                break;
            case 11:
                intent = new Intent(MainActivity.this, AutoRefreshActivity.class);
                break;
            case 12:
                intent = new Intent(MainActivity.this, StoreHouseActivity.class);
                break;
            case 13:
                intent = new Intent(MainActivity.this, RentalsStyleActivity.class);
                break;
            case 15:
                intent = new Intent(MainActivity.this, WithViewPageActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }
}
