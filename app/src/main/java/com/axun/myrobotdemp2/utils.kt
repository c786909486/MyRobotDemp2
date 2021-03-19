package com.axun.myrobotdemp2

import android.content.Context
import android.widget.Toast

/**
 *@packageName com.axun.myrobotdemp2
 *@author kzcai
 *@date 3/10/21
 */
fun Context.showToast(text:String) {
    Toast.makeText(this,text,Toast.LENGTH_SHORT).show()
}

