package fr.local.henripoitier.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.local.henripoitier.BuildConfig
import fr.local.henripoitier.data.BooksDataSource
import fr.local.henripoitier.data.BooksService
import fr.local.henripoitier.data.repository.BooksRepository
import fr.local.henripoitier.utils.NETWORK_TIMEOUT_SECONDS
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()

    @Provides
    fun provideOkHttpClientBuilder(): OkHttpClient.Builder = OkHttpClient.Builder()

    @Provides
    fun providesOkHttpClient(
            okHttpClientBuilder: OkHttpClient.Builder,
    ): OkHttpClient {
        okHttpClientBuilder.connectTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        okHttpClientBuilder.writeTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        okHttpClientBuilder.readTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        okHttpClientBuilder.addNetworkInterceptor(httpLoggingInterceptor)
        return okHttpClientBuilder.build()
    }

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    fun provideCharacterService(retrofit: Retrofit): BooksService =
            retrofit.create(BooksService::class.java)

    @Provides
    fun provideBooksRepository(booksDataSource: BooksDataSource) = BooksRepository(booksDataSource)
}