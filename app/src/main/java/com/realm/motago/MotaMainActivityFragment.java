package com.realm.motago;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.realm.motago.adapter.MsgAdapter;
import com.realm.motago.element.Msg;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MotaMainActivityFragment extends Fragment
{

    RecyclerView recyclerView;
    private MsgAdapter adapter;
    List<Msg> msgList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initMsg();

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
}
