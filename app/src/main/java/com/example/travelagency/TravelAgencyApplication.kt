package com.example.travelagency

import android.app.Application
import com.example.travelagency.data.db.DatabaseProvider
import com.example.travelagency.data.repository.AppContainer

class TravelAgencyApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
