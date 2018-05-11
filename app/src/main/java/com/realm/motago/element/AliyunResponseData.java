package com.realm.motago.element;

import java.util.List;

/**
 * Created by Skyyao on 2018\5\11 0011.
 */

public class AliyunResponseData
{
    private  int status_code;
    private  int biz_code;
    private int  finish;
    private String asr_ret;
    private  String uuid;
    private  AliyunServiceData service_data;

    public AliyunResponseData()
    {
    }

    public AliyunResponseData(int status_code, int biz_code, int finish, String asr_ret, String uuid, AliyunServiceData service_data)
    {
        this.status_code = status_code;
        this.biz_code = biz_code;
        this.finish = finish;
        this.asr_ret = asr_ret;
        this.uuid = uuid;
        this.service_data = service_data;
    }

    public int getmStateCode()
    {
        return status_code;
    }

    public int getmBizCode()
    {
        return biz_code;
    }

    public int getFinishiCode()
    {
        return finish;
    }

    public String getAskRet()
    {
        return asr_ret;
    }

    public String getUuid()
    {
        return uuid;
    }

    public AliyunServiceData getData()
    {
        return service_data;
    }

    public void setStatus_code(int status_code)
    {
        this.status_code = status_code;
    }

    public void setBiz_code(int biz_code)
    {
        this.biz_code = biz_code;
    }

    public void setFinish(int finish)
    {
        this.finish = finish;
    }

    public void setAsr_ret(String asr_ret)
    {
        this.asr_ret = asr_ret;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    public void setService_data(AliyunServiceData service_data)
    {
        this.service_data = service_data;
    }

    @Override
    public String toString()
    {
        return "AliyunResponseData{" +
                "status_code=" + status_code +
                ", biz_code=" + biz_code +
                ", finish=" + finish +
                ", asr_ret='" + asr_ret + '\'' +
                ", uuid='" + uuid + '\'' +
                ", service_data=" + service_data +
                '}';
    }

    public class AliyunServiceData
    {


//       private List<AliyunTTsData> ttsUrl;
//
//        public AliyunServiceData()
//        {
//        }
//
//        public AliyunServiceData(List<AliyunTTsData> ttsUrl)
//        {
//            this.ttsUrl = ttsUrl;
//        }
//
//        public List<AliyunTTsData> getTtsUrl()
//        {
//            return ttsUrl;
//        }
//
//        public void setTtsUrl(List<AliyunTTsData> ttsUrl)
//        {
//            this.ttsUrl = ttsUrl;
//        }
//        public  void  addTtsUrl(AliyunTTsData ttsUrl)
//        {
//            this.ttsUrl.add(ttsUrl);
//        }


        public AliyunServiceData()
        {
        }

        public AliyunServiceData(String ttsUrl)
        {
            this.ttsUrl = ttsUrl;
        }

        private  String ttsUrl;

        public String getTtsUrl()
        {
            return ttsUrl;
        }

        public void setTtsUrl(String ttsUrl)
        {
            this.ttsUrl = ttsUrl;
        }

        @Override
        public String toString()
        {
            return "AliyunServiceData{" +
                    "ttsUrl='" + ttsUrl + '\'' +
                    '}';
        }
    }
    public  class  AliyunTTsData
    {
        private String tts_text;
        private String tts_url;

        public AliyunTTsData()
        {
        }

        public AliyunTTsData(String tts_text, String tts_url)
        {
            this.tts_text = tts_text;
            this.tts_url = tts_url;
        }

        public String gettts_text()
        {
            return tts_text;
        }

        public String gettts_url()
        {
            return tts_url;
        }

        public void settts_text(String tts_text)
        {
            this.tts_text = tts_text;
        }

        public void settts_url(String tts_url)
        {
            this.tts_url = tts_url;
        }


    }

}

