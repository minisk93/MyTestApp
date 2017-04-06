package app.test.edu.mytestapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MyService extends Service
{
    private int mValue;
    private Date mDate;
    private SharedPreferences mSettings;
    private SharedPreferences.Editor mEditor;
    private boolean mIsRunning;
    private Thread mThread;
    private Intent mIntent;

    public MyService()
    {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        mIsRunning = true;
        mSettings = getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        mEditor = mSettings.edit();
        mIntent =  new Intent(MainActivity.BROADCAST_ACTION);

        mDate = new Date();
        mEditor.putLong(MainActivity.APP_PREFERENCES_TIME,mDate.getTime());
        mEditor.apply();

        mIntent.putExtra(MainActivity.APP_PREFERENCES_TIME,mDate.getTime());
        sendBroadcast(mIntent);


        if(mSettings.contains(MainActivity.APP_PREFERENCES_VALUE))
        {
            mValue = mSettings.getInt(MainActivity.APP_PREFERENCES_VALUE,0);
        }
        else
        {
            mValue = 0;
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        mThread  = new Thread(new MyRunnable());
        mThread.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        mIsRunning = false;
        mEditor.putInt(MainActivity.APP_PREFERENCES_VALUE,mValue);
        mEditor.apply();

        super.onDestroy();
    }

    private class MyRunnable implements Runnable
    {
        @Override
        public void run()
        {

            while(mIsRunning)
            {
                try
                {
                    mValue++;
                    mIntent.putExtra(MainActivity.APP_PREFERENCES_VALUE,mValue);
                    sendBroadcast(mIntent);
                    Thread.sleep(5000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
