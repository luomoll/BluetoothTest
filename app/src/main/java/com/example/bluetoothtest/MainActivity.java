package com.example.bluetoothtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;

import android.bluetooth.BluetoothAdapter;  //蓝牙适配器
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.sql.Struct;
import java.util.ArrayList;

import android.widget.AdapterView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;

import androidx.appcompat.app.AlertDialog;

import java.io.OutputStream;
import java.io.InputStream;
import java.lang.Thread;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.CharBuffer;
import java.util.logging.LogRecord;


public class MainActivity extends AppCompatActivity {
    int[] sendbyte = {0xfa, 0xde, 0x03, 0x05, 0x00, 0x00, 0xfe, 0xfe};
    byte[] send = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    int PageCount = 0;
    mBluetoothReceiver BluetoothReceiver;
    BluetoothDevice mBluetoothDevice;

    Button serch;
    Button secondBtn;
    TextView RebondValue;
    ListView BlueToothListView;
    TextView textView;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket socket;
    //定义一个列表，存蓝牙设备的地址。
    ArrayList<String> arrayList = new ArrayList<>();
    //定义一个列表，存蓝牙设备地址，用于显示。
    ArrayList<String> deviceName = new ArrayList<>();
    ArrayAdapter<String> adapter;
    BluetoothDevice device;
    boolean connecting = true;
    boolean connected = false;
    String AddressStrig;
    boolean BondStartFlag = false;
    Handler handler = null;
    Toast toast;
    Intent intentB;

    public ConnectedThread connectedThread = null;

    public enum javaState {
        PacketHead,
        PacketHead1,
        PacketLength,
        PacketDate,
        PacketSum,
        PacketEnd,
        PacketEnd1;
    }

    MyClass myClass;

    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
            //开启方式一：直接开启，不推荐。但是我测试的小米手机使用该方法仍然会在底部弹出一个确认窗口，需要用户同意，应该是小米对系统进行了定制化
            //boolean enable = mBluetoothAdapter.enable();

            //开启方式二：通过调用系统弹出界面来开启蓝牙，在onActivityResult中返回的resultCode为-1表示开启成功，0表示开启失败
            Intent startBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(startBt, 1);//REQUEST_ENABLE);  //请求开启蓝牙
        }
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH}, 100);
        BluetoothReceiver = new mBluetoothReceiver();
        IntentFilter filter = new IntentFilter();
        //发现设备
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        //设备绑定状态改变
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //蓝牙设备状态改变
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        //搜素完成
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //注册广播
        registerReceiver(BluetoothReceiver, filter);


        serch = findViewById(R.id.serchbluetooth);  //按键按下之后打印一下
        BlueToothListView = findViewById(R.id.BlueToothID); //蓝牙名称的显示列表
//        RebondValue = findViewById(R.id.textViewReBondValue);
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        myClass = (MyClass) getApplication();
        intentB = new Intent(MainActivity.this, MainActivity2.class);

        //搜索蓝牙的监听
        serch.setOnClickListener((View.OnClickListener) view -> {
            //开始搜索
            deviceName.clear();  //清除列表
            arrayList.clear();
            adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceName);
            BlueToothListView.setAdapter(adapter);//显示

            mBluetoothAdapter.startDiscovery();//开始搜索蓝牙
            BondStartFlag = true;
            Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
            if (devices.size() > 0) {
                for (Iterator<BluetoothDevice> it = devices.iterator(); it.hasNext(); ) {
                    BluetoothDevice de = (BluetoothDevice) it.next();
                    deviceName.add("设备名：" + de.getName() + "(已配对)\n" + "设备地址：" + de.getAddress() + "\n");
                    arrayList.add(de.getAddress());//将搜索到的蓝牙地址添加到列表。
                }
            }
        });
