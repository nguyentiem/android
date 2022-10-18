package khoa.nv.applocker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

//val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "applocker.db")

@HiltAndroidApp
class LockerApp : Application()