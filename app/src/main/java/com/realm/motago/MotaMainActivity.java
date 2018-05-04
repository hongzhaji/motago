package com.realm.motago;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import com.realm.motago.manager.SupperFragmentManager;

public class MotaMainActivity extends AppCompatActivity
{

private SupperFragmentManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ViewGroup v = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_mota_main,null);
        setContentView(v);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");


        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                 if(!manager.backToLastFragment())
                {
                    finish();
                }

            }
        });
        manager = new SupperFragmentManager(this,getSupportFragmentManager(),v);







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
            return true;
        }else if(id == R.id.menu_2)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
