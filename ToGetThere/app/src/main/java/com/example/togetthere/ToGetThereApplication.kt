package com.example.togetthere

import android.app.Application
import com.example.togetthere.data.AppContainer
import com.example.togetthere.data.AppContainerImpl

class ToGetThereApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}