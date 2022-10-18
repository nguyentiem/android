package khoa.nv.applocker.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import khoa.nv.applocker.data.database.AppLockerDatabase
import khoa.nv.applocker.data.database.lockedapps.LockedAppDao
import khoa.nv.applocker.data.database.pattern.PatternDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppLockerDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, AppLockerDatabase::class.java, "Applocker.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun providePatternDao(appDatabase: AppLockerDatabase) = appDatabase.getPatternDao()

    @Provides
    @Singleton
    fun provideLockedAppDao(appDatabase: AppLockerDatabase) = appDatabase.getLockedAppDao()
}