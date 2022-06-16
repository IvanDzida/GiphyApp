package com.yolo.di

import android.content.Context
import androidx.room.Room
import com.yolo.data.GiphyMapper
import com.yolo.db.GiphyCacheDatabase
import com.yolo.db.GiphyDbMapper
import com.yolo.db.core.GiphyDatabase
import com.yolo.db.core.GiphyItemDao
import com.yolo.network.GiphyRestApi
import com.yolo.network.Interceptors
import com.yolo.repository.GiphyRepository
import com.yolo.repository.GiphyRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideRetrofit(@ApplicationContext app: Context): Retrofit {
        val cacheSize = 10 * 1024 * 1024 // 10 MB
        val cache = Cache(app.cacheDir, cacheSize.toLong())

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .cache(cache)
            .addInterceptor(Interceptors().cacheInterceptor())
            .build()

        return Retrofit.Builder()
            .baseUrl(GiphyRestApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideGiphyApi(retrofit: Retrofit): GiphyRestApi =
        retrofit.create(GiphyRestApi::class.java)

    @Provides
    @Singleton
    fun provideCacheDatabase(
        @ApplicationContext app: Context,
        dao: GiphyItemDao,
        mapper: GiphyDbMapper
    ): GiphyCacheDatabase {
        Room.databaseBuilder(app, GiphyDatabase::class.java, "database").build()

        return GiphyCacheDatabase(dao, mapper)
    }

    @Provides
    @Singleton
    fun provideGiphyRepository(
        restApi: GiphyRestApi,
        //cache: GiphyCacheDatabase,
        mapper: GiphyMapper
    ): GiphyRepository {
        return GiphyRepositoryImpl(restApi, /*cache, */mapper)
    }
}