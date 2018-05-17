package com.realm.motago;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Skyyao on 2018\5\17 0017.
 */

public class HelpUtil
{


    public static String getAndroidOsSN()
    {
        String ret;
        try
        {
            Method systemProperties_get = Class.forName("android.os.SystemProperties").getMethod("get", String.class);
            if ((ret = (String) systemProperties_get.invoke(null, "ro.boot.serialno")) != null)
                return ret;
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

        return "";
    }

/**  
     * 根据wifi信息获取本地mac  
     * @param context  
     * @return  
     */  
    public static String getLocalMacAddressFromWifiInfo(Context context){  
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);  
        WifiInfo winfo = wifi.getConnectionInfo();  
        String mac =  winfo.getMacAddress();  
        return mac;  
    }
	
    /**
     * 根据IP地址获取MAC地址
     *
     * @return
     */
    public static String getMacAddress()
    {
        String strMacAddr = null;
        try
        {
            // 获得IpD地址
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip)
                    .getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++)
            {
                if (i != 0)
                {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (Exception e)
        {

        }

        return strMacAddr;
    }

    /**
     * 获取移动设备本地IP
     *
     * @return
     */
    private static InetAddress getLocalInetAddress()
    {
        InetAddress ip = null;
        try
        {
            // 列举
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface.hasMoreElements())
            {// 是否还有元素
                NetworkInterface ni = (NetworkInterface) en_netInterface
                        .nextElement();// 得到下一个元素
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();// 得到一个ip地址的列举
                while (en_ip.hasMoreElements())
                {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
                }

                if (ip != null)
                {
                    break;
                }
            }
        } catch (SocketException e)
        {

            e.printStackTrace();
        }
        return ip;
    }
}
