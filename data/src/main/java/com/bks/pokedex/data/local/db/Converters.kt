package com.bks.pokedex.data.local.db

import androidx.room.TypeConverter
import com.bks.pokedex.data.local.entity.EvolutionEntity
import com.bks.pokedex.data.local.entity.StatEntity
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        if (value == null) return null
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        return moshi.adapter<List<String>>(type).toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        if (value == null) return null
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        return moshi.adapter<List<String>>(type).fromJson(value)
    }

    @TypeConverter
    fun fromStatList(value: List<StatEntity>?): String? {
        if (value == null) return null
        val type = Types.newParameterizedType(List::class.java, StatEntity::class.java)
        return moshi.adapter<List<StatEntity>>(type).toJson(value)
    }

    @TypeConverter
    fun toStatList(value: String?): List<StatEntity>? {
        if (value == null) return null
        val type = Types.newParameterizedType(List::class.java, StatEntity::class.java)
        return moshi.adapter<List<StatEntity>>(type).fromJson(value)
    }

    @TypeConverter
    fun fromEvolutionEntity(value: EvolutionEntity?): String? {
        if (value == null) return null
        return moshi.adapter(EvolutionEntity::class.java).toJson(value)
    }

    @TypeConverter
    fun toEvolutionEntity(value: String?): EvolutionEntity? {
        if (value == null) return null
        return moshi.adapter(EvolutionEntity::class.java).fromJson(value)
    }
}
