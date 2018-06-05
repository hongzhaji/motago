package com.realm.motago;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.realm.motago.adapter.MsgAdapter;
import com.realm.motago.element.Msg;
import com.realm.motago.manager.SupperFragmentManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MotaMainActivityFragment extends Fragment implements SupperFragmentManager.IMesHelper
{

    private RecyclerView recyclerView;
    private TextView mWarnInfoTextView;
    private TextView mWarnInfoTextView_1;

    private MsgAdapter adapter;
    private boolean mWarnInfohide ;
    List<Msg> msgList = new ArrayList<>();
    private Handler mUiHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);

            if(!mWarnInfohide)
            {
                mWarnInfoTextView.setVisibility(View.GONE);
                mWarnInfoTextView_1.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

           // recyclerView.invalidate();
            Log.i("tyty"," adapter invalueda");
            adapter.notifyDataSetChanged();

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // initMsg();

    }

    public MotaMainActivityFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_mota_main, container, false);
        recyclerView = (RecyclerView) viewGroup.findViewById(R.id.msg_recyclerView);
        recyclerView.setVisibility(View.GONE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new MsgAdapter(msgList);
        recyclerView.setAdapter(adapter);

        mWarnInfoTextView = viewGroup.findViewById(R.id.main_warn_info);
        mWarnInfoTextView.setVisibility(View.VISIBLE);
        mWarnInfoTextView_1 = viewGroup.findViewById(R.id.main_warn_info_1);
        mWarnInfoTextView_1.setVisibility(View.VISIBLE);

        mWarnInfohide = false;
        return viewGroup;
    }

    private void initMsg()
    {
        Msg msg1 = new Msg("hello sealong", Msg.TYPE_RECEIVE);
        msgList.add(msg1);
        Msg msg2 = new Msg("hello peipei", Msg.TYPE_SEND);
        msgList.add(msg2);
        Msg msg = new Msg("What are you doing", Msg.TYPE_RECEIVE);
        msgList.add(msg);
    }

    @Override
    public void addMessage(String msg, int type)
    {

        Msg msg1 = new Msg(msg, type);
        Log.i("tyty","msg = "+msg+"  type = "+type);
        msgList.add(msg1);
        if(msgList.size()>4)
        {
            msgList.remove(0);

        }
        mUiHandler.obtainMessage().sendToTarget();

    }


}
