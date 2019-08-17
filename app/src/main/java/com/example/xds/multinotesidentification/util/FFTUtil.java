package com.example.xds.multinotesidentification.util;

import com.example.xds.multinotesidentification.model.Complex;
import com.example.xds.multinotesidentification.presenter.RecordThread;

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

    public static void sort(int[][] sort,int length) {
        for (int i = 0; i < length / 2; i++) {
            sort[i][0] = i;
            sort[i][1] = RecordThread.complexes[i].getIntValue();
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
    }
}
