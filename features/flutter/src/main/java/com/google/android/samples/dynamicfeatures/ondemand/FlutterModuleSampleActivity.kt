package com.google.android.samples.dynamicfeatures.ondemand

import android.os.Bundle
import com.google.android.samples.dynamicfeatures.BaseSplitActivity
import com.google.android.samples.dynamicfeatures.ondemand.fcode.databinding.ActivityFlutterBinding
import io.flutter.embedding.android.FlutterActivity

class FlutterModuleSampleActivity : BaseSplitActivity() {
    private val binding: ActivityFlutterBinding by lazy {
        ActivityFlutterBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btn.setOnClickListener {
            startActivity(
                FlutterActivity.createDefaultIntent(this@FlutterModuleSampleActivity)
            )
//            startActivity(
//                FlutterActivity
//                    .withNewEngine()
//                    .initialRoute("/")
//                    .build(this@FlutterModuleSampleActivity)
//            )
        }
    }
}