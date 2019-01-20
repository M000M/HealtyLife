package cn.edu.pku.tc.countingsteps;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements Handler.Callback{

    //循环取当前时刻的步数的中间的时间间隔
    private long TIME_INTERVAL = 500;

    private TextView text_step; //显示走的步数
    private TextView today_date; //显示日期

    private Handler delayHandler;

    private Messenger messenger;  //向服务端发消息
    private Messenger mGetReplayMessager = new Messenger(new Handler(this)); //服务端处理后放回数据的信使

    //以bind形式开启service,故有ServiceConnection接受回调
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try{
                //当建立好连接之后，就向服务端发出一条消息
                messenger = new Messenger(service);
                Message msg = Message.obtain(null, Constant.MSG_FROM_CLIENT);
                msg.replyTo = mGetReplayMessager; //消息中告诉服务端，返回数据时将数据交给那个信使
                messenger.send(msg);
            }catch(RemoteException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    //接受从服务端回调的步数
    @Override
    public boolean handleMessage(Message msg){
        switch(msg.what){
            case Constant.MSG_FROM_SERVER:
                //更新步数
                text_step.setText(msg.getData().getInt("step") + "");
                Log.d("MainActivity", "handleMessage is executed");
                //每接受到一条服务端返回的信息，就间隔TIME_INTERVAL就向服务端发送消息，然后服务端就会将当前的步数返回
                delayHandler.sendEmptyMessageDelayed(Constant.REQUEST_SERVER, TIME_INTERVAL);
                break;
            case Constant.REQUEST_SERVER:
                try{
                    Message msg1 = Message.obtain(null, Constant.MSG_FROM_CLIENT);
                    msg1.replyTo = mGetReplayMessager;
                    messenger.send(msg1);
                }catch(RemoteException e){
                    e.printStackTrace();
                }
                break;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text_step = (TextView)findViewById(R.id.main_text_step);
        getDate();
        delayHandler = new Handler(this);
        Log.d("MainActivity", "onCreate is executed");


    }

    private void getDate(){
        today_date = (TextView)findViewById(R.id.date);
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String str = (String)sdf.format(date);
        Log.d("MainActivity", str);
        today_date.setText(str);
    }

    @Override
    public void onStart(){
        super.onStart();
        setupService(); //启动服务
        Log.d("MainActivity", "onStart is executed");
    }

    /**
     * 开启服务
     */
    private void setupService(){
        Intent intent = new Intent(this, StepService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE); //绑定服务
        startService(intent); //开启服务
    }

    @Override
    public void onBackPressed(){  //按返回键
        moveTaskToBack(true);  //将活动转入后台运行
        super.onBackPressed();
    }

    @Override
    protected void onDestroy(){
        //取消绑定服务
        unbindService(conn);
        super.onDestroy();
    }
}

























