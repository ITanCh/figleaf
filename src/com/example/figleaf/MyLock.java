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
    int change;              //��¼�ϴθı��λ��
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
            int index = -1; //��¼����λ��
            if (v == buttonconfirm) {
                String pwd = pwd_in_text.getText().toString().trim();
                int count = getInfo();  //��ȡ���ݿ���һ�ֶ�������
                for (int i = 0; i < count; i++) {
                 //���������Ƿ����������

                    if (pwd.equals(PWArray[i])) {

                    //�������
                        index = i;
                        break;
                    }
                }
                if (index == -1) {
                    Toast.makeText(MyLock.this, "Wrong Password", Toast.LENGTH_LONG).show();
                } else {
                    if (index != change) {             //�����������

                        //KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
                        //KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
                        //lock.disableKeyguard();
                        Log.i("recover", String.valueOf(change));
                        if (change != 0) {
                            recover(); //�ָ�������
                        }
                        dirtyArray[change] = "0";
                         //����

                        update(idArray[change], change);
                        //��������


                        if (index != 0) {
                            hidefile(index);
                        }
                         //�����ļ�

                        dirtyArray[index] = "1";
                        update(idArray[index], index);
                        //�������ݿ���Ϣ

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
         //��ȡ���ݿ�����

        Cursor c = db.query(TABLE_NAME, new String[]{ID, NAME, PW, PICPATH, DIRTY}, null, null, null, null, ID);
        int idIndex = c.getColumnIndex(ID);
        int nameIndex = c.getColumnIndex(NAME);
        int pwIndex = c.getColumnIndex(PW);     //��������е��к�
        int picIndex = c.getColumnIndex(PICPATH);
        int dirIndex = c.getColumnIndex(DIRTY);
        idArray = new int[c.getCount()];            //�������id��int�������
        PWArray = new String[c.getCount()];
        picPathArray = new String[c.getCount()];            //�������ͼƬ��ַ��String�������
        nameArray = new String[c.getCount()];
        dirtyArray = new String[c.getCount()];
        int i = 0;
        for (c.moveToFirst(); !(c.isAfterLast()); c.moveToNext()) {
            idArray[i] = c.getInt(idIndex);
            nameArray[i] = c.getString(nameIndex);
            PWArray[i] = c.getString(pwIndex);          //��pW��ӵ�String������
            picPathArray[i] = c.getString(picIndex);            //��·����ӵ�String������
            dirtyArray[i] = c.getString(dirIndex);
            Log.i(nameArray[i], dirtyArray[i]);
            if (dirtyArray[i].equals("1")) {
                change = i;
                Log.i("getInfo", String.valueOf(change));
            }
            //��¼�ϴ�ʹ�õ��û�λ��
            i++;
        }
        c.close();
        //�ر�Cursor����

        db.close();
         //�ر�SQLiteDatabase����

        return i;

    }

    protected void recover() {

        if (!picPathArray[change].equals("0") && !picPathArray[change].equals(null)) {

        //��ֹû������

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

            //�㲥��Ϣ��ˢ�����

            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
            sendBroadcast(localIntent);

        }


    }

    protected void hidefile(int i) {
        if (!picPathArray[i].equals("0") && !picPathArray[i].equals(null)) {
            File file = new File(picPathArray[i]);                //����·��������ļ�
            String fpath = file.getParentFile().getPath();
            File newfile = new File(fpath + "/.hide/" + file.getName());   //���������ļ���
            file.renameTo(newfile);
            String s = newfile.getPath();
            picPathArray[i] = s;                   //�����ļ�·��
            Log.i("hideFile", s);
            file.delete();
            Uri localUri = Uri.fromFile(file);//�㲥��Ϣ��ˢ�����
            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
            sendBroadcast(localIntent);

        }


    }

    protected void update(int id, int i) {

        //������ݿ����
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, nameArray[i]);
        values.put(PW, PWArray[i]);
        values.put(PICPATH, picPathArray[i]);
        values.put(DIRTY, dirtyArray[i]);

        //��������
        db.update(TABLE_NAME, values, ID + "=?", new String[]{id + ""});
        db.close();


    }

    //����ϵͳ����
    private void EnableSystemKeyguard(boolean bEnable) {
        KeyguardManager mKeyguardManager = null;
        KeyguardManager.KeyguardLock mKeyguardLock = null;

        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        mKeyguardLock = mKeyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        if (bEnable)
            //������
            mKeyguardLock.reenableKeyguard();
        else
            //����
            mKeyguardLock.disableKeyguard();
    }


    //@Override
    //public boolean dispatchKeyEvent(KeyEvent event) {
    // TODO Auto-generated method stub
    //return super.dispatchKeyEvent(event);
    //  return true;
    //}

    //����home_key��back_key

    //�������һ��С���⣬��������д��������home��back���ĺ��� ��������û�����⣬����������������е�ʱ��ֻ��back
    //�ᱻ���Σ���home������ʹ�� ����������������ǲ��Ե�ʱ�����ܹ�ȫ�����εġ�����ĿǰҲ��û���ҵ�ԭ��

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //back_key
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // System.exit(0);
            // return true;

            //���������Ӧ�Ĳ��Ǻܳ��ף��������ڿ���Ӧ��ֱ�ӽ�������Ҳ����exit��0����

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

    //����ĵ���ע�͵Ĵ��벿�־������������������������������home��back���õĴ��룬����home����û��Ч��������������˼���ǲ���
    //���������home����������Ҫ������Ȩ�������������������Ͳ��ܱ�����home��

    //HOME
    //@Override
   // public boolean onKeyDown(int keyCode, KeyEvent event)
   // {
    //    // ���¼����Ϸ��ذ�ť
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

    //����������ε�Ȩ��
   // public void onAttachedToWindow()
   // {
     //   this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
   //     super.onAttachedToWindow();
   // }

    //BACK
    //@Override
    // public boolean onKeyDown(int keyCode, KeyEvent event)
    // {
    //    // ���¼����Ϸ��ذ�ť
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

    //����������ε�Ȩ��
    // public void onAttachedToWindow()
    // {
    //   this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
    //     super.onAttachedToWindow();
    // }


    @Override
    public void onAttachedToWindow() {
        // TODO Auto-generated method stub

        //Ȩ������
        //this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        //super.onAttachedToWindow();

        // try to disable home key.
        //this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
        //super.onAttachedToWindow();
    }


}
