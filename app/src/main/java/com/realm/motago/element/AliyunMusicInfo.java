package com.realm.motago.element;

import java.util.Date;

/**
 * Created by Skyyao on 2018\5\12 0012.
 */

public class AliyunMusicInfo
{
    private int playTime;
    private int seqNum;
    private boolean loved;
    private int cacheStatus;
    private int outId;
    private String artist;
    private String duration;
    private int theChannelId;
    private String collectionName;
    private int collectionId;
    private int channelId;
    private int providerId;
    private String ttsUrl;
    private int id;
    private int itemType;
    private String name;
    private long expiresIn;
    private String logo;
    private String provider;
    private int playMode;


    public AliyunMusicInfo()
    {
    }

    public int getPlayTime()
    {
        return playTime;
    }

    public void setPlayTime(int playTime)
    {
        this.playTime = playTime;
    }

    public int getSeqNum()
    {
        return seqNum;
    }

    public void setSeqNum(int seqNum)
    {
        this.seqNum = seqNum;
    }

    public boolean isLoved()
    {
        return loved;
    }

    public void setLoved(boolean loved)
    {
        this.loved = loved;
    }

    public int getCacheStatus()
    {
        return cacheStatus;
    }

    public void setCacheStatus(int cacheStatus)
    {
        this.cacheStatus = cacheStatus;
    }

    public int getOutId()
    {
        return outId;
    }

    public void setOutId(int outId)
    {
        this.outId = outId;
    }

    public String getArtist()
    {
        return artist;
    }

    public void setArtist(String artist)
    {
        this.artist = artist;
    }

    public String getDuration()
    {
        return duration;
    }

    public void setDuration(String duration)
    {
        this.duration = duration;
    }

    public int getTheChannelId()
    {
        return theChannelId;
    }

    public void setTheChannelId(int theChannelId)
    {
        this.theChannelId = theChannelId;
    }

    public String getCollectionName()
    {
        return collectionName;
    }

    public void setCollectionName(String collectionName)
    {
        this.collectionName = collectionName;
    }

    public int getCollectionId()
    {
        return collectionId;
    }

    public void setCollectionId(int collectionId)
    {
        this.collectionId = collectionId;
    }

    public int getChannelId()
    {
        return channelId;
    }

    public void setChannelId(int channelId)
    {
        this.channelId = channelId;
    }

    public int getProviderId()
    {
        return providerId;
    }

    public void setProviderId(int providerId)
    {
        this.providerId = providerId;
    }

    public String getTtsUrl()
    {
        return ttsUrl;
    }

    public void setTtsUrl(String ttsUrl)
    {
        this.ttsUrl = ttsUrl;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getItemType()
    {
        return itemType;
    }

    public void setItemType(int itemType)
    {
        this.itemType = itemType;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public long getExpiresIn()
    {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn)
    {
        this.expiresIn = expiresIn;
    }

    public String getLogo()
    {
        return logo;
    }

    public void setLogo(String logo)
    {
        this.logo = logo;
    }

    public String getProvider()
    {
        return provider;
    }

    public void setProvider(String provider)
    {
        this.provider = provider;
    }

    public int getPlayMode()
    {
        return playMode;
    }

    public void setPlayMode(int playMode)
    {
        this.playMode = playMode;
    }


    @Override
    public String toString()
    {
        return "AliyunMusicInfo{" +
                "playTime=" + playTime +
                ", seqNum=" + seqNum +
                ", loved=" + loved +
                ", cacheStatus=" + cacheStatus +
                ", outId=" + outId +
                ", artist='" + artist + '\'' +
                ", duration='" + duration + '\'' +
                ", theChannelId=" + theChannelId +
                ", collectionName='" + collectionName + '\'' +
                ", collectionId=" + collectionId +
                ", channelId=" + channelId +
                ", providerId=" + providerId +
                ", ttsUrl='" + ttsUrl + '\'' +
                ", id=" + id +
                ", itemType=" + itemType +
                ", name='" + name + '\'' +
                ", expiresIn=" + expiresIn +
                ", logo='" + logo + '\'' +
                ", provider='" + provider + '\'' +
                ", playMode=" + playMode +
                '}';
    }
}
