package com.example.xds.multinotesidentification.presenter;


import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.example.xds.multinotesidentification.model.Complex;

import org.jtransforms.fft.DoubleFFT_1D;

public class RecordThread extends Thread {

    DoubleFFT_1D fft_1D;

    PresenterImpl presenter;
    AudioRecord audioRecord;
    int minBufferSize;
    Complex[] complexes;

    String outTemp;
    int point;

    boolean isRecording;

    RecordThread(PresenterImpl presenter) {
        this.presenter = presenter;
        isRecording = false;
        minBufferSize = AudioRecord.getMinBufferSize(8000,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                8000, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, minBufferSize * 16);
        Log.d("BufferSize", minBufferSize + "");
    }

    @Override
    public void run() {

        short[] buffer = new short[minBufferSize * 16];
        int length = up2int(16 * minBufferSize);
        audioRecord.startRecording();

        while (isRecording) {
            //平移提取
            System.arraycopy(buffer, 4 * minBufferSize, buffer, 0, minBufferSize * 12);
            audioRecord.read(buffer, minBufferSize * 12, minBufferSize * 4);

            //正常提取
//            int length = up2int(audioRecord.read(buffer, 0, minBufferSize*8));

            fft_1D = new DoubleFFT_1D(length);
            complexes = new Complex[length];

            //自用FFT的处理
            for (int i = 0; i < length; i++) {
                complexes[i] = new Complex(buffer[i]);
            }
            fft(complexes, length);

            //排序部分
            int[][] sort = new int[length / 2][2];
            for (int i = 0; i < length / 2; i++) {
                sort[i][0] = i;
                sort[i][1] = complexes[i].getIntValue();
            }
            for (int j = 0; j < length/2; j++) {
                for (int i = 0; i < length/2 - 1 - j; i++) {
                    if (sort[i][1] < sort[i + 1][1]) {
                        int a = sort[i + 1][0];
                        int b = sort[i + 1][1];
                        sort[i + 1][0] = sort[i][0];
                        sort[i + 1][1] = sort[i][1];
                        sort[i][0] = a;
                        sort[i][1] = b;
                    }
                }
            }

            //第三方库的傅里叶处理
//            double[] data = new double[length * 2];
//            for (int i = 0; i < length * 2; i++) {
//                if (i % 2 == 0) {
//                    data[i] = buffer[i / 2];
//                } else {
//                    data[i] = 0;
//                }
//            }
//            fft_1D.complexForward(data);

            //自用FFT的查找最大算法
//            int max = 0;
//            for (int i = 1; i < length / 2; i++) {
//                int IntNum = complexes[i].getIntValue();
//                if (complexes[max].getIntValue() < IntNum) {
//                    max = i;
//                }
//            }

            //第三方库的查找最大算法
//            int max = 0;
//            for (int i = 1; i < length * 2; i++) {
//                double doubleData;
//                if (i % 2 == 0) {
//                    doubleData = data[i];
//                    if (data[max] < doubleData) {
//                        max = i;
//                    }
//                }
//            }
//            max /= 2;


            //计算频率和输出显示
            outTemp = sort[0][0] * 8000.0 / (length) + "";
            point = outTemp.indexOf(".");
            if (outTemp.length() - point > 2)
                update(outTemp.substring(0, point + 3) + "Hz");
            else update(outTemp.substring(0, point + 2) + "0" + "Hz");
        }
    }


    private int up2int(int in) {
        int ret = 1;
        while (ret <= in) {
            ret = ret << 1;
        }
        return ret >> 1;
    }

    public void fft(Complex[] complexes, int N) {
        double pi = 3.1415926;
        int f, m, N2, nm, i, k, j, L;//L:运算级数
        double p;
        int e2, le, B, ip;
        Complex w = new Complex();
        Complex t = new Complex();
        N2 = N / 2;//每一级中蝶形的个数,同时也代表m位二进制数最高位的十进制权值
        f = N;//f是为了求流程的级数而设立的
        for (m = 1; (f = f / 2) != 1; m++) ;//得到流程图的共几级
        nm = N - 2;
        j = N2;
        /******倒序运算——雷德算法******/
        for (i = 1; i <= nm; i++) {
            if (i < j)//防止重复交换
            {
                t = complexes[j];
                complexes[j] = complexes[i];
                complexes[i] = t;
            }
            k = N2;
            while (j >= k) {
                j = j - k;
                k = k / 2;
            }
            j = j + k;
        }
        /******蝶形图计算部分******/
        for (L = 1; L <= m; L++)                                    //从第1级到第m级
        {
            e2 = (int) Math.pow(2, L);
            //e2=(int)2.pow(L);
            le = e2 + 1;
            B = e2 / 2;
            for (j = 0; j < B; j++)                                   //j从0到2^(L-1)-1
            {
                p = 2 * pi / e2;
                w.real = Math.cos(p * j);
                //w.real=Math.cos((double)p*j);                                   //系数W
                w.image = Math.sin(p * j) * -1;
                //w.imag = -sin(p*j);
                for (i = j; i < N; i = i + e2)                                //计算具有相同系数的数据
                {
                    ip = i + B;                                            //对应蝶形的数据间隔为2^(L-1)
                    t = complexes[ip].cc(w);
                    complexes[ip] = complexes[i].cut(t);
                    complexes[i] = complexes[i].sum(t);
                }
            }
        }
    }

    public void update(final String s) {
        ((Activity) presenter.context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                presenter.onModifyShowFreqText(s);
            }
        });
    }


}
