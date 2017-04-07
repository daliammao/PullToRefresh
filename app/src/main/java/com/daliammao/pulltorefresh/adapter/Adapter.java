package com.daliammao.pulltorefresh.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.daliammao.pulltorefresh.R;
import com.daliammao.pulltorefresh.bean.Img;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * @author: zhoupengwei
 * @time:15/12/4-下午7:36
 * @Email: 496946423@qq.com
 * @desc:
 */
public class Adapter extends BaseAdapter{
    private Context mContext;
    private List<Img> beanList;

    public Adapter(Context mContext,
                   List<Img> beanList) {
        super();
        this.mContext = mContext;
        this.beanList = beanList;
    }

    @Override
    public int getCount() {
        return beanList==null?0:beanList.size();
    }

    @Override
    public Object getItem(int position) {
        return beanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Img bean = beanList.get(position);
        ViewHolder holder = null;
        if(convertView == null){
            convertView = View.inflate(mContext, R.layout.with_grid_view_item_image_list_grid, null);
            holder= new ViewHolder();
            holder.with_grid_view_item_image = (ImageView) convertView.findViewById(R.id.with_grid_view_item_image);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        Picasso.with(mContext)
                .load(bean.getPic())
                .into(holder.with_grid_view_item_image);
        return convertView;
    }

    static class ViewHolder{
        ImageView with_grid_view_item_image;
    }
}
