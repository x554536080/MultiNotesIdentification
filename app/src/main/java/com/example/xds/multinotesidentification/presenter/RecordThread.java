package com.example.xds.multinotesidentification.presenter;


import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.example.xds.multinotesidentification.model.Complex;
import com.example.xds.multinotesidentification.model.MusicalNote;

import org.jtransforms.fft.DoubleFFT_1D;

import java.text.NumberFormat;

public class RecordThread extends Thread {

//    DoubleFFT_1D fft_1D;

    PresenterImpl presenter;
    AudioRecord audioRecord;
    boolean isRecording;
    int currentActivity;


    int minBufferSize;
    Complex complexes[];

    String outTemp;
    int point;


    RecordThread(PresenterImpl presenter) {
        this.presenter = presenter;
        currentActivity = presenter.currentActivity;
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

        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(2);

        while (isRecording) {
            //平移提取
            System.arraycopy(buffer, 4 * minBufferSize, buffer, 0, minBufferSize * 12);
            audioRecord.read(buffer, minBufferSize * 12, minBufferSize * 4);

            //正常提取
//            int length = up2int(audioRecord.read(buffer, 0, minBufferSize*8));

//            fft_1D = new DoubleFFT_1D(length);
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
            for (int j = 0; j < length / 2; j++) {
                for (int i = 0; i < length / 2 - 1 - j; i++) {
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


            //控制音量足够时才更新
            if (sort[0][1] < 200000) {
                continue;
            }
            //Log.d("maxSort",sort[0][1]+"");

            /**多音识别部分,活动号等于3**/
            if (currentActivity == 3) {
                String strNote[] = new String[3];
                double strMaxHz[] = new double[3];
                double errorPercentage[] = new double[3];
                int volume[] = new int[3];

                //黄老师方法
                int i = 0, j = 0;
                while (i < 3 && j < 256) {
                    double MaxHz = sort[j][0] * 8000 / (length);
                    strMaxHz[i] = MaxHz;
                    strNote[i] = MusicalNote.maxLocation(MaxHz);
                    errorPercentage[i] = MusicalNote.percentage;
                    //errorPercentage[i] = MusicalNote.percentage;
                    if (i > 0) {
                        int k = i - 1;
                        while (k >= 0) {
                            if (strNote[i].equals(strNote[k])) {
                                j++;
                                break;
                            }
                            k--;
                        }
                        if (k < 0) {
                            i++;
                            j++;
                        }
                    } else {
                        i++;
                        j++;
                    }
                }

                String error0 = errorPercentage[0] + "";
                String error1 = errorPercentage[1] + "";
                String error2 = errorPercentage[2] + "";
                String outError0 = "";
                String outError1 = "";
                String outError2 = "";

                if (error0.length() > 5) {
                    if (errorPercentage[0] < 0) {
                        outError0 = "-" + error0.substring(3, 5) + "%";
                    } else {
                        outError0 = "+" + error0.substring(2, 4) + "%";
                    }
                }
                if (error1.length() > 5) {
                    if (errorPercentage[1] < 0) {
                        outError1 = "-" + error1.substring(3, 5) + "%";
                    } else {
                        outError1 = "+" + error1.substring(2, 4) + "%";
                    }
                }
                if (error2.length() > 5) {
                    if (errorPercentage[2] < 0) {
                        outError2 = "-" + error2.substring(3, 5) + "%";
                    } else {
                        outError2 = "+" + error2.substring(2, 4) + "%";
                    }
                }
                updateMultiNoteA(strNote[0], numberFormat.format(strMaxHz[0]) + "Hz", outError0);
                updateMultiNoteB(strNote[1], numberFormat.format(strMaxHz[1]) + "Hz", outError1);
                updateMultiNoteC(strNote[2], numberFormat.format(strMaxHz[2]) + "Hz", outError2);


//                //过滤杂音，提取峰值部分
//                double maxDB = 80000000;
//                double[] volumeInDB = new double[length / 2];
//                //Log.d("maxDB",10 * (int) Math.log10(sort[0][1] / maxDB)+"");
//                for (int i = 0; i < length / 2; i++) {
//                    volumeInDB[i] = 10 * Math.log10(complexes[i].getIntValue() / maxDB);
//                    if (volumeInDB[i] <= 10 * (int) Math.log10(sort[0][1] / maxDB) - 20) {
//                        volumeInDB[i] = 10 * (int) Math.log10(sort[0][1] / maxDB) - 20;
//                    }
//                }
//
//                //峰值确定算法
//                double peak[][] = new double[length / 2][2];
//                peak[0][0] = 0;
//                peak[1][0] = 0;
//                peak[2][0] = 0;
//                peak[0][1] = 0;
//                peak[1][1] = 0;
//                peak[2][1] = 0;
//                int peakNum = 0;
//                for (int i = 3; i < length / 2 - 3; i++) {
//                    if ((volumeInDB[i] - volumeInDB[i - 1] > 0) && (volumeInDB[i] - volumeInDB[i + 1] > 0) &&
//                            (volumeInDB[i - 1] - volumeInDB[i - 2] > 0) && (volumeInDB[i + 1] - volumeInDB[i + 2] > 0)) {
//                        peak[peakNum][0] = i;
//                        peak[peakNum][1] = volumeInDB[i];
//                        peakNum++;
//                    }
//                }
//
//                Log.d("xds", peakNum + "");
//                //peak数组排序
//
//                //从峰值提取多音
//
//                strMaxHz[0] = peak[0][0] * 8000 / (length);
//                strMaxHz[1] = peak[1][0] * 8000 / (length);
//                strMaxHz[2] = peak[2][0] * 8000 / (length);
//                strNote[0] = MusicalNote.maxLocation(strMaxHz[0]);
//                strNote[1] = MusicalNote.maxLocation(strMaxHz[1]);
//                strNote[2] = MusicalNote.maxLocation(strMaxHz[2]);
//
//
//
//                if (strMaxHz[0] != 0)
//                    updateMultiNoteA(strNote[0], numberFormat.format(strMaxHz[0]) + "Hz");
//                if (strMaxHz[1] != 0)
//                    updateMultiNoteB(strNote[1], numberFormat.format(strMaxHz[1]) + "Hz");
//                if (strMaxHz[2] != 0)
//                    updateMultiNoteC(strNote[2], numberFormat.format(strMaxHz[2]) + "Hz");

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

            /**频率显示部分,活动号等于2**/
            if (currentActivity == 2) {
                outTemp = sort[0][0] * 8000.0 / (length) + "";
                point = outTemp.indexOf(".");
                if (outTemp.length() - point > 2)
                    updateShowFreq(outTemp.substring(0, point + 3) + "Hz");
                else updateShowFreq(outTemp.substring(0, point + 2) + "0" + "Hz");
            }


            /**音名显示部分,活动号等于1**/
            if (currentActivity == 1) {
                String note = MusicalNote.maxLocation(sort[0][0] * 8000.0 / (length));
                double errorNum = MusicalNote.percentage;
                String error = errorNum + "";
                String outError = "";
                if (error.length() > 5) {
                    if (errorNum < 0) {
                        outError = "-" + error.substring(3, 5) + "%";
                    } else {
                        outError = "+" + error.substring(2, 4) + "%";
                    }
                }
                updateSingleNote(note, outError);
            }
        }


        audioRecord.stop();

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

    public void updateSingleNote(final String n1, final String e1) {
        ((Activity) presenter.context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                presenter.onModifySingleNote(n1, e1);
            }
        });
    }

    public void updateShowFreq(final String s) {
        ((Activity) presenter.context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                presenter.onModifyShowFreqText(s);
            }
        });
    }

    public void updateMultiNoteA(final String n, final String f, final String e) {
        ((Activity) presenter.context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                presenter.onModifyMultiNoteTextA(n, f, e);
            }
        });
    }

    public void updateMultiNoteB(final String n, final String f, final String e) {
        ((Activity) presenter.context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                presenter.onModifyMultiNoteTextB(n, f, e);
            }
        });
    }

    public void updateMultiNoteC(final String n, final String f, final String e) {
        ((Activity) presenter.context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                presenter.onModifyMultiNoteTextC(n, f, e);
            }
        });
    }
}


