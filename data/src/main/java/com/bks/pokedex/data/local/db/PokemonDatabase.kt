package com.bks.pokedex.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bks.pokedex.data.local.entity.FavoritePokemonEntity
import com.bks.pokedex.data.local.entity.PokemonDetailEntity
import com.bks.pokedex.data.local.entity.PokemonEntity
import com.bks.pokedex.data.local.entity.RemoteKeys

@Database(
    entities = [
        FavoritePokemonEntity::class,
        PokemonEntity::class,
        RemoteKeys::class,
        PokemonDetailEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PokemonDatabase : RoomDatabase() {
    abstract val pokemonDao: PokemonDao
    abstract val remoteKeysDao: RemoteKeysDao
}
