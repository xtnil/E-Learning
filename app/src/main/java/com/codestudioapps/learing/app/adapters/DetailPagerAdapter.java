package com.codestudioapps.learing.app.adapters;

import android.app.Activity;
import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.codestudioapps.learing.app.R;
import com.codestudioapps.learing.app.webengine.WebEngine;

import java.util.ArrayList;

public class DetailPagerAdapter extends PagerAdapter {

    private Context mContext;

    private ArrayList<String> mItemList;
    private LayoutInflater inflater;
    private LinearLayout mLoadingView, mNoDataView;
    private WebView mWebView;
    private WebEngine mWebEngine;


    public DetailPagerAdapter(Context mContext, ArrayList<String> mItemList) {
        this.mContext = mContext;
        this.mItemList = mItemList;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mItemList.size();
    }


    @Override
    public Object instantiateItem(final ViewGroup view, final int position) {

        View rootView = inflater.inflate(R.layout.item_view_pager, view, false);

        mLoadingView = (LinearLayout) rootView.findViewById(R.id.loadingView);
        mNoDataView = (LinearLayout) rootView.findViewById(R.id.noDataView);

        mWebView = (WebView) rootView.findViewById(R.id.web_view);
        mWebEngine = new WebEngine(mWebView, (Activity) mContext);
        mWebEngine.initWebView();

        showLoader();
        mWebEngine.loadHtml(mItemList.get(position));
        hideLoader();

        view.addView(rootView);

        return rootView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    public void showLoader() {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.VISIBLE);
        }

        if (mNoDataView != null) {
            mNoDataView.setVisibility(View.GONE);
        }
    }

    public void hideLoader() {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.GONE);
        }
        if (mNoDataView != null) {
            mNoDataView.setVisibility(View.GONE);
        }
    }

}
