package com.zhangyangjing.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zhangyangjing.weather.service.DataBootstrapService;

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

        if (false == DataBootstrapService.startDatastrapIfNecessary(this)) {
            startMainActivityAndFinish();
            return;
        }

        setContentView(R.layout.activity_guide);

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
        unregisterReceiver(myBroadcastReceiver);
        mRegiested = false;
        finish();
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mBGFinish = true;
            if (true == mVisible)
                startMainActivityAndFinishIFNeed();
        }
    }
}
