package com.realm.motago;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.realm.motago.element.AliyunMusicInfo;
import com.realm.motago.manager.INavigationClick;

/**
 * Created by Skyyao on 2018\5\4 0004.
 */

public class MotaMusicFragment extends Fragment implements INavigationClick
{

    private IAliyunMusicHelp musicHelp;
    private AliyunMusicInfo musicInfo;
    private TextView musicAtist;
    private TextView musicName;
    private  Button playButton;


    public MotaMusicFragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_mota_music, container, false);
        initUI(v);

        return v;
    }

    @Override
    public boolean onNavigationCLick()
    {

        return true;
    }

    public void setMusicHelp(IAliyunMusicHelp musicHelp)
    {
        this.musicHelp = musicHelp;
    }

    public void setMusicInfo(AliyunMusicInfo musicInfo)
    {
        this.musicInfo = musicInfo;
        invaluedateMusicInfo();
        ;
    }

    private void initUI(View v)
    {
         playButton = v.findViewById(R.id.music_play_play);
        playButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (musicHelp.isPlaying())
                {
                    musicHelp.aliyunPauseMusic();
                    playButton.setBackgroundResource(R.drawable.music_play_sel_pause);

                } else
                {
                    musicHelp.aliyunPlayMusic();
                    playButton.setBackgroundResource(R.drawable.music_play_sel_play);
                }

            }
        });
        Button nextButtom = v.findViewById(R.id.music_play_next);
        nextButtom.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                musicHelp.aliyunPlayNext();
            }
        });
        Button listButtom = v.findViewById(R.id.music_play_list);
        listButtom.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                musicHelp.showPlayList();
            }
        });
        Button lastButtom = v.findViewById(R.id.music_play_last);
        lastButtom.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                musicHelp.aliyunPlayLast();
            }
        });

        Button modelButtom = v.findViewById(R.id.music_play_model);
        modelButtom.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                musicHelp.aliyunSetPlayMode(0);
            }
        });



        musicAtist = v.findViewById(R.id.music_artis);
        musicName = v.findViewById(R.id.music_name);

    }

    private void invaluedateMusicInfo()
    {
        musicAtist.setText(musicInfo.getArtist());
        musicName.setText(musicInfo.getName());
        playButton.setBackgroundResource(R.drawable.music_play_sel_play);
    }

    public interface IAliyunMusicHelp
    {
        void aliyunPlayMusic();

        void aliyunPauseMusic();

        void aliyunPlayNext();

        void aliyunPlayLast();

        void aliyunSetPlayMode(int mode);

        void showPlayList();

        boolean isPlaying();


    }
}
