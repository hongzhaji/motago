package com.realm.motago;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.aliyun.alink.device.AlinkDevice;
import com.realm.motago.element.AliyunMusicInfo;
import com.realm.motago.manager.INavigationClick;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Skyyao on 2018\5\4 0004.
 */

public class MotaMusicFragment extends Fragment implements INavigationClick
{

    private IAliyunMusicHelp musicHelp;
    private AliyunMusicInfo musicInfo;
    private TextView musicAtist;
    private TextView musicName;
    private Button playButton;
    private CheckBox musicLoved;
    private TextView totalMusicTime;
    private TextView currentMusicTime;
    private SeekBar musicSeekBar;
    private Handler mhadler;
    private  static final int CYCLE_MODE= 0;
    private  static final int RANDOM_MODE= 1;
    private  static final int SINGLE_MODE= 2;
    private int currentMode = 0;



    public MotaMusicFragment()
    {
        mhadler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                invaluedateMusicPlayStateUI();
            }
        };
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

    }

    //long
    public void setMusicCurrentTime(String time, int percent)
    {

        if(time == null || musicInfo ==null)
        {
            Log.i("tyty","---music = null");
            return;
        }


        if (time.equals("") || musicInfo.getDuration() == null || musicInfo.getDuration().equals(""))
        {

            //set recompense total time.
            long curTotalTime = musicHelp.getRecompenseTotalTime();
            if (curTotalTime == -1)
            {
                Log.e("tyty", "time can't be null");
                currentMusicTime.setText("");
                return;
            }
            try
            {
                Date totalDate = longToDate(curTotalTime);
                String mmssTotal = dateToString(totalDate, "mm:ss");
                totalMusicTime.setText(mmssTotal);
                musicInfo.setDuration(mmssTotal);
            } catch (Exception e)
            {
                Log.e("tyty", "time can't be null");
                currentMusicTime.setText("");
                return;
            }


        }
        try
        {
            long curTime = new Long(time);
            Date date = longToDate(curTime);
            currentMusicTime.setText(dateToString(date, "mm:ss"));
            musicSeekBar.setProgress(percent);
            Log.i("tyty", "data = " + date.toString() + "  percent = " + percent);

        } catch (ParseException e)
        {

        }


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
                if (musicInfo != null)
                {
                    //max = 20
                    musicHelp.showPlayList(AlinkDevice.getInstance().getDeviceUUID(), "0", "20", "1", "" + musicInfo.getChannelId(), "" + musicInfo.getItemType(), "" + musicInfo.getCollectionId(), "" + musicInfo.getId());
                }

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
                if(currentMode == CYCLE_MODE)
                {
                    currentMode = RANDOM_MODE;
                    v.setBackgroundResource(R.drawable.music_play_sel_random);
                }else if(currentMode == RANDOM_MODE)
                {
                    currentMode = SINGLE_MODE;
                    v.setBackgroundResource(R.drawable.music_play_sel_single);
                }
                else if(currentMode == SINGLE_MODE)
                {
                    currentMode = CYCLE_MODE;
                    v.setBackgroundResource(R.drawable.music_play_sel_cycle);
                }
                musicHelp.aliyunSetPlayMode(currentMode);
            }
        });


        musicAtist = v.findViewById(R.id.music_artis);
        musicName = v.findViewById(R.id.music_name);
        totalMusicTime = v.findViewById(R.id.music_total_time);
        currentMusicTime = v.findViewById(R.id.music_current_time);
        musicLoved = v.findViewById(R.id.music_loved_cb);
        musicSeekBar = v.findViewById(R.id.music_seek);
        musicSeekBar.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });
        musicLoved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    musicHelp.aliyunLoveMusic(musicInfo.getId());
                } else
                {
                    musicHelp.cancelLovedMusic(AlinkDevice.getInstance().getDeviceUUID(), "" + musicInfo.getId(), "" + musicInfo.getChannelId());
                }

            }
        });


        //update play state ui .immediately
        invaluedateMusicPlayStateUI();
    }

    private void invaluedateMusicInfo()
    {
        musicAtist.setText(musicInfo.getArtist());
        musicName.setText(musicInfo.getName());
        playButton.setBackgroundResource(R.drawable.music_play_sel_play);
        Log.i("tyty", "-- love = " + musicInfo.isLoved());
        musicLoved.setChecked(musicInfo.isLoved());
        totalMusicTime.setText(musicInfo.getDuration());
    }

    //alone
    private void invaluedateMusicPlayStateUI()
    {
        if(musicHelp == null)
        {
            Log.i("tyty","  musicHelp  === null" );
            return;
        }
        if (musicHelp.isPlaying())
        {
            playButton.setBackgroundResource(R.drawable.music_play_sel_play);

        } else
        {
            playButton.setBackgroundResource(R.drawable.music_play_sel_pause);
        }
        mhadler.sendEmptyMessageDelayed(1, 1000);
    }


    //mm:ss
    private Date stringToDate(String strTime)

    {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        Date date = null;
        try
        {

            date = formatter.parse(strTime);
        } catch (ParseException e)
        {
            Log.e("tyty", e.toString());
            date = null;
        }
        return date;
    }

    private Date longToDate(long currentTime)
            throws ParseException
    {
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = dateToString(dateOld, "mm:ss"); // 把date类型的时间转换为string
        Date date = stringToDate(sDateTime); // 把String类型转换为Date类型
        return date;
    }

    private String dateToString(Date data, String formatType)
    {
        return new SimpleDateFormat(formatType).format(data);
    }

    public interface IAliyunMusicHelp
    {
        void aliyunPlayMusic();

        void aliyunPauseMusic();

        void aliyunPlayNext();

        void aliyunPlayLast();

        void aliyunSetPlayMode(int mode);

        void showPlayList(String uuid, String from, String size, String direct, String channelId, String channelType, String collectionID, String itemId);

        boolean isPlaying();

        void aliyunLoveMusic(int itemid);

        //time recompense
        long getRecompenseTotalTime();

        void cancelLovedMusic(String uuid, String itemId, String channelId);

    }
}
