package com.bks.pokedex.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bks.pokedex.data.local.entity.FavoritePokemonEntity
import com.bks.pokedex.data.local.entity.PokemonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(pokemon: FavoritePokemonEntity)

    @Delete
    suspend fun deleteFavorite(pokemon: FavoritePokemonEntity)

    @Query("SELECT * FROM favorite_pokemon")
    fun getFavoritePokemon(): Flow<List<FavoritePokemonEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_pokemon WHERE id = :id)")
    suspend fun isFavorite(id: Int): Boolean

    @Query("SELECT * FROM favorite_pokemon WHERE id = :id")
    suspend fun getFavoriteById(id: Int): FavoritePokemonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPokemon(pokemonList: List<PokemonEntity>)

    @Query("SELECT * FROM pokemon_list ORDER BY id ASC")
    fun getPokemonListPagingById(): PagingSource<Int, PokemonEntity>

    @Query("SELECT * FROM pokemon_list ORDER BY name ASC")
    fun getPokemonListPagingByName(): PagingSource<Int, PokemonEntity>

    @Query("DELETE FROM pokemon_list")
    suspend fun clearAllPokemon()
}
