package com.example.bluetoothtest;

import android.content.Context;

public class MyDataClss {
    private Context mcontext;
    private char u8Len;                                                                    // 1字节报文长度(协议和数据段的数据长度)
    private char u8Function;                                                            // 1字节功能命令
    private char[] u8Buf = new char[100];
    //    union{
//        u8 cmd;
//        u8 u8Buf[APP_LAYER_DATA_MAX_BYTE_LEN];										// 缓存内容数据
//    }blueData;															// 应用层结构体
    private char u8CheckSum;

    public MyDataClss(Context context) {
        mcontext = context;
    }

    public MyDataClss(char[] array, char lengh) {
        u8Len = array[0];
        u8Function = array[1];
        for (int i = 2; i < lengh; i++) {
            u8Buf[i - 2] = array[i];

        }
    }

    public char getU8Function() {
        return u8Function;
    }

    public char getU8Len() {
        return u8Len;
    }

    public char[] getU8Buf() {
        return u8Buf;
    }
}
