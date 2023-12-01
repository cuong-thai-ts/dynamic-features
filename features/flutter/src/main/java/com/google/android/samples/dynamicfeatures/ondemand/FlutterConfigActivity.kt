package com.google.android.samples.dynamicfeatures.ondemand

import android.content.Context
import com.google.android.play.core.splitcompat.SplitCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache

class FlutterConfigActivity : FlutterActivity() {
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        try {
            SplitCompat.installActivity(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun provideFlutterEngine(context: Context): FlutterEngine? {
        return FlutterEngineCache.getInstance()["flutter_main"]
    }

}