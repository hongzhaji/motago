package com.realm.motago;


import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.realm.motago.element.TestJson;
import com.realm.motago.manager.SupperFragmentManager;

public class MotaMainActivity extends AppCompatActivity
{

    private SupperFragmentManager manager;
    private  Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ViewGroup v = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_mota_main, null);

        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(v);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");



        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (!manager.backToLastFragment())
                {
                    finish();
                }
                else
                {
                    toolbar.setTitle("");
                }

            }
        });
        manager = new SupperFragmentManager(this, getSupportFragmentManager(), v);


        //test json
//        String str = "{\"status_code\":\"1111\",\"biz_code\":\"aaa\",\"asr_ret\":\"ccccc\"}";
//        TestJson aaJson = JSON.parseObject(str, TestJson.class);
//        Log.i("tyty", aaJson.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mota_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_1)
        {
            manager.onXiaoZhiClick();
            toolbar.setTitle("我是阿里小智");
            return true;
        } else if (id == R.id.menu_2)
        {
            showExitXiaoZhiDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showExitXiaoZhiDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认退出阿里小智？")
                .setMessage("")
                .setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void finish()
    {
        super.finish();
        manager.finish();
    }
}
