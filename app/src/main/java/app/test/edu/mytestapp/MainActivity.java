package app.test.edu.mytestapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity
{
    private TextView mTextView_start;
    private TextView mTextView_value;
    private Button mButton_on;
    private Button mButton_off;
    private Date mDate;
    private int mValue=0;
    private SharedPreferences mSettings;
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_VALUE = "PrefValue";
    public static final String APP_PREFERENCES_TIME = "PrefTime";
    public static final String BROADCAST_ACTION = "app.test.edu.mytestapp.MainActivity.Broadcast_action";
    private boolean mIsRunning=false;
    private BroadcastReceiver mReciever;
    private Intent mServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView_start = (TextView)findViewById(R.id.textView_start);
        mTextView_value = (TextView)findViewById(R.id.textView_value);
        mButton_on = (Button)findViewById(R.id.btn_on);
        mButton_off = (Button)findViewById(R.id.btn_off);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        mServiceIntent = new Intent(MainActivity.this,MyService.class);

        SharedPreferences.Editor editor = mSettings.edit();
        if(mSettings.contains(MainActivity.APP_PREFERENCES_VALUE))
        {
            mValue = mSettings.getInt(MainActivity.APP_PREFERENCES_VALUE,0);
            mDate = new Date(mSettings.getLong(MainActivity.APP_PREFERENCES_TIME,0));

            mTextView_start.setText(mDate.toString());

        }
        else
        {
            mValue = 0;
            mTextView_start.setText("Not detected yet");
        }
        mTextView_value.setText(Integer.toString(mValue));

        mReciever = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                mValue = intent.getIntExtra(APP_PREFERENCES_VALUE,0);
                mDate = new Date(intent.getLongExtra(APP_PREFERENCES_TIME,0));

                mTextView_start.setText(mDate.toString());
                mTextView_value.setText(Integer.toString(mValue));
            }
        };

        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(mReciever, intFilt);

        mButton_on.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!mIsRunning)
                {
                    mIsRunning = true;
                    startService(mServiceIntent);
                    Toast.makeText(MainActivity.this,"Started",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this,"Already running",Toast.LENGTH_SHORT).show();
                }

            }
        });

        mButton_off.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mIsRunning)
                {
                    mIsRunning = false;
                    stopService(mServiceIntent);
                    Toast.makeText(MainActivity.this,"Stopped",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this,"Already stopped",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {

        unregisterReceiver(mReciever);
        stopService(mServiceIntent);
        super.onDestroy();
    }
}
