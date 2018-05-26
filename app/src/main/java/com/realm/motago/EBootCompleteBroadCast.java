package com.realm.motago;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.realm.motago.server.AliyunBackstageServer;

/**
 * Created by Skyyao on 2018\5\26 0026.
 */

public class EBootCompleteBroadCast extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            MyApplication application = (MyApplication) context.getApplicationContext();
            application.bindService();

        }
    }
}
