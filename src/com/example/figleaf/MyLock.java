package com.example.figleaf;

import static com.example.figleaf.myOpenHelper.*;

import java.io.File;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MyLock extends Activity {

    Button buttonconfirm;
    EditText pwd_in_text;
    int change;              //记录上次改变的位置
    private myOpenHelper mHelper;
    private int[] idArray;
    private String[] PWArray;
    private String[] picPathArray;
    private String[] dirtyArray;
    private String[] nameArray;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lockscreen);
        buttonconfirm = (Button) this.findViewById(R.id.comfirm);
        pwd_in_text = (EditText) this.findViewById(R.id.pwd);
        buttonconfirm.setOnClickListener(listener);
        mHelper = new myOpenHelper(this, DB_NAME, null, 1);
        change = 0;


        EnableSystemKeyguard(false);

        //KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
        //KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        //lock.disableKeyguard();
    }



    Button.OnClickListener listener = new Button.OnClickListener() {

        public void onClick(View v) {
            int index = -1; //记录命中位置
            if (v == buttonconfirm) {
                String pwd = pwd_in_text.getText().toString().trim();
                int count = getInfo();  //获取数据库里一种多少数据
                for (int i = 0; i < count; i++) {
                 //查找数据是否有这个密码

                    if (pwd.equals(PWArray[i])) {

                    //如果命中
                        index = i;
                        break;
                    }
                }
                if (index == -1) {
                    Toast.makeText(MyLock.this, "Wrong Password", Toast.LENGTH_LONG).show();
                } else {
                    if (index != change) {             //如果换密码了

                        //KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
                        //KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
                        //lock.disableKeyguard();
                        Log.i("recover", String.valueOf(change));
                        if (change != 0) {
                            recover(); //恢复被隐藏
                        }
                        dirtyArray[change] = "0";
                         //置零

                        update(idArray[change], change);
                        //更新数据


                        if (index != 0) {
                            hidefile(index);
                        }
                         //隐藏文件

                        dirtyArray[index] = "1";
                        update(idArray[index], index);
                        //更新数据库信息

                    }

                    EnableSystemKeyguard(false);

                    //something interesting here...
                    finish();
                    //System.exit(0);
                }

                //EnableSystemKeyguard(false);
            }

        }
    };

    public int getInfo() {
        SQLiteDatabase db = mHelper.getWritableDatabase();
         //获取数据库连接

        Cursor c = db.query(TABLE_NAME, new String[]{ID, NAME, PW, PICPATH, DIRTY}, null, null, null, null, ID);
        int idIndex = c.getColumnIndex(ID);
        int nameIndex = c.getColumnIndex(NAME);
        int pwIndex = c.getColumnIndex(PW);     //获得姓名列的列号
        int picIndex = c.getColumnIndex(PICPATH);
        int dirIndex = c.getColumnIndex(DIRTY);
        idArray = new int[c.getCount()];            //创建存放id的int数组对象
        PWArray = new String[c.getCount()];
        picPathArray = new String[c.getCount()];            //创建存放图片地址的String数组对象
        nameArray = new String[c.getCount()];
        dirtyArray = new String[c.getCount()];
        int i = 0;
        for (c.moveToFirst(); !(c.isAfterLast()); c.moveToNext()) {
            idArray[i] = c.getInt(idIndex);
            nameArray[i] = c.getString(nameIndex);
            PWArray[i] = c.getString(pwIndex);          //将pW添加到String数组中
            picPathArray[i] = c.getString(picIndex);            //将路径添加到String数组中
            dirtyArray[i] = c.getString(dirIndex);
            Log.i(nameArray[i], dirtyArray[i]);
            if (dirtyArray[i].equals("1")) {
                change = i;
                Log.i("getInfo", String.valueOf(change));
            }
            //记录上次使用的用户位置
            i++;
        }
        c.close();
        //关闭Cursor对象

        db.close();
         //关闭SQLiteDatabase对象

        return i;

    }

    protected void recover() {

        if (!picPathArray[change].equals("0") && !picPathArray[change].equals(null)) {

        //防止没有内容

            Log.i("recover", picPathArray[change]);
            String path = picPathArray[change];
            File file = new File(path);
            String newpath = file.getParentFile().getParent() + "/" + file.getName();  //vertivy to correct path
            picPathArray[change] = newpath;   //recover the correct path
            Log.i("recover", newpath);
            File newfile = new File(newpath);
            file.renameTo(newfile);
            file.delete();
            Uri localUri = Uri.fromFile(newfile);

            //广播消息，刷新相册

            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
            sendBroadcast(localIntent);

        }


    }

    protected void hidefile(int i) {
        if (!picPathArray[i].equals("0") && !picPathArray[i].equals(null)) {
            File file = new File(picPathArray[i]);                //根据路径，获得文件
            String fpath = file.getParentFile().getPath();
            File newfile = new File(fpath + "/.hide/" + file.getName());   //创立隐藏文件夹
            file.renameTo(newfile);
            String s = newfile.getPath();
            picPathArray[i] = s;                   //更新文件路径
            Log.i("hideFile", s);
            file.delete();
            Uri localUri = Uri.fromFile(file);//广播消息，刷新相册
            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
            sendBroadcast(localIntent);

        }


    }

    protected void update(int id, int i) {

        //获得数据库对象
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, nameArray[i]);
        values.put(PW, PWArray[i]);
        values.put(PICPATH, picPathArray[i]);
        values.put(DIRTY, dirtyArray[i]);

        //更新数据
        db.update(TABLE_NAME, values, ID + "=?", new String[]{id + ""});
        db.close();


    }

    //屏蔽系统锁屏
    private void EnableSystemKeyguard(boolean bEnable) {
        KeyguardManager mKeyguardManager = null;
        KeyguardManager.KeyguardLock mKeyguardLock = null;

        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        mKeyguardLock = mKeyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        if (bEnable)
            //不屏蔽
            mKeyguardLock.reenableKeyguard();
        else
            //屏蔽
            mKeyguardLock.disableKeyguard();
    }


    //@Override
    //public boolean dispatchKeyEvent(KeyEvent event) {
    // TODO Auto-generated method stub
    //return super.dispatchKeyEvent(event);
    //  return true;
    //}

    //屏蔽home_key和back_key

    //这里出现一个小问题，就是我们写好了屏蔽home和back键的函数 ，理论上没有问题，可是在虚拟机上运行的时候只有back
    //会被屏蔽，而home键还能使用 ，但是我们在真机是测试的时候是能够全部屏蔽的。我们目前也还没有找到原因。

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //back_key
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // System.exit(0);
            // return true;

            //这里好像响应的不是很彻底，本身是在考虑应该直接结束掉，也就是exit（0）的

            Toast.makeText(this, "You pressed the back button.", Toast.LENGTH_LONG).show();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_HOME) { //home_key

            // System.exit(0);
            // return true;

            Toast.makeText(this, "You pressed the back button.", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    //这里的单独注释的代码部分就是我们用来单独测试虚拟机上屏蔽home和back键用的代码，但是home部分没有效果，所以我们在思考是不是
    //在虚拟机上home键的屏蔽需要其他的权限声明或者虚拟机本身就不能被屏蔽home键

    //HOME
    //@Override
   // public boolean onKeyDown(int keyCode, KeyEvent event)
   // {
    //    // 按下键盘上返回按钮
    //    if (keyCode == KeyEvent.KEYCODE_HOME)
    //    {
    //        System.exit(0);
    //        return true;
    //    }
    //    else
   //     {
    //        return super.onKeyDown(keyCode, event);
     //   }
   // }

    //声明获得屏蔽的权限
   // public void onAttachedToWindow()
   // {
     //   this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
   //     super.onAttachedToWindow();
   // }

    //BACK
    //@Override
    // public boolean onKeyDown(int keyCode, KeyEvent event)
    // {
    //    // 按下键盘上返回按钮
    //    if (keyCode == KeyEvent.KEYCODE_BACK)
    //    {
    //        System.exit(0);
    //        return true;
    //    }
    //    else
    //     {
    //        return super.onKeyDown(keyCode, event);
    //   }
    // }

    //声明获得屏蔽的权限
    // public void onAttachedToWindow()
    // {
    //   this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
    //     super.onAttachedToWindow();
    // }


    @Override
    public void onAttachedToWindow() {
        // TODO Auto-generated method stub

        //权限声明
        //this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        //super.onAttachedToWindow();

        // try to disable home key.
        //this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
        //super.onAttachedToWindow();
    }


}
