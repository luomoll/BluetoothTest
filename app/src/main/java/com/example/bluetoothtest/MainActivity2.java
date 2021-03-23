package com.example.bluetoothtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

import java.io.OutputStream;
import java.nio.ByteBuffer;

import android.widget.Toast;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "按钮提示";
    //    Button button;
//    Button test1;
    ByteBuffer reBuffer;
    MyClass myClass = null;
    //    Handler handler = null;
    private TextView textView;
    private TextView textValue;
    private TextView textAngle;

    private EditText editNumber;
    private EditText nameNumber;
    private EditText editVerName;
    private EditText editVerName1;
    private EditText editVerName2;
    private EditText nameVer;
    private EditText nameVer1;
    private EditText nameVer2;
    private EditText editBluetoothName;
    private EditText editJson;

    EditText editText;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private Button sendNumBtn;
    private Button getNumBtn;
    private Button sendVerBtn;
    private Button getVerBtn;
    private Button sendKeyBtn;
    private Button sendBluetoothBtn;
    private Button copyBtn;
    SharedPreferences sharedPreferences;

    private ClipboardManager cm;
    private ClipData mClipData;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    MyDataClss str = (MyDataClss) msg.obj;
                    blueToothData(str);
                    break;
                default:
                    break;
            }
        }
    };

