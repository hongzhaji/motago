package com.realm.motago.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.aliyun.alink.device.AlinkDevice;
import com.realm.motago.MotaPlayListFragment;
import com.realm.motago.R;
import com.realm.motago.element.AliyunMusicInfo;
import com.realm.motago.element.MyMusicInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Skyyao on 2018\5\15 0015.
 */

public class MyListViewAdapter extends BaseAdapter
{
    private List<AliyunMusicInfo> myMusicInfos;
    private Context context;
    private MotaPlayListFragment.IMusicPlayHelp help;

    public MyListViewAdapter( Context context)
    {
        this.context = context;
        myMusicInfos = new ArrayList<AliyunMusicInfo>();
    }

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
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder = null;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.my_list_view_item, parent, false);
            TextView name = convertView.findViewById(R.id.list_view_item_music_name);
            TextView artis = convertView.findViewById(R.id.list_view_item_music_artis);
            Button loved = convertView.findViewById(R.id.list_view_item_music_loved);
            holder = new ViewHolder(name, artis, loved);
            convertView.setTag(holder);
        } else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.cbLoved.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                help.cancelFavorite("", "", "");
            }
        });
        holder.tvName.setText(myMusicInfos.get(position).getName());
        holder.tvArtis.setText(myMusicInfos.get(position).getArtist());
        convertView.setClickable(true);
        convertView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AliyunMusicInfo info = myMusicInfos.get(position);
                help.quickPlay(AlinkDevice.getInstance().getDeviceUUID(), ""+info.getItemType(), ""+info.getId(), ""+info.getCollectionId());
            }
        });

        return convertView;
    }

    public void setHelp(MotaPlayListFragment.IMusicPlayHelp help)
    {
        this.help = help;
    }

    public void setMyMusicInfos(List<AliyunMusicInfo> myMusicInfos)
    {
        this.myMusicInfos = myMusicInfos;
    }

    class ViewHolder
    {
        public TextView tvName;
        public TextView tvArtis;
        public Button cbLoved;

        public ViewHolder(TextView tvName, TextView tvArtis, Button cbLoved)
        {
            this.tvName = tvName;
            this.tvArtis = tvArtis;
            this.cbLoved = cbLoved;
        }
    }

}
