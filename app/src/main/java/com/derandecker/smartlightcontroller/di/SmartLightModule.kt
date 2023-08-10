package com.derandecker.smartlightcontroller.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.derandecker.smartlightcontroller.LoggerInterface
import com.derandecker.smartlightcontroller.LoggerInterfaceImpl
import com.derandecker.smartlightcontroller.coroutine.CoroutineDispatchProvider
import com.derandecker.smartlightcontroller.coroutine.CoroutineDispatchProviderImpl
import com.derandecker.smartlightcontroller.database.LightDao
import com.derandecker.smartlightcontroller.database.LightDatabase
import com.derandecker.smartlightcontroller.preferences.EncryptedPreferencesImpl
import com.derandecker.smartlightcontroller.preferences.EncryptedPreferencesInterface
import com.derandecker.smartlightcontroller.shade.ShadeImpl
import com.derandecker.smartlightcontroller.shade.ShadeInterface
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import inkapplications.shade.core.Shade
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface SmartLightModule {
    @Binds
    fun bindsDispatcher(impl: CoroutineDispatchProviderImpl): CoroutineDispatchProvider

    @Binds
    fun bindsPreferences(impl: EncryptedPreferencesImpl): EncryptedPreferencesInterface

    @Binds
    fun bindsShade(impl: ShadeImpl): ShadeInterface

    @Binds
    fun bindLogger(impl: LoggerInterfaceImpl): LoggerInterface

    companion object {
        @Singleton
        @Provides
        fun provideShade(): Shade {
            return Shade()
        }

        @Singleton
        @Provides
        fun provideDatabase(@ApplicationContext context: Context): LightDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                LightDatabase::class.java,
                "light_database"
            ).build()
        }

        @Provides
        fun provideDao(database: LightDatabase): LightDao {
            return database.lightDao()
        }

        @Provides
        fun provideMasterKey(@ApplicationContext context: Context): MasterKey {
            return MasterKey.Builder(context.applicationContext)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
        }

        @Provides
        fun provideEncryptedSharedPreferences(
            @ApplicationContext context: Context,
            masterKey: MasterKey
        ): SharedPreferences {
            return EncryptedSharedPreferences.create(
                context.applicationContext,
                "com_derandecker_shared_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }
}
