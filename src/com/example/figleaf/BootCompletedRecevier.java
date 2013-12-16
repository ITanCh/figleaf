package com.example.figleaf;

import android.content.BroadcastReceiver;
import android.content.*;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Figleaf Group.
 * 一个BroadcastReceiver,接受系统一开机的信息,之后根据情况,调用应用的锁屏.
 * 在其中利用了SharedPreferences这个类(用来存储程序的一些设定),从该类所创造的一个
 * "小的数据库(Setting.xml)"中读取"lock_screen_on_off"的设定,据此作出合适的锁屏
 * 选择.
 */
public class BootCompletedRecevier extends BroadcastReceiver {


    //重写onReceive
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {


            //接受应用关于是否开启锁屏的设定,如果应用中还未设定,预设值为不开启(false)
            SharedPreferences setting = context.getSharedPreferences("Setting", Context.MODE_PRIVATE);
            boolean Lock_On_or_Off = setting.getBoolean("lock_screen_on_off", false);

            //判断在开机时是否需要启动应用的锁屏设定
            //这里修复了原来开机直接启动应用的bug，因为我们需要的只是启动应用的一部分功能--锁屏，而不是整个应用。
            // 这里就不包括了用户的信息设置page。
            if (Lock_On_or_Off) {

                //调用MyLock这个Activity及MyLockScreenService这个Service
                Intent i = new Intent(context, MyLock.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                Intent newIntent = new Intent(context, MyLockScreenService.class);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                //KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
                //KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
                //lock.disableKeyguard();

                context.startService(newIntent);
                context.startActivity(i);
            }


        }
    }
}
