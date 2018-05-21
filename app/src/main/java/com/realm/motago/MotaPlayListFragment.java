package com.realm.motago;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.realm.motago.adapter.PlayListAdapter;

/**
 * Created by Skyyao on 2018\5\15 0015.
 */

public class MotaPlayListFragment extends Fragment implements CompoundButton.OnCheckedChangeListener
{
    private ViewPager viewPager;
    private PlayListAdapter adapter;
    private CheckBox playCB;
    private CheckBox lovedCB;


    public MotaPlayListFragment()
    {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_mota_musiclist,container,false);

        playCB = v.findViewById(R.id.mota_music_list_play);
        playCB.setOnCheckedChangeListener(this);
        lovedCB = v.findViewById(R.id.mota_music_list_loved);
        lovedCB.setOnCheckedChangeListener(this);

        viewPager = v.findViewById(R.id.list_music_pageview);

        adapter = new PlayListAdapter(getContext());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                if(position == 1)
                {
                    lovedCB.setChecked(true);
                    playCB.setChecked(false);
                }else  if(position == 0)
                {
                    lovedCB.setChecked(false);
                    playCB.setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });

        return v;
    }

    public void setHelp(IMusicPlayHelp help)
    {
        adapter.setHelp(help);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        Log.i("tyty"," id = "+buttonView.getId() +"  ischecked = "+isChecked);
        if(buttonView.getId() == R.id.mota_music_list_loved && isChecked)
        {
            viewPager.setCurrentItem(1);
            lovedCB.setEnabled(false);
            playCB.setEnabled(true);
        }else  if(buttonView.getId() == R.id.mota_music_list_play && isChecked)
        {
            viewPager.setCurrentItem(0);
            lovedCB.setEnabled(true );
            playCB.setEnabled(false);
        }
    }

    public interface IMusicPlayHelp
    {
         void quickPlay(String uuid,String type, String itemId, String collectionId);

         void cancelFavorite(String uuid, String itemId, String channelId);

    }
}
