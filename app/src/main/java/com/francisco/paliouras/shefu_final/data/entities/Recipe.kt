package com.francisco.paliouras.shefu_final.data.entities

import androidx.room.*
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
    @ColumnInfo(name="isFavorite")
    val isFavorite : Boolean = false
)

@Entity(tableName = "ingredient")
data class Ingredient(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var recipe_id: Int,
    val name: String?,
    val amount: Double?,
    val amountType: String?
)

@Entity(tableName = "direction")
data class Direction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val recipe_id: Int,
    val direction: String?,
    val index: Int?,
)

data class RecipeWithRelations(
    @Embedded
    val recipe: Recipe,
    @Relation(parentColumn = "id", entityColumn = "recipe_id")
    val ingredients: List<Ingredient>,
    @Relation(parentColumn = "id", entityColumn = "recipe_id")
    val directions: List<Direction>
)
