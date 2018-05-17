package com.realm.motago;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.google.android.exoplayer.audio.AudioTrack;
import com.google.android.exoplayer.util.Util;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.realm.motago.manager.INavigationClick;
import mtopsdk.common.util.StringUtils;


import java.util.Hashtable;


/**
 * Created by Skyyao on 2018\5\4 0004.
 */

public class MotaXiaoZhiFragment extends Fragment implements INavigationClick
{
    private ImageView qrImageView;
    private Bitmap qrBitmap;

    public MotaXiaoZhiFragment()
    {

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                createQR();
                if (qrBitmap != null)
                {
                    Log.i("tyty", "height = " + qrBitmap.getHeight() + "  width = " + qrBitmap.getWidth());
                } else
                {
                    Log.i("tyty", "null for qr");
                }
            }
        }).start();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_mota_xiaozhi, container, false);
        qrImageView = v.findViewById(R.id.qr_iv);


        return v;
    }

    @Override
    public void onHiddenChanged(boolean hidden)
    {
        super.onHiddenChanged(hidden);
        if (!hidden)
        {
            if (qrBitmap != null)
            {
                Log.i("tyty", "height = " + qrBitmap.getHeight() + "  width = " + qrBitmap.getWidth());
            } else
            {
                Log.i("tyty", "null for qr");
            }
        }
        qrImageView.setImageBitmap(qrBitmap);
    }

    @Override
    public boolean onNavigationCLick()
    {
        return true;
    }

    private void createQR()
    {
        String val = "";
        try
        {

            // http://smart.aliyun.com/download.htm?ver=2.0&model=ALINKTEST_ENTERTAINMENT_ATALK_RTOS_TEST&mac=11:12:F2:F9:F9:F8&sn=11:12:F2:F9:F9:F8
            val = "http://smart.aliyun.com/download.htm?ver=2.0&model=" + "ALINKTEST_ENTERTAINMENT_ATALK_RTOS_TEST"
                    + "&mac=" + HelpUtil.getMacAddress()
                    + "&sn=" + HelpUtil.getAndroidOsSN();

        } catch (Exception e)
        {
            e.printStackTrace();

            // APPLog.e(TAG, "生成二维码数据出错," + e.getMessage());
        }

        // APPLog.i(TAG, "val=" + val);


        qrBitmap = create2DCode(val, 140);
        // qr = Utils.addLogo(qr, BitmapFactory.decodeResource(getResources(), R.drawable.qrlogo));
    }


    private Bitmap create2DCode(String str, int qrWidth)
    {

        try
        {
            Hashtable hints = new Hashtable();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.MARGIN, 1);
            //设置二维码边的空度，非负数
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // 容错级别尽量设置到最高，以免出现无法解析的情况

            BitMatrix bitMatrix = (new MultiFormatWriter()).encode(str, BarcodeFormat.QR_CODE, qrWidth, qrWidth, hints);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            int[] pixels = new int[width * height];

            for (int y = 0; y < height; ++y)
            {
                for (int x = 0; x < width; ++x)
                {
                    if (bitMatrix.get(x, y))
                    {
                        pixels[y * width + x] = 0xFF000000;  // 图案的颜色
                    } else
                    {
                        pixels[y * width + x] = 0xffffffff;  // 背景色
                    }
                }
            }
            // LOGW(TAG, "create2DCode Bitmap width="+width);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

            return bitmap;
        } catch (WriterException e)
        {
            e.printStackTrace();
            //LOGE(TAG, "创建二维码失败, "+e.getMessage());
            return null;
        }
    }
}
