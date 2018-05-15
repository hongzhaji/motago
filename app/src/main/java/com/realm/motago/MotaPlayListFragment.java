package com.realm.motago;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Skyyao on 2018\5\15 0015.
 */

public class MotaPlayListFragment extends Fragment
{
    private ViewPager viewPager;

    public MotaPlayListFragment()
    {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_mota_musiclist,container,false);
        viewPager = v.findViewById(R.id.list_music_pageview);


        return v;
    }
}
