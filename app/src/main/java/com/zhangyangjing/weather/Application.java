package com.zhangyangjing.weather;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by zhangyangjing on 16/11/2016.
 */

public class Application extends android.app.Application {

   @Override
   public void onCreate() {
      super.onCreate();
      JodaTimeAndroid.init(this);
   }
}
