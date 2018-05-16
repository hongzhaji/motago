package com.realm.motago.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.realm.motago.R;
import com.realm.motago.element.MyMusicInfo;

import java.util.List;

/**
 * Created by Skyyao on 2018\5\15 0015.
 */

public class MyListViewAdapter extends BaseAdapter
{
    private List<MyMusicInfo> myMusicInfos;
    private Context context;

    public MyListViewAdapter(List<MyMusicInfo> myMusicInfos, Context context)
    {
        this.myMusicInfos = myMusicInfos;
        this.context = context;
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
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder = null;
        if(convertView == null)
        {
            convertView =  LayoutInflater.from(context).inflate(R.layout.my_list_view_item,parent,false);
            TextView name = convertView.findViewById(R.id.list_view_item_music_name);
            TextView artis = convertView.findViewById(R.id.list_view_item_music_artis);
            CheckBox loved = convertView.findViewById(R.id.list_view_item_music_loved);
            holder = new ViewHolder(name,artis,loved);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.cbLoved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                Log.i("tyty","loved = "+isChecked);
            }
        });
        holder.tvName.setText(myMusicInfos.get(position).musicName);
        holder.tvArtis.setText(myMusicInfos.get(position).musicArtis);

        return convertView;
    }

    class ViewHolder
    {
       public TextView tvName;
        public  TextView tvArtis;
        public CheckBox  cbLoved;

        public ViewHolder(TextView tvName, TextView tvArtis, CheckBox cbLoved)
        {
            this.tvName = tvName;
            this.tvArtis = tvArtis;
            this.cbLoved = cbLoved;
        }
    }

}
