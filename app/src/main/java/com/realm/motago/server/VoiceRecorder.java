package com.realm.motago.server;

import android.os.Environment;
import android.util.Log;

import com.aliyun.alink.pal.business.ALinkManager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by guoweisong on 17/10/31.
 */
public class VoiceRecorder
{

    public VoiceRecorder()
    {
        init();
    }

    private ALinkManager.onRecordListener mRecordListener;

    private void init()
    {
        mkDirIfNotExist(dirPath);
        mkDirIfNotExist(wavsPath);
        mRecordListener = new ALinkManager.onRecordListener()
        {
            @Override
            public void onStartRecording()
            {
                executor.execute(new Runnable()
                {
                    public void run()
                    {

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS");
                        String filename = dateFormat.format(new Date());
                        waveFilePath = wavsPath + filename + ".wav";
                        pcmFilePath = wavsPath + filename + ".pcm";
                        createFile(pcmFilePath);
                        try
                        {
                            os = new BufferedOutputStream(new FileOutputStream(pcmFilePath));
                        } catch (IOException e)
                        {
                            Log.e("AudioRecordUtil", "write data error", e);

                        }

                    }
                });
            }

            @Override
            public void onRecordVoiceData(final short[] shorts, int i)
            {
                executor.execute(new Runnable()
                {
                    public void run()
                    {
                        byte[] b = getBytes(shorts);
                        try
                        {
                            os.write(b);
                        } catch (IOException e)
                        {
                            Log.e("AudioRecordUtil", "write data error", e);
                        }
                    }
                });
            }

            @Override
            public void onRecordVoiceDetected(byte[] bytes, int i)
            {

            }

            @Override
            public void onStopRecord()
            {
                executor.execute(new Runnable()
                {
                    public void run()
                    {
                        if (os != null)
                        {
                            try
                            {
                                os.close();
                            } catch (Exception var4)
                            {

                            }
                        }
                        convertWaveFile(pcmFilePath, waveFilePath);
                        File pcmFile = new File(pcmFilePath);

                        try
                        {
                            pcmFile.delete();
                        } catch (Exception var3)
                        {

                        }
                    }
                });
            }
        };
    }

    public ALinkManager.onRecordListener getRecordListener()
    {
        return mRecordListener;
    }

    public static String dirPath = Environment.getExternalStorageDirectory().getPath() + "/palRecorder/";
    public static String wavsPath = dirPath + "palRecordFiles/";
    public static String waveFilePath;
    public static String pcmFilePath;
    private ExecutorService executor = Executors.newScheduledThreadPool(5);

    public void mkDirIfNotExist(String path)
    {
        File dir = new File(path);
        if (!dir.exists())
        {
            dir.mkdirs();
        }
    }

    private void createFile(String path)
    {
        File baseFile = new File(path);

        if (baseFile.isDirectory() && !baseFile.exists())
            baseFile.mkdirs();
        if (baseFile.isFile() && !baseFile.exists())
        {
            try
            {
                baseFile.createNewFile();
            } catch (IOException e)
            {
                Log.e("TAG", "create file error", e);
            }
        }
    }

    private OutputStream os;


    private byte[] getBytes(short s[])
    {

        if (s == null)
        {
            return null;
        }

        int nLen = s.length;
        if (nLen == 0)
        {
            return null;
        }

        byte[] buf = new byte[nLen + nLen];
        int nIndex = 0;
        short sh = 0;
        for (int i = 0; i < nLen; i++)
        {
            sh = s[i];
            buf[nIndex++] = (byte) (sh & 0xff);
            buf[nIndex++] = (byte) (sh >> 8 & 0xff);
        }
        return buf;
    }

    private static int audioRate = 16000;

    // 这里得到可播放的音频文件
    private void convertWaveFile(String pcmPath, String wavPath)
    {
        createFile(wavPath);
        FileInputStream in;
        FileOutputStream out;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = audioRate;
        int channels = 1;
        long byteRate = 16 * audioRate * channels / 8;
        byte[] data = new byte[640];
        try
        {
            in = new FileInputStream(pcmPath);
            out = new FileOutputStream(wavPath);
            totalAudioLen = in.getChannel().size();
            //由于不包括RIFF和WAV
            totalDataLen = totalAudioLen + 36;
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);
            while (in.read(data) != -1)
            {
                out.write(data);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /* 任何一种文件在头部添加相应的头文件才能够确定的表示这种文件的格式，wave是RIFF文件结构，每一部分为一个chunk，其中有RIFF WAVE chunk， FMT Chunk，Fact chunk,Data chunk,其中Fact chunk是可以选择的， */
    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate,
                                     int channels, long byteRate) throws IOException
    {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);//数据大小
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';//WAVE
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        //FMT Chunk
        header[12] = 'f'; // 'fmt '
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';//过渡字节
        //数据大小
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        //编码方式 10H为PCM编码格式
        header[20] = 1; // format = 1
        header[21] = 0;
        //通道数
        header[22] = (byte) channels;
        header[23] = 0;
        //采样率，每个通道的播放速度
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        //音频数据传送速率,采样率*通道数*采样深度/8
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        // 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数
        header[32] = (byte) (1 * 16 / 8);
        header[33] = 0;
        //每个样本的数据位数
        header[34] = 16;
        header[35] = 0;
        //Data chunk
        header[36] = 'd';//data
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }
}
