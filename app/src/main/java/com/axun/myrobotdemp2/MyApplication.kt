package com.axun.myrobotdemp2

import android.app.Application
import com.tencent.bugly.Bugly

/**
 *@packageName com.axun.myrobotdemp2
 *@author kzcai
 *@date 3/23/21
 */
class MyApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        Bugly.init(getApplicationContext(), "57c6092d0c", false);

    }
}