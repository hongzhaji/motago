package com.realm.motago.element;

/**
 * Created by Skyyao on 2018\5\17 0017.
 */

public class MusicChannel
{
    public String auid;
    public String channelId;
    public String channelLogo;
    public String channelName;
    public String channelType;
    public String detailEditable;
    public String removable;
    public String seqNum;
    public String supportCache;


    @Override
    public String toString()
    {
        return "MusicChannel{" +
                "auid='" + auid + '\'' +
                ", channelId='" + channelId + '\'' +
                ", channelLogo='" + channelLogo + '\'' +
                ", channelName='" + channelName + '\'' +
                ", channelType='" + channelType + '\'' +
                ", detailEditable='" + detailEditable + '\'' +
                ", removable='" + removable + '\'' +
                ", seqNum='" + seqNum + '\'' +
                ", supportCache='" + supportCache + '\'' +
                '}';
    }
}
