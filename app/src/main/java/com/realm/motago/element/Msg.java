package com.realm.motago.element;

/**
 * Created by Skyyao on 2018\5\4 0004.
 */

public class Msg
{
    public static final int TYPE_RECEIVE = 0;
    public static final int TYPE_SEND = 1;
    private String content;
    private int type;

    public Msg(String content, int type)
    {
        this.content = content;
        this.type = type;
    }

    public String getContent()
    {
        return content;
    }

    public int getType()
    {
        return type;
    }

    @Override
    public String toString()
    {
        return "content = "+content+"; type"+type;
    }
}