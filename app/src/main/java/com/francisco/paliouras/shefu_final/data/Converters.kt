package com.francisco.paliouras.shefu_final.data

import androidx.room.TypeConverter
import com.francisco.paliouras.shefu_final.data.entities.Ingredient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringToList(value: String): List<String> {
        return value.split(',').map { it.trim() }
    }

    @TypeConverter
    fun fromListToString(list: List<String>): String {
        return list.joinToString(separator = ", ")
    }

    @TypeConverter
    fun fromJsonToIngredientList(json: String): List<Ingredient>? {
        val type = object : TypeToken<List<Ingredient>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromIngredientListToJson(ingredientList: List<Ingredient>?): String {
        return gson.toJson(ingredientList)
    }
}
