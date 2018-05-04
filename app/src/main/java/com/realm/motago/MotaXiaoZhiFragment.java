package com.realm.motago;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.realm.motago.manager.INavigationClick;

/**
 * Created by Skyyao on 2018\5\4 0004.
 */

public class MotaXiaoZhiFragment extends Fragment implements INavigationClick
{
    public MotaXiaoZhiFragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_mota_xiaozhi,container,false);
        return v;
    }

    @Override
    public boolean onNavigationCLick()
    {
            return true;
    }
}
