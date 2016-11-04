package com.zhangyangjing.weather.ui;

import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.zhangyangjing.weather.R;
import com.zhangyangjing.weather.provider.weather.WeatherContract;
import com.zhangyangjing.weather.service.DataBootstrapService;
import com.zhangyangjing.weather.settings.SettingsUtil;
import com.zhangyangjing.weather.util.AccountUtil;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityGuide extends AppCompatActivity {
    private static final String TAG = ActivityGuide.class.getSimpleName();

    private boolean mVisible = false;
    private boolean mUIFinish = false;
    private boolean mBGFinish = false;
    private boolean mRegiested = false;
    private MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SettingsUtil.setCurrentCity(this, "CN101120501"); // TODO: JUST FOR TEST

        if (false == DataBootstrapService.startDatastrapIfNecessary(this)) {
            startMainActivityAndFinish();
            return;
        }

        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);

        initSync();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DataBootstrapService.ACTION_IMPORT_FINISH);
        registerReceiver(myBroadcastReceiver, intentFilter);
        mRegiested = true;
    }

    @Override
    protected void onResume() {
        super.onPostResume();
        mVisible = true;
        startMainActivityAndFinishIFNeed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVisible = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRegiested)
            unregisterReceiver(myBroadcastReceiver);
    }

    @OnClick(R.id.dummy_button)
    public void onClick(View view) {
        mUIFinish = true;
        startMainActivityAndFinishIFNeed();
    }

    private void startMainActivityAndFinishIFNeed() {
        if (mUIFinish && mBGFinish)
            startMainActivityAndFinish();
    }

    private void startMainActivityAndFinish() {
        startActivity(new Intent(this, ActivityMain.class));
        if (mRegiested) {
            unregisterReceiver(myBroadcastReceiver);
            mRegiested = false;
        }
        finish();
    }

    private void initSync() {
        Account account = AccountUtil.getSyncAccount(this);
        getContentResolver().setSyncAutomatically(account, WeatherContract.CONTENT_AUTHORITY, true);
        getContentResolver().setIsSyncable(account, WeatherContract.CONTENT_AUTHORITY, 1);
        getContentResolver().setMasterSyncAutomatically(true);

        Bundle bundle = new Bundle();
        getContentResolver().addPeriodicSync(account, WeatherContract.CONTENT_AUTHORITY, bundle, 1);
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "onReceive:" + intent.getAction());
            if (false == intent.getAction().equals(DataBootstrapService.ACTION_IMPORT_FINISH))
                return;

            mBGFinish = true;
            if (true == mVisible)
                startMainActivityAndFinishIFNeed();
        }
    }
}
