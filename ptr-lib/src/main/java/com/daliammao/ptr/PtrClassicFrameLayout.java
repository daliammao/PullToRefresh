package com.daliammao.ptr;

import android.content.Context;
import android.util.AttributeSet;

import com.daliammao.ptr.footer.PtrClassicDefaultFooter;
import com.daliammao.ptr.header.PtrClassicDefaultHeader;

public class PtrClassicFrameLayout extends PtrFrameLayout {

    private PtrClassicDefaultHeader mPtrClassicHeader;
    private PtrClassicDefaultFooter mPtrClassicFooter;

    public PtrClassicFrameLayout(Context context) {
        super(context);
        initViews();
    }

    public PtrClassicFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public PtrClassicFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
    }

    private void initViews() {
        mPtrClassicHeader = new PtrClassicDefaultHeader(getContext());
        mPtrClassicFooter = new PtrClassicDefaultFooter(getContext());
        setHeaderView(mPtrClassicHeader);
        addPtrUIHeaderHandler(mPtrClassicHeader);
        setFooterView(mPtrClassicFooter);
        addPtrUIFooterHandler(mPtrClassicFooter);
    }

    public PtrClassicDefaultHeader getHeader() {
        return mPtrClassicHeader;
    }

    /**
     * Specify the last update time by this key string
     *
     * @param key
     */
    public void setLastUpdateTimeKey(String key) {
        if (mPtrClassicHeader != null) {
            mPtrClassicHeader.setLastUpdateTimeKey(key);
        }
    }

    /**
     * Using an object to specify the last update time.
     *
     * @param object
     */
    public void setLastUpdateTimeRelateObject(Object object) {
        if (mPtrClassicHeader != null) {
            mPtrClassicHeader.setLastUpdateTimeRelateObject(object);
        }
    }
}
