package com.realm.motago.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.realm.motago.element.MyMusicInfo;

import java.util.List;

/**
 * Created by Skyyao on 2018\5\15 0015.
 */

public class MyListViewAdapter extends BaseAdapter
{
    private List<MyMusicInfo> myMusicInfos;


    @Override
    public int getCount()
    {
        return myMusicInfos.size();
    }

    @Override
    public Object getItem(int position)
    {
        return myMusicInfos.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {


        return null;
    }
}
