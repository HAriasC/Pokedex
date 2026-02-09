package com.bks.pokedex.data.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.bks.pokedex.data.local.db.PokemonDao
import com.bks.pokedex.data.local.db.PokemonDatabase
import com.bks.pokedex.data.local.prefs.AuthLocalDataSource
import com.bks.pokedex.data.local.prefs.AuthLocalDataSourceImpl
import com.bks.pokedex.data.remote.PokeApi
import com.bks.pokedex.data.remote.auth.AuthInterceptor
import com.bks.pokedex.data.remote.auth.TokenAuthenticator
import com.bks.pokedex.data.repository.AuthRepositoryImpl
import com.bks.pokedex.data.repository.PokemonRepositoryImpl
import com.bks.pokedex.domain.repository.AuthRepository
import com.bks.pokedex.domain.repository.PokemonRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EncryptedPrefs

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @EncryptedPrefs
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            "encrypted_auth_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @Provides
    @Singleton
    fun provideAuthLocalDataSource(@EncryptedPrefs sharedPreferences: SharedPreferences): AuthLocalDataSource {
        return AuthLocalDataSourceImpl(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun providePokeApi(okHttpClient: OkHttpClient): PokeApi {
        return Retrofit.Builder()
            .baseUrl(PokeApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(PokeApi::class.java)
    }

    @Provides
    @Singleton
    fun providePokemonDatabase(@ApplicationContext context: Context): PokemonDatabase {
        return Room.databaseBuilder(
            context,
            PokemonDatabase::class.java,
            "pokemon_db"
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    @Singleton
    fun providePokemonDao(db: PokemonDatabase): PokemonDao {
        return db.pokemonDao
    }

    @Provides
    @Singleton
    fun providePokemonRepository(
        api: PokeApi,
        dao: PokemonDao,
        db: PokemonDatabase
    ): PokemonRepository {
        return PokemonRepositoryImpl(api, dao, db)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        localDataSource: AuthLocalDataSource
    ): AuthRepository {
        return AuthRepositoryImpl(localDataSource)
    }
}
