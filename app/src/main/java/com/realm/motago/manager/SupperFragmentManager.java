package com.realm.motago.manager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.realm.motago.MotaMainActivityFragment;
import com.realm.motago.MotaMusicFragment;
import com.realm.motago.MotaXiaoZhiFragment;
import com.realm.motago.R;

/**
 * Created by Skyyao on 2018\5\4 0004.
 */

public class SupperFragmentManager implements IXiaoZhiClick
{
    public static final int MOTA_FRAGMENT_MAIN = 0;
    public static final int MOTA_FRAGMENT_MUSIC = 1;
    public static final int MOTA_FRAGMENT_XIAOZHI = 2;


    private Context mContext;
    private FragmentManager motaManager;
    private ViewGroup mRootView;

    private Fragment[] kindFragment;


    // User interaction
    private Button mVoiceInput;
    private Button getmVoiceCancel;
    private ViewGroup mBottomView;
    private int mCurrentFragmentIndex ;


    public SupperFragmentManager(Context context, FragmentManager fragmentManager, ViewGroup v)
    {
        this.mContext = context;
        this.motaManager = fragmentManager;
        this.mRootView = v;

        initFragment();
        initEventCallback();

        motaManager.beginTransaction().show( kindFragment[MOTA_FRAGMENT_MUSIC]).commit();
        mCurrentFragmentIndex = MOTA_FRAGMENT_MAIN;

        switchAliyunState(false);
    }


    @Override
    public void onXiaoZhiClick()
    {
        FragmentTransaction transaction = motaManager.beginTransaction();
        transaction.hide(kindFragment[MOTA_FRAGMENT_MAIN]);
        transaction.show( kindFragment[MOTA_FRAGMENT_XIAOZHI]).commit();
        mCurrentFragmentIndex = MOTA_FRAGMENT_XIAOZHI;
    }


    public boolean backToLastFragment()
    {
        if (mCurrentFragmentIndex == MOTA_FRAGMENT_XIAOZHI && ((MotaXiaoZhiFragment) kindFragment[MOTA_FRAGMENT_XIAOZHI]).onNavigationCLick())
        {
            FragmentTransaction transaction = motaManager.beginTransaction();
            transaction.hide(kindFragment[MOTA_FRAGMENT_XIAOZHI]);
            transaction.show(kindFragment[MOTA_FRAGMENT_MAIN]).commit();
            mCurrentFragmentIndex = MOTA_FRAGMENT_MAIN;
            return true;
        }


        return false;
    }


    private void initFragment()
    {
        kindFragment = new Fragment[3];
        MotaMainActivityFragment mainFragment = new MotaMainActivityFragment();
        kindFragment[MOTA_FRAGMENT_MAIN] = mainFragment;
        MotaMusicFragment musicFragment = new MotaMusicFragment();
        kindFragment[MOTA_FRAGMENT_MUSIC] = musicFragment;
        MotaXiaoZhiFragment xiaoZhiFragment = new MotaXiaoZhiFragment();
        kindFragment[MOTA_FRAGMENT_XIAOZHI] = xiaoZhiFragment;
        for (Fragment fragment :
                kindFragment)
        {
            motaManager.beginTransaction().add(R.id.fragment_main,fragment).hide(fragment).commit();
        }

    }

    private void initEventCallback()
    {
        mVoiceInput = mRootView.findViewById(R.id.voice_input);
        mVoiceInput.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switchAliyunState(true);
            }
        });
        getmVoiceCancel = mRootView.findViewById(R.id.cancel_record);
        getmVoiceCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switchAliyunState(false);
            }
        });
        mBottomView = mRootView.findViewById(R.id.bottom_recording);
    }

    private void switchAliyunState(boolean recording)
    {
        if (recording)
        {
            mBottomView.setVisibility(View.VISIBLE);
            mVoiceInput.setVisibility(View.GONE);
        } else
        {
            mBottomView.setVisibility(View.GONE);
            mVoiceInput.setVisibility(View.VISIBLE);
        }
    }
}