//        secondBtn = (Button) findViewById(R.id.second);
//        secondBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                startActivity(intentB);
////                Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_LONG).show();//悬浮提示
//            }
//        });
        //ListView显示监听
        BlueToothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                AddressStrig = arrayList.get(arg2);  //获得当前选中的Item对应list里面的地址
                mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(AddressStrig);
                try {
                    UUID mactekHartModemUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                    socket = mBluetoothDevice.createRfcommSocketToServiceRecord(mactekHartModemUuid);  //UUID使用方法
//                    Method m;
//                    m=mBluetoothDevice.getClass().getMethod("createRfcommSocketToServiceRecord", UUID.class);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                }
                connectDevice();

            }
        });
        BlueToothListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AddressStrig = arrayList.get(position);  //获得当前选中的Item对应list里面的地址
                mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(AddressStrig);
                if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    try {
                        Method removeBondMethod = BluetoothDevice.class.getMethod("removeBond");
                        removeBondMethod.invoke(mBluetoothDevice);
                        showExitDialog01("取消配对");
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        return false;
                    }

                }
                return true;
            }
        });
    }


    protected void connectDevice() {
        try {
            // 连接建立之前的先配对
            if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                Method creMethod = BluetoothDevice.class.getMethod("createBond");
                creMethod.invoke(mBluetoothDevice);
            } else if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) //已连接的取消连接
            {
//                Method removeBondMethod = BluetoothDevice.class.getMethod("removeBond");
//                removeBondMethod.invoke(mBluetoothDevice);
//                showExitDialog01("取消配对");
                try {
                    mBluetoothAdapter.cancelDiscovery();
                    socket.connect();  //Socket连接
                    Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();//悬浮提示
//                    showExitDialog01("蓝牙连接成功");
                    myClass.setMac(AddressStrig);
                    myClass.setSocket(socket);
                    connected = true;
                    startActivity(intentB);
                    connectedThread = new ConnectedThread(socket); //开启线程
                    connectedThread.start();
                } catch (IOException e) {
                    // TODO: handle exception
                    Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_SHORT).show();//悬浮提示
                    connected = false;
                    try {
                        socket.close();
                        socket = null;
                    } catch (IOException e2) {
                        // TODO: handle exception
                    }
                } finally {
                    connecting = false;
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            //DisplayMessage("无法配对！");
//            Log.e("TAG", "无法配对");
            Toast.makeText(MainActivity.this, "配对失败", Toast.LENGTH_SHORT).show();//悬浮提示
            e.printStackTrace();
        }
    }

    private void showExitDialog01(String message) {
        new AlertDialog.Builder(this)
                .setTitle("提示：")
                .setMessage(message)
                .setPositiveButton("确定", null)
                .show();
    }

    //蓝牙广播监听响应
    public class mBluetoothReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // if (action == null) return;
            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            switch (action) {
                case BluetoothDevice.ACTION_FOUND:
                    //显示在ListView列表里面

                    for (Object list : arrayList) {
                        String s = (String) list;
                        if (s.equals(device.getAddress())) {
                            return;
                        }
                    }
                    deviceName.add("设备名：" + device.getName() + "\n" + "设备地址：" + device.getAddress() + "\n");//将搜索到的蓝牙名称和地址添加到列表。
                    arrayList.add(device.getAddress());//将搜索到的蓝牙地址添加到列表。
                    adapter.notifyDataSetChanged();//更新界面显示
//                    //获取搜索到设备的信息
//                    Log.i(TAG, "device name: " + device.getName() + " address: " + device.getAddress());
//                    //获取绑定状态
//                    Log.i(TAG, "device bond state : " + device.getBondState());
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    //如果蓝牙连接成功之后提示对话框出现
                    if (device.getBondState() == BluetoothDevice.BOND_BONDED) {

//                    //取消搜索设备

                        try {
                            socket.connect();  //Socket连接
                            Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();//悬浮提示
//                            showExitDialog01("蓝牙连接成功");
                            connected = true;
                            myClass.setMac(AddressStrig);
                            myClass.setSocket(socket);
//                            intentB = new Intent(MainActivity.this, MainActivity2.class);
//                intent.setAction("MainActivity2");
                            startActivity(intentB);
//                            if ((connectedThread != null) || (connectedThread.isRunning())) {
//                                connectedThread.cancel();
//                            }
                            connectedThread = new ConnectedThread(socket); //开启线程
                            connectedThread.start();
                            mBluetoothAdapter.cancelDiscovery();  //断开搜索

                        } catch (IOException e) {
                            connected = false;
                            Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_SHORT).show();//悬浮提示
                            try {
                                socket.close();
                                socket = null;
                            } catch (IOException e2) {
                                // TODO: handle exception
                            }
                        } finally {
                            connecting = false;
                        }
                    }
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    if (BondStartFlag == true) {
//                        showExitDialog01("搜索完成");
//                        toast.setText("搜索完成");
                        Toast.makeText(MainActivity.this, "搜索完成", Toast.LENGTH_SHORT).show();//悬浮提示
                        BondStartFlag = false;
                    }
                    break;
            }
        }
    }

    public static String bytesToHexString(byte[] bytes) {
        String result = "";
        for (int i = 0; i < bytes.length; i++) {
            String hexString = Integer.toHexString(bytes[i] & 0xFF);
            if (hexString.length() == 1) {
                hexString = '0' + hexString;
            }
            result += hexString.toUpperCase();
        }
        return result;
    }

    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private boolean running;

        public ConnectedThread(BluetoothSocket socket) {

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            running = true;

            // 获取BluetoothSocket的input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e("zn", "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            //mState = STATE_CONNECTED;
        }

        public void run() {
            Log.i("zn", "BEGIN mConnectedThread");
            StringBuffer strBuf = new StringBuffer(); // 下划线命名法
            char[] array = new char[100];
            int Date = 0;
            char dataCount = 0;
            String destination;
            javaState State = javaState.PacketHead;
            Log.i("zn", "线程开始");
//            ArrayList<Byte> reData=new ArrayList<>(1024);
            ByteBuffer reData = ByteBuffer.allocate(1024);

            while (true) {
                if (!running) {
                    break;
                }
                Log.i("zn", "循环开始");
                try {
                    Log.d("zn", "准备接收到数据");
                    byte[] buffer = new byte[1024];
                    byte[] reBlueData = new byte[100];

                    int length = mmInStream.read(buffer);             //读到的数据，需要显示在界面
                    if (length > 0) {
//                        reData.compact();
                        reData.put(buffer, 0, length);
                        reData.flip();
                    }
                    while (reData.array().length > 0) {
                        if (!reData.hasRemaining()) {
                            reData.compact();
                            break;
                        }
                        int data = reData.get() & 0x00ff;

//                        data = reData.array()[0] & 0x00ff;
                        switch (State) {
                            case PacketHead:
                                if (data == 0xfa) {
                                    dataCount = 0;
                                    State = javaState.PacketHead1; //到下一个状态，等待包头
                                }
                                break;
                            case PacketHead1:
                                if (data == 0xde) {
                                    State = javaState.PacketLength; //等待数据长度，
                                } else {
                                    State = javaState.PacketHead;   //继续等待包头
                                }
                                break;
                            case PacketLength:
                                if ((data < 0x20) && (data > 0))  //等待到命令
                                {
                                    array[dataCount] = (char) data;
                                    dataCount++;
                                    State = javaState.PacketDate;   //继续获取数据
                                } else {
                                    State = javaState.PacketHead;
                                    dataCount = 0;
                                }
                                break;
                            case PacketDate:
                                array[dataCount] = (char) data;

                                if (dataCount == array[0]) {
                                    State = javaState.PacketSum;   //继续等待校验和
                                }
                                dataCount++;
                                break;
                            case PacketSum:

                                char sum = 0;
                                for (int i = 1; i < dataCount; i++) {
                                    sum += array[i];
                                }
                                sum = (char) (sum % 256);
                                if (data == sum) {
                                    State = javaState.PacketEnd;   //继续等待结束
                                } else {
                                    State = javaState.PacketHead;   //重新接收数据
                                }
                                break;
                            case PacketEnd:
                                if (data == 0xfe) {
                                    State = javaState.PacketEnd1;   //等待下一帧结束帧
                                } else {
                                    State = javaState.PacketHead;   //重新接收数据
                                }
                                break;
                            case PacketEnd1:
                                State = javaState.PacketHead;
                                if (data != 0xfe) {
                                    dataCount = 0;
                                    break;
                                }
                                MyDataClss myDataClss = new MyDataClss(array, dataCount);
                                if (handler == null) {
                                    handler = myClass.getHandler();
                                    if(handler == null) {
                                        break;
                                    }
                                }
                                String temp = new String(array);
                                Message msg = new Message();
                                msg.what = 0;
                                msg.obj = (Object) myDataClss;
                                handler.sendMessage(msg);
//                                }

                                break;
                        }
                        //  }
                    }

                } catch (IOException e) {
                    Log.e("zn", "disconnected", e);
                    handler = null;

                    break;
                }
            }
            try {
                mmOutStream.close();
                mmInStream.close();
                mmSocket.close();
                handler = null;
                socket.close();
                socket.getInputStream().close();
                socket.getOutputStream().close();

                socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean isRunning() {
            return running;
        }

        //写数据
        public void write(byte[] buffer) {
            try {
                Log.e("zn", "send");
                mmOutStream.write(buffer);
                //发送消息到主线程
//                mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer)
//                        .sendToTarget();
            } catch (IOException e) {
                Log.e("zn", "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                running = false;
                mmSocket.close();
            } catch (IOException e) {
                Log.e("zn", "close() of connect socket failed", e);
            }
        }
    }

    @Override
    protected void onDestroy() {
//        try {
//            socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        super.onDestroy();

    }
}
