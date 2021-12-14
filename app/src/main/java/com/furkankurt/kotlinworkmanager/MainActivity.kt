package com.furkankurt.kotlinworkmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.work.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data=Data.Builder().putInt("intKey",1).build()

        val constraints=Constraints.Builder()
                //internete bağlı olsun
            .setRequiredNetworkType(NetworkType.CONNECTED)
                //şarza takılı olmasına gerek yok
            .setRequiresCharging(false)
            .build()

        /*
        val myWorkRequest : WorkRequest = OneTimeWorkRequestBuilder<RefreshDatabase>()
            .setConstraints(constraints)
            .setInputData(data)
                //5saat sonra başlat
            //.setInitialDelay(5,TimeUnit.HOURS)
            //.addTag("myTag")
            .build()

        WorkManager.getInstance(this).enqueue(myWorkRequest)

         */

        val myWorkRequest : PeriodicWorkRequest= PeriodicWorkRequestBuilder<RefreshDatabase>(15,TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setInputData(data)
            .build()
        WorkManager.getInstance(this).enqueue(myWorkRequest)

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(myWorkRequest.id).observe(this,
            Observer {
                if(it.state==WorkInfo.State.RUNNING){
                    println("running")
                }
                else if(it.state==WorkInfo.State.FAILED)
                {
                    println("Faıled")
                }
                else if(it.state==WorkInfo.State.SUCCEEDED)
                {
                    println("Succeeded")
                }
            })
        //iptal etmeyi sağlar.
        //WorkManager.getInstance().cancelAllWork()

        //Chaining


        val oneTimeRequest : OneTimeWorkRequest = OneTimeWorkRequestBuilder<RefreshDatabase>()
            .setConstraints(constraints)
            .setInputData(data)
            .build()

        //bir iş bitince diğerini yap demek
        WorkManager.getInstance(this).beginWith(oneTimeRequest)
            .then(oneTimeRequest)
            .then(oneTimeRequest)
            .enqueue()



    }
}