//    private Timer timer=new Timer();

    @SuppressLint({"HandlerLeak", "WorldReadableFiles", "WrongConstant"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        textViewInit();
        readString();
        myClass = (MyClass) getApplication();

        myClass.setHandler(handler);
//        socket = null;
        socket = myClass.getSocket();
        try {
            try {
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception ignored) {

        }
//        byte[] bytes = new byte[8];
//        bytes[0] = (byte) 0xfa;
//        bytes[1] = (byte) 0xde;
//        bytes[2] = (byte) 0x02;
//        bytes[3] = (byte) 0x01;
//        bytes[4] = (byte) 0x01;
//        bytes[5] = (byte) 0x02;
//        bytes[6] = (byte) 0xfe;
//        bytes[7] = (byte) 0xfe;
//
//        TimerTask timerTask=new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    outputStream.write(bytes);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        timer.schedule(timerTask,2000,2000);
        reBuffer = ByteBuffer.allocate(100);

    }

    private void textViewInit() {
        textView = (TextView) findViewById(R.id.battaryValueId);
        textAngle = (TextView) findViewById(R.id.textAngle);
        textValue = (TextView) findViewById(R.id.textValue);

        editNumber = (EditText) findViewById(R.id.editNumber);
        nameNumber = (EditText) findViewById(R.id.nameNumber);
        editVerName = (EditText) findViewById(R.id.editVerName);
        editVerName1 = (EditText) findViewById(R.id.editVerName1);
        editVerName2 = (EditText) findViewById(R.id.editVerName2);
        nameVer = (EditText) findViewById(R.id.nameVer);
        nameVer1 = (EditText) findViewById(R.id.nameVer1);
        nameVer2 = (EditText) findViewById(R.id.nameVer2);
        editBluetoothName = (EditText) findViewById(R.id.editBluetoothName);
        editJson = (EditText) findViewById(R.id.editJson);

        editJson.setGravity(Gravity.TOP);
        editJson.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editJson.setSingleLine(false);
        editJson.setHorizontallyScrolling(false);


        sendNumBtn = (Button) findViewById(R.id.sendNumid);
        getNumBtn = (Button) findViewById(R.id.getNumid);
        sendVerBtn = (Button) findViewById(R.id.sendVerid);
        getVerBtn = (Button) findViewById(R.id.getVerid);
        sendKeyBtn = (Button) findViewById(R.id.sendKeyid);
        sendBluetoothBtn = (Button) findViewById(R.id.sendBluetoothNameid);
        copyBtn = (Button) findViewById((R.id.button2));

//        copyBtn.setFocusable(true);
//        copyBtn.requestFocus();
//        copyBtn.setFocusableInTouchMode(true);
//        copyBtn.requestFocusFromTouch();

        sendNumBtn.setOnClickListener(this);
        getNumBtn.setOnClickListener(this);
        sendVerBtn.setOnClickListener(this);
        getVerBtn.setOnClickListener(this);
        sendKeyBtn.setOnClickListener(this);
        sendBluetoothBtn.setOnClickListener(this);
        copyBtn.setOnClickListener(this);

    }

    private void writeString() {
        try {
            sharedPreferences = getSharedPreferences("peizhi", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("number", editNumber.getText().toString());
            editor.putString("yingjian", editVerName.getText().toString());
            editor.putString("yingjian1", editVerName1.getText().toString());
            editor.putString("yingjian2", editVerName2.getText().toString());
//        editor.putInt("yingjian", 10);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("WrongConstant")
    private void readString() {
        try {
            sharedPreferences = getSharedPreferences("peizhi", 1);
            String str = sharedPreferences.getString("number", "");
            editNumber.setText(str + "");
            String str1 = sharedPreferences.getString("yingjian", "");
            editVerName.setText(str1 + "");
            String str2 = sharedPreferences.getString("yingjian1", "");
            editVerName1.setText(str2 + "");
            String str3 = sharedPreferences.getString("yingjian2", "");
            editVerName2.setText(str3 + "");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sendNumid: {
                Log.d(TAG, "onClick: 1");
                String str = editNumber.getText().toString();
                byte[] bytes = str.getBytes();
                if (bytes.length < 9) {
                    Toast.makeText(MainActivity2.this, "设备编号输入不能小于9位", Toast.LENGTH_SHORT).show();//悬浮提示
                    break;
                }
                byte[] buf = new byte[20];
                buf[0] = (byte) 0xfa;
                buf[1] = (byte) 0xde;
                buf[2] = (byte) 0x0b;
                buf[3] = (byte) 0x06;
                buf[4] = (byte) 0x01;
//                buf[5] = (byte) 0x53;
//                buf[buf[2] + 3] = (byte) (buf[3] + buf[4] + buf[5]);
                buf[buf[2] + 3] = (byte) (buf[3] + buf[4]);
                for (int i = 0; i < buf[2] - 2; i++) {
                    buf[i + 5] = bytes[i];
                    buf[buf[2] + 3] += bytes[i];
                }
                buf[buf[2] + 4] = (byte) 0xfe;
                buf[buf[2] + 5] = (byte) 0xfe;
                if (outputStream != null) {
                    try {
                        outputStream.write(buf, 0, buf[2] + 6);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
            case R.id.getNumid: {
                byte[] buf = new byte[20];
                buf[0] = (byte) 0xfa;
                buf[1] = (byte) 0xde;
                buf[2] = (byte) 0x02;
                buf[3] = (byte) 0x07;
                buf[4] = (byte) 0x01;
                buf[5] = (byte) 0x08;
                buf[buf[2] + 4] = (byte) 0xfe;
                buf[buf[2] + 5] = (byte) 0xfe;
                if (outputStream != null) {
                    try {
                        outputStream.write(buf, 0, buf[2] + 6);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
            case R.id.sendVerid: {
                Log.d(TAG, "onClick: 1");
                String str = editVerName.getText().toString();
                String str1 = editVerName1.getText().toString();
                String str2 = editVerName2.getText().toString();
                int[] data = new int[3];
                if (str.isEmpty()) {
                    str = "";
                    data[0] = 0;
                } else {
                    data[0] = Integer.valueOf(str).intValue();
                }
                if (str1.isEmpty()) {
                    str1 = "";
                    data[1] = 0;
                } else {
                    data[1] = Integer.valueOf(str1).intValue();
                }
                if (str2.isEmpty()) {
                    str2 = "";
                    data[2] = 0;
                } else {
                    data[2] = Integer.valueOf(str2).intValue();
                }
//                byte[] bytes = str.getBytes();
//                if (bytes.length < 4) {
//                    Toast.makeText(MainActivity2.this, "硬件输入为4位", Toast.LENGTH_SHORT).show();//悬浮提示
//                    break;
//                }
                byte[] buf = new byte[20];
                buf[0] = (byte) 0xfa;
                buf[1] = (byte) 0xde;
                buf[2] = (byte) 0x06;
                buf[3] = (byte) 0x06;
                buf[4] = (byte) 0x03;
//                buf[buf[2] + 3] = (byte) (buf[3] + buf[4]);
//                for (int i = 0; i < buf[2] - 3; i++) {
//                    buf[i + 5] = (byte)data[i];
//                    buf[buf[2] + 3] += buf[i+5];
//                }
                buf[5] = (byte) (data[0]);
                buf[6] = (byte) (data[1]);
                buf[7] = (byte) (data[2] / 256);
                buf[8] = (byte) (data[2] % 256);
                buf[buf[2] + 3] = (byte) (buf[3] + buf[4] + buf[5] + buf[6] + buf[7] + buf[8]);
                buf[buf[2] + 4] = (byte) 0xfe;
                buf[buf[2] + 5] = (byte) 0xfe;
                if (outputStream != null) {
                    try {
                        outputStream.write(buf, 0, buf[2] + 6);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.d(TAG, "onClick: 3");
            break;
            case R.id.getVerid: {
                byte[] buf = new byte[20];
                buf[0] = (byte) 0xfa;
                buf[1] = (byte) 0xde;
                buf[2] = (byte) 0x02;
                buf[3] = (byte) 0x07;
                buf[4] = (byte) 0x03;
                buf[5] = (byte) (buf[3] + buf[4]);
                buf[buf[2] + 4] = (byte) 0xfe;
                buf[buf[2] + 5] = (byte) 0xfe;
                if (outputStream != null) {
                    try {
                        outputStream.write(buf, 0, buf[2] + 6);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.d(TAG, "onClick: 4");
            break;
            case R.id.sendKeyid: {
                byte[] buf = new byte[20];
                buf[0] = (byte) 0xfa;
                buf[1] = (byte) 0xde;
                buf[2] = (byte) 0x03;
                buf[3] = (byte) 0x06;
                buf[4] = (byte) 0x07;
                buf[5] = 0;
                buf[6] = (byte) (buf[3] + buf[4]);
                buf[buf[2] + 4] = (byte) 0xfe;
                buf[buf[2] + 5] = (byte) 0xfe;
                if (outputStream != null) {
                    try {
                        outputStream.write(buf, 0, buf[2] + 6);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
            case R.id.sendBluetoothNameid: {
                String str = editBluetoothName.getText().toString();
                byte[] bytes = str.getBytes();
                if (bytes.length < 9) {
                    Toast.makeText(MainActivity2.this, "蓝牙输入不能小于9位", Toast.LENGTH_SHORT).show();//悬浮提示
                    break;
                }
                byte[] buf = new byte[20];
                buf[0] = (byte) 0xfa;
                buf[1] = (byte) 0xde;
                buf[2] = (byte) 0x0b;
                buf[3] = (byte) 0x06;
                buf[4] = (byte) 0x05;
                buf[buf[2] + 3] = (byte) (buf[3] + buf[4]);
                for (int i = 0; i < buf[2] - 2; i++) {
                    buf[i + 5] = bytes[i];
                    buf[buf[2] + 3] += bytes[i];
                }
                buf[buf[2] + 4] = (byte) 0xfe;
                buf[buf[2] + 5] = (byte) 0xfe;
                if (outputStream != null) {
                    try {
                        outputStream.write(buf, 0, buf[2] + 6);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
            case R.id.button2: {
                String address = myClass.getMac();
                String no = nameNumber.getText().toString();
                no = no.substring(0, no.length() - 1);
                StringBuilder res = new StringBuilder();
                res.append("{\"address\":\"").append(address).append("\",\"no\":\"").append(no).append("\"}").append("");
                editJson.setText(res.toString());
                cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                mClipData = ClipData.newPlainText("ce", res.toString());
                cm.setPrimaryClip(mClipData);
                Toast.makeText(MainActivity2.this, "复制成功", Toast.LENGTH_SHORT).show();//悬浮提示
            }
            break;
            default:
                break;
        }

    }

    @SuppressLint("SetTextI18n")
    private void blueToothData(MyDataClss myDataClss) {
        MyDataClss blueData = myDataClss;
        switch (blueData.getU8Function()) {
            case 1:
                break;
            case 2: {
                int battaryValue = blueData.getU8Buf()[0];
                if (battaryValue >= 0) {
                    String value = String.valueOf(battaryValue * 20);
                    if (battaryValue < 6) {
                        textView.setText(value + "");
                    } else if (battaryValue == 6) {
                        textView.setText("充电中");
                    }
                }
                break;
            }
            case 5: {
                int AngleValue = blueData.getU8Buf()[2];
                int value = blueData.getU8Buf()[3];
                textAngle.setText(AngleValue + "");
                textValue.setText(value + "");
                break;
            }
            case 6: {
                switch ((blueData.getU8Buf()[0])) {
                    case 0:
                        if (blueData.getU8Buf()[1] == 1) {
                            Toast.makeText(MainActivity2.this, "回弹设置成功", Toast.LENGTH_SHORT).show();//悬浮提示
                        } else {
                            Toast.makeText(MainActivity2.this, "回弹设置失败", Toast.LENGTH_SHORT).show();//悬浮提示
                        }
                        break;
                    case 1:
                        if (blueData.getU8Buf()[1] == 1) {
                            Toast.makeText(MainActivity2.this, "编号设置成功", Toast.LENGTH_SHORT).show();//悬浮提示
                        } else {
                            Toast.makeText(MainActivity2.this, "编号设置失败", Toast.LENGTH_SHORT).show();//悬浮提示
                        }
                        break;
                    case 3:
                        if (blueData.getU8Buf()[1] == 1) {
                            Toast.makeText(MainActivity2.this, "硬件版本设置成功", Toast.LENGTH_SHORT).show();//悬浮提示
                        } else {
                            Toast.makeText(MainActivity2.this, "硬件版本设置失败", Toast.LENGTH_SHORT).show();//悬浮提示
                        }
                        break;
                    default:
                        Toast.makeText(MainActivity2.this, "接收无效", Toast.LENGTH_SHORT).show();//悬浮提示
                        break;
                }
                break;
            }
            case 7: {
                switch (blueData.getU8Buf()[0]) {
//                    case 0:
//                        break;
                    case 1: {
                        byte[] buf = new byte[blueData.getU8Len() - 1];
                        for (int i = 0; i < blueData.getU8Len() - 1; i++) {
                            buf[i] = (byte) blueData.getU8Buf()[i + 1];
                        }
                        String str = new String(buf);
                        nameNumber.setText(str);
                        editBluetoothName.setText(str);
                        break;
                    }
                    case 3: {
                        int[] buf = new int[blueData.getU8Len() - 1];
                        String str = "";
                        for (int i = 0; i < blueData.getU8Len() - 1; i++) {
                            buf[i] = (int) blueData.getU8Buf()[i + 1];
                        }
//                        str = String.valueOf(buf[0]) + "-" + String.valueOf(buf[1]) + "-" + String.valueOf(buf[2] * 256 + buf[3]);
                        str = String.valueOf(buf[0]);
                        nameVer.setText(str + "");
                        str = String.valueOf(buf[1]);
                        nameVer1.setText(str + "");
                        str = String.valueOf(buf[2] + buf[3] * 256);
                        nameVer2.setText(str + "");
                        break;
                    }
                    default:
                        break;
                }
                break;
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socket != null) {
            if (socket.isConnected()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void finish() {
        super.finish();
        writeString();
        if (socket != null) {
            if (socket.isConnected()) {
                try {
                    outputStream.close();
                    outputStream = null;
                    socket.getOutputStream().close();
                    socket.getInputStream().close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        handler.removeCallbacksAndMessages(null);
        handler = null;
        myClass.setHandler(handler);
        myClass = null;
    }

}