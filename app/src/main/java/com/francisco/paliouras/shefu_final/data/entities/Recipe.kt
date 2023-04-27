package com.francisco.paliouras.shefu_final.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.francisco.paliouras.shefu_final.data.Converters

@Entity(tableName = "recipe")
@TypeConverters(Converters::class)
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val name: String?,
    @ColumnInfo(name= "tags")
    val tags : List<String>?,
    @ColumnInfo(name= "ingredients")
    val ingredients : List<Ingredient>?,
    @ColumnInfo(name= "directions")
    val directions : List<String>?,
    @ColumnInfo(name="isFavorite")
    val isFavorite : Boolean = false
)

data class Ingredient(
    val name: String?,
    val amount: Double?,
    val amountType: String?
)

