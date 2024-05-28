package com.example.freeimagefeed.di_hilt

import android.content.Context
import com.example.freeimagefeed.data.local_db.AppDatabase
import com.example.freeimagefeed.data.local_db.CommentDao
import com.example.freeimagefeed.data.local_db.PostDao
import com.example.freeimagefeed.data.local_db.FriendDao
import com.example.freeimagefeed.data.remote.api.DummyRepoApi
import com.example.freeimagefeed.data.remote.api.DummyRepoApiImpt
import com.example.freeimagefeed.data.remote.client.retrofitinterfaces.DummyApiInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideOkHttpClientBuilder(): OkHttpClient.Builder {
        val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
    }

    @Provides
    fun provideDummyApiInterface(okHttpBuilder: OkHttpClient.Builder): DummyApiInterface {
        return Retrofit.Builder()
            .baseUrl("https://dummyapi.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpBuilder.build())
            .build()
            .create(DummyApiInterface::class.java)
    }

    // Dummy Repo Api
    @Provides
    fun provideDummyRepoApi(dummyApiInterface: DummyApiInterface): DummyRepoApi {
        return DummyRepoApiImpt(dummyApiInterface)
    }

    @Provides
    fun provideRoomDb(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideUserPostDao(appDatabase: AppDatabase): PostDao {
        return appDatabase.postDao()
    }

    @Provides
    fun provideCommentDao(appDatabase: AppDatabase): CommentDao {
        return appDatabase.commentDao()
    }

    @Provides
    fun provideUserDao(appDatabase: AppDatabase): FriendDao {
        return appDatabase.userDao()
    }
}