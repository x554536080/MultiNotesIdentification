package com.example.xds.multinotesidentification.util;

import android.util.Log;

import com.example.xds.multinotesidentification.model.Complex;
import com.example.xds.multinotesidentification.presenter.RecordThread;

import java.util.Arrays;

public class FFTUtil {

    public static void fft(Complex[] complexes, int N) {
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

    //复数值赋给数组
    public static void assignToArray(int[][] sort, int length) {
        for (int i = 0; i < length / 2; i++) {
            sort[i][0] = i;
            sort[i][1] = RecordThread.complexes[i].getIntValue();
        }
    }

    //排序数组
    public static void sort(int[][] sort, int length) {
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
    }

    //找到前几个峰值的下标与幅值二维数组
    public static int[][] findTopNPeaks(int[][] array, int N) {
        int[][] peaks = new int[array.length][2];
        int p = 0;
        for (int i = 0; i < array.length; i++) {
            if (i != 0 && i != array.length - 1)
                if (array[i][1] > 50000)
                    if (array[i + 1][1] < array[i][1] && array[i - 1][1] < array[i][1]) {
                        peaks[p][0] = i;
                        peaks[p][1] = array[i][1];
                        p++;
                    }
        }
        sort(peaks, peaks.length);
        return Arrays.copyOfRange(peaks, 0, N);
    }

    //C3-C6基本准确，其余部分要分块识别
    public static float findFirstPeakBy3to2(int[][] array, int length) {
        float result = 0;
        for (int i = 0; i < array.length; i++)
            for (int j = 0; j < array.length; j++) {
                if (array[i][1] == 0) continue;
                float ratio = (float) array[j][0] / (float) array[i][0];
                if (ratio > 1.49f && ratio < 1.51f) {
                    float resultHere = array[i][0] / 2;
                    if (result == 0)
                        result = resultHere;
                    if (resultHere < result)
                        result = resultHere;
                }
            }
        Log.i("result", result * 2 * 8000 / length + "");
        return result;
    }

//    public static int method2(int[][] array, int length) {
//
//    }

    public static int findFirstPeak(int[][] array, int length, int biggest) {
        int peakPos = 0;

        double maxDB = 80000000;
        double maxDBThisTime = 10 * Math.log10(biggest / maxDB);
        double[] volumeInDB = new double[length / 2];

        for (int i = 0; i < length / 2; i++) {
            volumeInDB[i] = 10 * Math.log10(array[i][1] / maxDB);
        }

        boolean isBigger;
        for (int i = 20; i < length / 2 - 15; i++) {
            isBigger = false;
            //如果值比较高
            if (volumeInDB[i] > (maxDBThisTime - 14)) {
                //如果是个顶点
                if (volumeInDB[i + 1] < volumeInDB[i] && volumeInDB[i - 1] < volumeInDB[i])
                //如果比周围高出许多
//                    if (volumeInDB[i] - volumeInDB[i - 3] > 15 && volumeInDB[i] - volumeInDB[i + 3] > 15) {
                {
                    //如果周围领域他最大          //且一直在降 || volumeInDB[i + j + 1] - volumeInDB[i + j] > 0
                    int range = 0;
                    if (i < 350) range = 3;
                    if (i > 350 && i < 800) range = 12;
                    if (i > 800) range = 25;
                    for (int j = 0; j < 10; j++) {
                        isBigger = true;
                        if (volumeInDB[i - j] > volumeInDB[i] || volumeInDB[i + j] > volumeInDB[i]) {
                            isBigger = false;
                            break;
//                                }
                        }
                    }
                }
            }
            if (isBigger) {
                peakPos = i;
                return peakPos;
            }
        }
        return 999;


//        for (int i = 0; i < length / 2 - 1; i++) {
//            if (volumeInDB[i + 1] < volumeInDB[i] &&
//                    (((float) i * 8000 / length) > 20)
//            ) {
//                peakPos = i;
//                return peakPos;
//            }
//        }
//
//
//        return peakPos;
    }

}
