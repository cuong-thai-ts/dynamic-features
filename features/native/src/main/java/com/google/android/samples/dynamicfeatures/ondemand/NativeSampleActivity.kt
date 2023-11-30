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

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitinstall.SplitInstallHelper
import com.google.android.samples.dynamicfeatures.BaseSplitActivity
import com.google.android.samples.dynamicfeatures.ondemand.ccode.R
//import com.trustingsocial.tvcoresdk.external.TVCapturingCallBack
//import com.trustingsocial.tvcoresdk.external.TVDetectionError
//import com.trustingsocial.tvcoresdk.external.TVDetectionResult
//import com.trustingsocial.tvcoresdk.external.TVIDConfiguration
//import com.trustingsocial.tvsdk.TrustVisionActivity
//import com.trustingsocial.tvsdk.TrustVisionSDK
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


/** A simple activity displaying some text coming through via JNI. */
class NativeSampleActivity : BaseSplitActivity() {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
//        val libName = "trustvision-lib"
//        val libc = "c++_shared"
//        val libcPath =
//            "/data/data/com.google.android.samples.dynamicfeatures.ondemand/files/splitcompat/1/native-libraries/native.config.arm64_v8a/libc++_shared.so"
////        val libcPath = "/data/user/0/com.google.android.samples.dynamicfeatures.ondemand/files/splitcompat/1/native-libraries/native.config.arm64_v8a/libc++_shared.so"
////        val libc = "hello-jni"
//        var libFile: File? = null
//        try {
//            Log.i("TdcTest", "Loading...$libName along with ${File(libcPath).exists()}: $libcPath ")
////            println("TdcTest ~ source dirs: ${applicationInfo.splitSourceDirs.joinToString(", ")}")
//            println("TdcTest ~ files dir: $filesDir")
//            println("TdcTest ~ lib dir: ${applicationInfo.nativeLibraryDir}")
////            System.loadLibrary(libc)
//            libFile = filesDir.find(libName)
//            println("TdcTest ~ libPath: $libFile")
//            System.load(libcPath)
//            System.load(libFile?.path ?: libcPath)
//            Log.i("TdcTest", "Loaded...")
//        } catch (e: Throwable) {
//            Log.e("TdcTest", "traditional library load failed...split loading...")
//            e.printStackTrace()
//            initSo(libcPath)
//            initSo(
//                    "/data/data/com.google.android.samples.dynamicfeatures.ondemand/files/splitcompat/1/native-libraries/native.config.arm64_v8a/libtrustvision-lib.so"
//            )
////            SplitInstallHelper.loadLibrary(this, libc)
////            libraryFallback(libc, this)
////            libraryFallback(libName, this)
//        }
//        TrustVisionSDK.installDynamicFeature {
//
//        }
    }

    private fun File.find(name: String): File? {
        if (this.name.contains(name, true)) return this
        if (!isDirectory) return null
        listFiles()?.forEach { return it.find(name) ?: return@forEach }
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val configuration = assets.open("serverConfigurations.json").reader().readText()
        SplitInstallHelper.loadLibrary(this, "hello-jni")

        setContentView(R.layout.activity_hello_jni)
        findViewById<TextView>(R.id.hello_textview).let {
            it.text = stringFromJNI()
            it.setOnClickListener {
//                TrustVisionSDK.init(configuration, null, null)
//                TrustVisionActivity.startIDCapturing(
//                    this,
//                    TVIDConfiguration.Builder().build(),
//                    object : TVCapturingCallBack() {
//                        override fun onCanceled() {
//                            println("TdcTest ~ onCanceled")
//                        }
//
//                        override fun onError(error: TVDetectionError) {
//                            println("TdcTest ~ onError: $error")
//                        }
//
//                        override fun onSuccess(result: TVDetectionResult) {
//                            println("TdcTest ~ onSuccess")
//                        }
//                    })
            }
        }
    }

    @Synchronized
    private fun initSo(path: String, loader: ClassLoader? = javaClass.classLoader) {
        val runtime = Runtime.getRuntime()
        val nativeLoadRuntimeMethod: Method = getNativeLoadRuntimeMethod() ?: return
        println("TdcTest ~ native method loaded...initialize so file...")
        var error: String? = null
        try {
            synchronized(runtime) {
                error = nativeLoadRuntimeMethod.invoke(
                    runtime, path, loader, "callerName"
                ) as String
                if (error != null) {
                    throw UnsatisfiedLinkError(error)
                }
            }
        } catch (e: IllegalAccessException) {
            error = "Error: Cannot load $path"
            throw RuntimeException(error, e)
        } catch (e: IllegalArgumentException) {
            error = "Error: Cannot load $path"
            throw RuntimeException(error, e)
        } catch (e: InvocationTargetException) {
            error = "Error: Cannot load $path"
            throw RuntimeException(error, e)
        } finally {
            if (error != null) {
                Log.e(
                    "TdcTest",
                    "Error when loading lib: "
                            + error
                            + " lib hash: "
                            + getLibHash(path)
                            + " search path is "
                            + path
                )
            }
        }
    }

    /** * Logs MD5 of lib that failed loading  */
    private fun getLibHash(libPath: String): String {
        var digestStr: String
        try {
            val libFile = File(libPath)
            val digest = MessageDigest.getInstance("MD5")
            FileInputStream(libFile).use { libInStream ->
                val buffer = ByteArray(4096)
                var bytesRead: Int
                while (libInStream.read(buffer).also { bytesRead = it } > 0) {
                    digest.update(buffer, 0, bytesRead)
                }
                digestStr =
                    String.format("%32x", BigInteger(1, digest.digest()))
            }
        } catch (e: IOException) {
            digestStr = e.toString()
        } catch (e: SecurityException) {
            digestStr = e.toString()
        } catch (e: NoSuchAlgorithmException) {
            digestStr = e.toString()
        }
        return digestStr
    }

    private fun getNativeLoadRuntimeMethod(): Method? {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M/* || Build.VERSION.SDK_INT > 27*/) {
            null
        } else
            try {
            Runtime::class.java.getDeclaredMethod(
                "nativeLoad", String::class.java, ClassLoader::class.java
            ).also {
                it.isAccessible = true
            }
        } catch (e: NoSuchMethodException) {
            Log.w("TdcTest", "Cannot get nativeLoad method", e)
            null
        } catch (e: SecurityException) {
            Log.w("TdcTest", "Cannot get nativeLoad method", e)
            null
        }
    }


    /** Read a string from packaged native code. */
    external fun stringFromJNI(): String
}
