package com.example.figleaf;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.*;
import android.os.IBinder;
import android.util.Log;

public class MyLockScreenService extends Service {
    private final String ACT_SCREEN_OFF = "android.intent.action.SCREEN_OFF";
    //private final String ACT_SCREEN_ON = "android.intent.action.SCREEN_ON";

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        Log.e("", "***********onBind MyLockScreenService");
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        // register Broadcast
        Log.e("", "***********onCreate registerReceiver");
        IntentFilter intentFilter = new IntentFilter(ACT_SCREEN_OFF);
        registerReceiver(mScreenBCR, intentFilter);

        //KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
        //KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        //lock.disableKeyguard();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        unregisterReceiver(mScreenBCR);
    }

    /**一个BroadcastReceiver ,接受屏幕是否变暗,变亮,从而判断需不需根据应用设置
     * 即接受人工变暗锁屏以及当待机一定时长后的系统自动锁屏的信息来响应，从而调出我们的锁屏界面，并且同时屏蔽系统锁屏
     * 屏蔽系统锁屏的函数Enablesystemkeyguard在mainactivity里面
     * 调用锁屏Activity.
     */
    private BroadcastReceiver mScreenBCR = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            Log.e("", "***********onReceive Intent=" + intent);

            {
                try {
                    //SharedPreferences setting = getSharedPreferences("Setting", MODE_PRIVATE);
                    //boolean Lock_On_or_Off = setting.getBoolean("lock_screen_on_off", false);
                    //if (Lock_On_or_Off) {
                    // Intent i = new Intent();
                    // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    // i.setClass(context, MyLock.class);
                    // context.startActivity(i);
                    //}



                    //调用MyLock这个Activity

                    Intent i = new Intent();
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setClass(context, MyLock.class);
                    context.startActivity(i);

                } catch (Exception e) {
                    // TODO: handle exception
                    Log.e("", "***********onReceive Error=" + e);
                }
            }
        }
    };

}
