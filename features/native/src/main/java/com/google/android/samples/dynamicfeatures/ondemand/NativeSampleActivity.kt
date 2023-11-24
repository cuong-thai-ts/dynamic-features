/*
 * Copyright 2018 Google LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.samples.dynamicfeatures.ondemand

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.android.play.core.splitinstall.SplitInstallHelper
import com.google.android.samples.dynamicfeatures.BaseSplitActivity
import com.google.android.samples.dynamicfeatures.ondemand.ccode.R

/** A simple activity displaying some text coming through via JNI. */
class NativeSampleActivity : BaseSplitActivity() {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        val libName = "trustvision-lib"
        val libc = "c++_shared"
//        val libc = "hello-jni"
        try {
            Log.i("TdcTest", "Loading...$libName along with c++_shared")
//            System.loadLibrary(libc)
            System.load("/data/data/com.google.android.samples.dynamicfeatures.ondemand/files/splitcompat/1/native-libraries/native.config.arm64_v8a/libc++_shared.so")
            Log.i("TdcTest", "Loaded...$libc")
//            System.loadLibrary(libName)
            Log.i("TdcTest", "Loaded...$libName")
        } catch (e: Throwable) {
            Log.e("TdcTest", "traditional library load failed...split loading...")
            e.printStackTrace()
            SplitInstallHelper.loadLibrary(this, libc)
//            libraryFallback(libc, this)
//            libraryFallback(libName, this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SplitInstallHelper.loadLibrary(this, "hello-jni")

        setContentView(R.layout.activity_hello_jni)
        findViewById<TextView>(R.id.hello_textview).text = stringFromJNI()
    }

    /** Read a string from packaged native code. */
    external fun stringFromJNI(): String
}
