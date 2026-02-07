package com.bks.pokedex.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bks.pokedex.data.local.entity.FavoritePokemonEntity
import com.bks.pokedex.data.local.entity.PokemonEntity
import com.bks.pokedex.data.local.entity.RemoteKeys

@Database(
    entities = [FavoritePokemonEntity::class, PokemonEntity::class, RemoteKeys::class],
    version = 2,
    exportSchema = false
)
abstract class PokemonDatabase : RoomDatabase() {
    abstract val pokemonDao: PokemonDao
    abstract val remoteKeysDao: RemoteKeysDao
}
