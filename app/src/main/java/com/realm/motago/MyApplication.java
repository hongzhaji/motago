package com.realm.motago;

import android.support.multidex.MultiDexApplication;
import android.util.Log;
import com.alibaba.sdk.android.openaccount.Environment;
import com.aliyun.alink.AlinkSDK;
import com.aliyun.alink.business.account.OALoginBusiness;
import com.aliyun.alink.business.login.IAlinkLoginCallback;

/**
 * Created by Skyyao on 2018\5\21 0021.
 */

public class MyApplication extends MultiDexApplication
{

    @Override
    public void onCreate()
    {
        super.onCreate();



        OALoginBusiness oaLoginBusiness = new OALoginBusiness();
        oaLoginBusiness.init(this, Environment.ONLINE, new IAlinkLoginCallback()
        {
            @Override
            public void onSuccess()
            {
                Log.i("tyty"," -------- success");
            }

            @Override
            public void onFailure(int i, String s)
            {
                Log.i("tyty"," -------- onFailure = "+s);
            }
        });

        AlinkSDK.init(this,"24898847",oaLoginBusiness);


    }
}
