package com.realm.motago.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.realm.motago.element.MyMusicInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Skyyao on 2018\5\15 0015.
 */

public class PlayListAdapter extends PagerAdapter
{
    private List<ListView> mViews;


    public PlayListAdapter(Context context)
    {
        mViews = new ArrayList<ListView>();

        List<MyMusicInfo> infos = new ArrayList<MyMusicInfo>();
        infos.add(new MyMusicInfo("aa","bb"));
        infos.add(new MyMusicInfo("aa","bb"));
        infos.add(new MyMusicInfo("aa","bb"));

        ListView mPlayListView = new ListView(context);
        mPlayListView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mPlayListView.setAdapter(new MyListViewAdapter(infos,context));
        ListView mPlayLovedView = new ListView(context);
        mPlayLovedView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mPlayLovedView.setAdapter(new MyListViewAdapter(infos,context));
        mViews.add(mPlayListView);
        mViews.add(mPlayLovedView);

        //set adepter



    }

    @Override
    public int getCount()
    {
        return mViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        container.addView(mViews.get(position));
        return mViews.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        //super.destroyItem(container, position, object);
        container.removeView(mViews.get(position));
    }

    @Override
    public int getItemPosition(Object object)
    {
        return super.getItemPosition(object);
    }
}
