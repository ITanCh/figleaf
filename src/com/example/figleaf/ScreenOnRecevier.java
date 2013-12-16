/**
package com.example.figleaf;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
 */

/**
 * Created by lafwind on 7/29/13.
 */

/**
public class ScreenOnRecevier extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {

            SharedPreferences setting = context.getSharedPreferences("Setting", Context.MODE_PRIVATE);
            boolean Lock_On_or_Off = setting.getBoolean("lock_screen_on_off", false);

            if (Lock_On_or_Off) {

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
 */
