package com.tools;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by win10 on 2017/5/4.
 */


public class ClassMeasFunction extends Service {

    private BluetoothSocket socket;
    private StringBuilder survingString;//存储消息
    private InputStream inputStream;//蓝牙输入流
    private OutputStream outputStream;//得到输出流
    public listenThread liThread=null;
    private boolean flag;//线程是否停止的标签
    private final IBinder binder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    public class LocalBinder extends Binder
    {
        public ClassMeasFunction getService()
        {
            return ClassMeasFunction.this;
        }
    }

    //初始化
    public void init(BluetoothSocket socket)
    {
        survingString=new StringBuilder("");
        this.socket=socket;
        flag=true;
    }
    //开始监听
    public void beginComutting()
    {
        setFlag(false);//关闭旧的线程
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        liThread = new listenThread();
        flag=true;
        liThread.start();
    }

    //关闭
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void closeSocket()
    {
        setFlag(false);//关闭旧的线程
        if(socket!=null&&socket.isConnected())
        {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socket=null;
    }

    //停止服务
    @Override
    public void onDestroy() {
        super.onDestroy();
        closeSocket();
    }

    public void setFlag(boolean flag) //关闭监听线程
    {
        this.flag = flag;
    }

    //获得蓝牙连接
    public BluetoothSocket getSocket() {
        return socket;
    }

    //重启线程
    public void reBegain()
    {
        setFlag(true);
        liThread=new listenThread();
        liThread.start();
    }

    public boolean getFlag() {
        return flag;
    }


    //监听全站仪消息的线程
    public final class listenThread extends Thread
    {
        private Handler handler=new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if(msg.obj.toString().startsWith("%"))
                {
                    survingString=new StringBuilder(msg.obj.toString());
                }
                else
                {
                    survingString.append(msg.obj.toString());
                }
            }

        };
        public listenThread()
        {
            try
            {
                inputStream= socket.getInputStream();
                outputStream = socket.getOutputStream();
            }
            catch (IOException e1)
            {
            }
        }
        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        @Override
        public void run()
        {
            if(inputStream!=null)
            {
                try
                {
                    while(flag)
                    {
                        //判断蓝牙是否断开
                        if(!socket.isConnected())
                        {
                            socket.connect();
                            inputStream= socket.getInputStream();
                            outputStream = socket.getOutputStream();
                        }
                        byte[] buffer=new byte[1024];
                        int count=inputStream.read(buffer);
                        Message msgMessage=new Message();
                        msgMessage.obj=new String(buffer,0,count,"utf-8");
                        handler.sendMessage(msgMessage);
                    }
                }
                catch (IOException e)
                {

                }
            }
        }


    }

    //向全站仪发送命令并接受返回值
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private String[] sendCommand(BluetoothSocket socket, String command, int outLength, long errorTime) throws IOException
    {
        //判断蓝牙是否断开
        if(!socket.isConnected())
        {
            socket.connect();
            inputStream= socket.getInputStream();
            outputStream = socket.getOutputStream();
        }

        command="\n"+command;
        survingString.setLength(0);//清除消息集合
        String[]result=new String[outLength];//存储结果
        outputStream.write(command.getBytes("utf-8"));
        long timeBegin= Calendar.getInstance().getTimeInMillis();//得到程序运行的初始时间
        long timeNow=Calendar.getInstance().getTimeInMillis();//得到程序运行的过程的时间
        while(!survingString.toString().endsWith("\r\n"))
        {
            if(timeNow-timeBegin<=errorTime)
            {
                timeNow=Calendar.getInstance().getTimeInMillis();//得到程序运行的过程的时间
            }
            else
            {
                return new String[]{"12312","1536"};
            }
        }
        String getString=survingString.substring(0,survingString.length()-2).toString();//得到的消息,去掉末尾的换行
        String out=getString.substring(getString.indexOf(":")+1).trim();//获得观测结果
        if(outLength!=1)//返回结果不是一个值
        {
            result=out.split(",");
        }
        else//返回结果是一个值
        {
            result=new String[]{out};
        }
        return result;
//		return new String[]{"2323","1656"};
    }

    //得到盘位
    public String[] VB_TMC_GetFace()
    {
        String command="%R1Q,2026:\r\n";//操作命令
        String[]result=new String[]{"12312","1536"};
        try
        {
            result=sendCommand(socket, command,2,15000);
        }
        catch (IOException e)
        {

        }//发送命令
        if(!result[0].equals("0"))//观测失败
        {
            result=new String[]{"12312","1536"};
        }
        return result;
    }

    //得到盘位
    public String[] TMC_GetAngle()
    {
        String command="%R1Q,2107:1\r\n";//操作命令
        String[]result=new String[]{"12312","1536","123"};
        try
        {
            result=sendCommand(socket, command,2,15000);
        }
        catch (IOException e)
        {

        }//发送命令
        if(!result[0].equals("0"))//观测失败
        {
            result=new String[]{"12312","1536","1516"};
        }
        return result;
    }

    public String[] VB_AUT_ChangeFace4()
    {
        String command="%R1Q,9028:0,0,0\r\n";//操作命令
        String[]result=new String[]{"12312"};
        try
        {
            result=sendCommand(socket, command,1,15000);
        }
        catch (IOException e)
        {

        }//发送命令
        if(!result[0].equals("0"))//观测失败
        {
            result=new String[]{"12312"};
        }
        return result;
    }

    public String[] VB_BAP_MeasDistAng() {
        String command="%R1Q,17017:2\r\n";//操作命令
        String[]result=new String[]{"12312","14","414","4141","475"};
        try
        {
            result=sendCommand(socket, command,5,15000);
        }
        catch (IOException e)
        {

        }//发送命令
        if(!result[0].equals("0"))//观测失败
        {
            result=new String[]{result[0],"1536","414","4141","475"};
        }
        return result;
    }

    public String[] VB_TMC_DoMeasure(String com, String mode) {
        String command="%R1Q,2008:"+com+","+mode+"\r\n";//操作命令
        String[]result=new String[]{"12312"};
        try
        {
            result=sendCommand(socket, command,1,15000);
        }
        catch (IOException e)
        {

        }//发送命令
        if(!result[0].equals("0"))//观测失败
        {
            result=new String[]{"12312"};
        }
        return result;
    }

    public String[] VB_TMC_SetOrientation(double dblHz_Radian) {
        String command="%R1Q,2113:"+String.valueOf(dblHz_Radian)+"\r\n";//操作命令
        String[]result=new String[]{"12312"};
        try
        {
            result=sendCommand(socket, command,1,15000);
        }
        catch (IOException e)
        {

        }//发送命令
        if(!result[0].equals("0"))//观测失败
        {
            result=new String[]{"12312"};
        }
        return result;
    }

    //设置ATR状态
    public String[] setATR(String state)
    {
        String command="%R1Q,18005:"+state+"\r\n";//操作命令
        String[]result=new String[]{"12312"};
        try
        {
            result=sendCommand(socket, command,1,15000);
        }
        catch (IOException e)
        {

        }//发送命令
        if(!result[0].equals("0"))//设置失败
        {
            result=new String[]{"12312"};
        }
        return result;

    }

    //得到ATR状态
    public String[]  getATRstate()
    {
        String command="%R1Q,18006:\r\n";//操作命令
        String[]result=new String[]{"12312","1656"};
        try
        {
            result=sendCommand(socket, command,2,15000);
        }
        catch (IOException e)
        {

        }//发送命令
        if(!result[0].equals("0"))//获取失败
        {
            result=new String[]{"12312","1656"};
        }
        return result;
    }

    public String[] VB_AUT_MakePositioning4(String hza, String vca, String posMode, String atrMode,
                                            boolean b) {
        String command="%R1Q,9027:"+hza+","+vca+","+posMode+","+atrMode+","+"0\r\n";//操作命令
        String[]result=new String[]{"12312"};
        try
        {
            result=sendCommand(socket, command,1,15000);
        }
        catch (IOException e)
        {

        }//发送命令
        if(!result[0].equals("0"))//观测失败
        {
            result=new String[]{"12312"};
        }
        return result;
    }

    //获得设备号
    public String[] getInstrumentNo()
    {
        String command="%R1Q,5003:\r\n";//操作命令
        String[]result=new String[]{"12312","161616"};
        try
        {
            result=sendCommand(socket, command,2,15000);
        }
        catch (IOException e)
        {

        }//发送命令
        if(!result[0].equals("0"))//观测失败
        {
            result=new String[]{"12312","161616"};
        }
        return result;
    }

    //获得设备名称
    public String[] getInstrumentName()
    {
        String command="%R1Q,5004:\r\n";//操作命令
        String[]result=new String[]{"12312","161616"};
        try
        {
            result=sendCommand(socket, command,2,15000);
        }
        catch (IOException e)
        {

        }//发送命令
        if(!result[0].equals("0"))//观测失败
        {
            result=new String[]{"12312","161616"};
        }
        return result;
    }

    //设置棱镜模式
    public String[] SetPrimeType(String PrimeType)
    {
        String command="%R1Q,17008:"+PrimeType+"\r\n";//操作命令
        String[]result=new String[]{"12312"};
        try
        {
            result=sendCommand(socket, command,1,15000);
        }
        catch (IOException e)
        {

        }//发送命令
        if(!result[0].equals("0"))//观测失败
        {
            result=new String[]{"12312","161616"};
        }
        return result;
    }

    //获得棱镜模式
    public String[] getPrismStyle()
    {
        String command="%R1Q,17009:\r\n";//操作命令
        String[]result=new String[]{"12312","1656"};
        try
        {
            result=sendCommand(socket, command,2,15000);
        }
        catch (IOException e)
        {

        }//发送命令
        if(!result[0].equals("0"))//获取失败
        {
            result=new String[]{"12312","1656"};
        }
        return result;
    }

    //设置棱镜模式
    public String[] SetReflator(String reflactor)
    {
        String command="%R1Q,17021:"+reflactor+"\r\n";//操作命令
        String[]result=new String[]{"12312"};
        try
        {
            result=sendCommand(socket, command,1,15000);
        }
        catch (IOException e)
        {

        }//发送命令
        if(!result[0].equals("0"))//观测失败
        {
            result=new String[]{"12312","161616"};
        }
        return result;
    }

}
