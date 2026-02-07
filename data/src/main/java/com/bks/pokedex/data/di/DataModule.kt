package com.bks.pokedex.data.di

import android.content.Context
import androidx.room.Room
import com.bks.pokedex.data.local.PokemonDao
import com.bks.pokedex.data.local.PokemonDatabase
import com.bks.pokedex.data.remote.PokeApi
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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
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
            .fallbackToDestructiveMigration()
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
        @ApplicationContext context: Context
    ): AuthRepository {
        return AuthRepositoryImpl(context)
    }
}
