package com.realm.motago.element;

/**
 * Created by Skyyao on 2018\5\11 0011.
 */

public class TestJson
{
    private String status_code;
    private String biz_code;
    private String asr_ret;


    public TestJson()
    {
    }

    public TestJson(String status_code, String biz_code, String asr_ret)
    {
        this.status_code = status_code;
        this.biz_code = biz_code;
        this.asr_ret = asr_ret;
    }

    public String getStatus_code()
    {
        return status_code;
    }

    public void setStatus_code(String status_code)
    {
        this.status_code = status_code;
    }

    public String getBiz_code()
    {
        return biz_code;
    }

    public void setBiz_code(String biz_code)
    {
        this.biz_code = biz_code;
    }

    public String getAsr_ret()
    {
        return asr_ret;
    }

    public void setAsr_ret(String asr_ret)
    {
        this.asr_ret = asr_ret;
    }

    @Override
    public String toString()
    {
        return "TestJson{" +
                "status_code='" + status_code + '\'' +
                ", biz_code='" + biz_code + '\'' +
                ", asr_ret='" + asr_ret + '\'' +
                '}';
    }
}
