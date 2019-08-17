package com.mindorks.modularisation_playground

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var splitInstallManager: SplitInstallManager
    lateinit var request: SplitInstallRequest
    val DYNAMIC_FEATURE = "news_feature"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initDynamicModules()
        setClickListeners()
    }

    private fun initDynamicModules() {
        splitInstallManager = SplitInstallManagerFactory.create(this)
        request = SplitInstallRequest
            .newBuilder()
            .addModule(DYNAMIC_FEATURE)
            .build()
    }

    private fun setClickListeners() {
        buttonClick.setOnClickListener {
            if (!isDynamicFeatureDownloaded(DYNAMIC_FEATURE)) {
                downloadFeature()
            } else {
                Log.d("MainActivity", "Downloaded")
                buttonDeleteNewsModule.visibility = View.VISIBLE
                buttonOpenNewsModule.visibility = View.VISIBLE

            }

        }

        buttonOpenNewsModule.setOnClickListener {
            val intent = Intent()
                .setClassName(this, "com.mindorks.news_feature.newsloader.NewsLoaderActivity")
            startActivity(intent)
        }
        buttonDeleteNewsModule.setOnClickListener {
            val list = ArrayList<String>()
            list.add(DYNAMIC_FEATURE)
            uninstallDynamicFeature(list)
        }
    }

    private fun downloadFeature() {
        splitInstallManager.startInstall(request)
            .addOnFailureListener {
                Log.d("MainActivity", it.localizedMessage.toString())

            }
            .addOnSuccessListener {
                Log.d("MainActivity", it.toString())
                buttonOpenNewsModule.visibility = View.VISIBLE
                buttonDeleteNewsModule.visibility = View.VISIBLE

            }
            .addOnCompleteListener {
                Log.d("MainActivity", it.result.toString())

            }
        var mySessionId = 0
        val listener = SplitInstallStateUpdatedListener {
            mySessionId = it.sessionId()
            when (it.status()) {
                SplitInstallSessionStatus.DOWNLOADING -> {
                    val totalBytes = it.totalBytesToDownload()
                    val progress = it.bytesDownloaded()
                    // Update progress bar.
                }
                SplitInstallSessionStatus.INSTALLING -> Log.d("Status", "INSTALLING")
                SplitInstallSessionStatus.INSTALLED -> Log.d("Status", "INSTALLED")
                SplitInstallSessionStatus.FAILED -> Log.d("Status", "FAILED")
                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> startIntentSender(
                    it.resolutionIntent().intentSender,
                    null,
                    0,
                    0,
                    0
                )

            }

        }
    }

    private fun isDynamicFeatureDownloaded(feature: String): Boolean =
        splitInstallManager.installedModules.contains(feature)


    private fun uninstallDynamicFeature(list: List<String>) {
        splitInstallManager.deferredUninstall(list)
            .addOnSuccessListener {
                buttonDeleteNewsModule.visibility = View.GONE
                buttonOpenNewsModule.visibility = View.GONE
            }
    }
}
