package com.example.figleaf;

import android.content.BroadcastReceiver;
import android.content.*;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Figleaf Group.
 * һ��BroadcastReceiver,����ϵͳһ��������Ϣ,֮��������,����Ӧ�õ�����.
 * ������������SharedPreferences�����(�����洢�����һЩ�趨),�Ӹ����������һ��
 * "С�����ݿ�(Setting.xml)"�ж�ȡ"lock_screen_on_off"���趨,�ݴ��������ʵ�����
 * ѡ��.
 */
public class BootCompletedRecevier extends BroadcastReceiver {


    //��дonReceive
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {


            //����Ӧ�ù����Ƿ����������趨,���Ӧ���л�δ�趨,Ԥ��ֵΪ������(false)
            SharedPreferences setting = context.getSharedPreferences("Setting", Context.MODE_PRIVATE);
            boolean Lock_On_or_Off = setting.getBoolean("lock_screen_on_off", false);

            //�ж��ڿ���ʱ�Ƿ���Ҫ����Ӧ�õ������趨
            //�����޸���ԭ������ֱ������Ӧ�õ�bug����Ϊ������Ҫ��ֻ������Ӧ�õ�һ���ֹ���--����������������Ӧ�á�
            // ����Ͳ��������û�����Ϣ����page��
            if (Lock_On_or_Off) {

                //����MyLock���Activity��MyLockScreenService���Service
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
