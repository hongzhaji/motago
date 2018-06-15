package com.realm.motago;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.realm.motago.element.TestJson;
import com.realm.motago.manager.FontManager;
import com.realm.motago.manager.SupperFragmentManager;

public class MotaMainActivity extends AppCompatActivity
{

    private SupperFragmentManager manager;
    private Toolbar toolbar;
    private TextView tv;
    private Button mainMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.i("tyty","onCreate");

        ViewGroup v = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_mota_main, null);

        //change fonts
        FontManager.changeFonts(v,this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(v);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        tv = toolbar.findViewById(R.id.music_main_title);
        mainMenu = toolbar.findViewById(R.id.main_menu);
        mainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopMen();
            }
        });


        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

               backPressed();

            }
        });
        manager = new SupperFragmentManager(this, getSupportFragmentManager(), v);


        //test json
//        String str = "{\"status_code\":\"1111\",\"biz_code\":\"aaa\",\"asr_ret\":\"ccccc\"}";
//        TestJson aaJson = JSON.parseObject(str, TestJson.class);
//        Log.i("tyty", aaJson.toString());


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
      
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {


backPressed();

            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_mota_main, menu);
        return false;
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

    public  void setMainMusicName(String name)
    {
        if(name!=null)
        {
            tv.setText(name);
            tv.setEnabled(true);
            tv.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    manager.setMusicInfo(manager.getCurrentMusicInfo());
                }
            });
        }


    }

    private void showPopMen()
    {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.main_popu_menu,null);
        final PopupWindow popupWindow = new PopupWindow(linearLayout,100,120);
        Button xiaoziButoon = linearLayout.findViewById(R.id.main_menu_xiaozhi);
        xiaoziButoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.onXiaoZhiClick();
                //toolbar.setTitle("我是阿里小智");
                popupWindow.dismiss();
            }
        });
        Button exitButoon = linearLayout.findViewById(R.id.main_menu_exit);
        exitButoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showExitXiaoZhiDialog();
                popupWindow.dismiss();
            }
        });



        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(toolbar,Gravity.RIGHT|Gravity.TOP,0,48);


    }

    private void goHome()
    {
        Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
        mHomeIntent.addCategory(Intent.CATEGORY_HOME);
        mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(mHomeIntent);
    }

    private  void  backPressed()
    {
        if(!manager.backToLastFragment())
        {
            goHome();
        }
        else
        {
            if(manager.getCurrentMusicInfo()!=null && manager.isMainFragment())
            {
                tv.setText(manager.getCurrentMusicInfo().getName());
                tv.setEnabled(true);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        manager.translateToMotagoMusicFragment();
                    }
                });
            }
            else
            {
                tv.setText("");
                tv.setEnabled(false);
            }

        }
    }

    private void showExitXiaoZhiDialog()
    {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("确认退出阿里小智？")
//                .setMessage("")
//                .setPositiveButton("确定", new DialogInterface.OnClickListener()
//                {
//                    public void onClick(DialogInterface dialog, int id)
//                    {
//                        finish();
//                    }
//                })
//                .setNegativeButton("取消", new DialogInterface.OnClickListener()
//                {
//                    public void onClick(DialogInterface dialog, int id)
//                    {
//                        dialog.dismiss();
//                    }
//                }).show();
LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.popu_xiaozhi_exit,null);
//layout.setBackgroundResource(R.mipmap.main_menu_bg);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();

        dialog.show();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(240,120);
        layoutParams.leftMargin = 10;
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;



        dialog.getWindow().setContentView(layout,layoutParams);
        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        dialog.getWindow().setBackgroundDrawableResource(R.mipmap.main_menu_bg);
        dialog.getWindow().setLayout(240,120);

        Button cancel = layout.findViewById(R.id.xiaozhi_exit_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        Button sure = layout.findViewById(R.id.xiaozhi_exit_sure);
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                finish();
            }
        });

    }

    @Override
    public void finish()
    {
        super.finish();
        manager.finish();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.i("tyty","onPause");
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.i("tyty","onStart");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.i("tyty","onResume");
    }


}
