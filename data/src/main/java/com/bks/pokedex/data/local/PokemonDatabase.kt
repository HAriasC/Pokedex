package com.bks.pokedex.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bks.pokedex.data.local.entity.FavoritePokemonEntity

@Database(
    entities = [FavoritePokemonEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PokemonDatabase : RoomDatabase() {
    abstract val pokemonDao: PokemonDao
}
