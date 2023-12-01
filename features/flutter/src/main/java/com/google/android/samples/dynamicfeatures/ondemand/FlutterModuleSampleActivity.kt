package com.google.android.samples.dynamicfeatures.ondemand

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.samples.dynamicfeatures.BaseSplitActivity
import com.google.android.samples.dynamicfeatures.ondemand.fcode.databinding.ActivityFlutterBinding
import dalvik.system.PathClassLoader
import io.flutter.FlutterInjector
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor

class FlutterModuleSampleActivity : BaseSplitActivity() {
    private var initialized = false
    private val binding: ActivityFlutterBinding by lazy {
        ActivityFlutterBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeFlutterEngine(applicationContext)
        binding.btn.setOnClickListener {
//            startActivity(
//                FlutterActivity.createDefaultIntent(this)
//            )

//            startActivity(
//                FlutterActivity
//                    .withNewEngine()
//                    .initialRoute("/")
//                    .build(this@FlutterModuleSampleActivity)
//            )

            startActivity(
                Intent(
                    this,
                    FlutterConfigActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    private fun initializeFlutterEngine(applicationContext: Context) {
        if (initialized) {
            Log.d("TdcTest", "Flutter initialize Engine is already called")
            return
        }
        val injector = FlutterInjector.instance()
        val flutterLoader = injector.flutterLoader()
        flutterLoader.startInitialization(applicationContext)
        val pathLoader = applicationContext.classLoader as PathClassLoader
        val pathapp = pathLoader.findLibrary("app")
        val pathC = pathLoader.findLibrary("c++_shared")
        val pathTv = pathLoader.findLibrary("trustvision-lib")
        val shellapp = String.format("--aot-shared-library-name=%s", pathapp)
        val shellTv = String.format("--aot-shared-library-name=%s", pathTv)
        flutterLoader.ensureInitializationComplete(
            applicationContext,
            arrayOf(shellapp, shellTv)
        )
        val jni = injector.flutterJNIFactory.provideFlutterJNI()
        val flutterEngine = FlutterEngine(applicationContext, flutterLoader, jni)
//        flutterEngine.navigationChannel.setInitialRoute("/")
        /*** Start executing Dart code to pre-warm the FlutterEngine.  */
        flutterEngine.dartExecutor.executeDartEntrypoint(DartExecutor.DartEntrypoint.createDefault())
        /*** Cache the FlutterEngine to be used by FlutterActivity.  */
        FlutterEngineCache.getInstance().put("flutter_main", flutterEngine)
        initialized = true
        println("TdcTest ~ pathApp:$pathapp")

//        SplitInstallHelper.loadLibrary(applicationContext, libNameTensor);
//        SplitInstallHelper.loadLibrary(applicationContext, libNameConvertImage);

//        Constant.libDir = pathTensor;
    }

}