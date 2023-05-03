package com.francisco.paliouras.shefu_final.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.francisco.paliouras.shefu_final.data.dao.RecipeDao
import com.francisco.paliouras.shefu_final.data.dao.ShoppingItemDao
import com.francisco.paliouras.shefu_final.data.entities.Direction
import com.francisco.paliouras.shefu_final.data.entities.Ingredient
import com.francisco.paliouras.shefu_final.data.entities.Recipe
import com.francisco.paliouras.shefu_final.data.entities.ShoppingItem

//database annotation is looking for an abstract class
@Database(entities = [Recipe::class,Ingredient::class, Direction::class, ShoppingItem::class],
    version = 5, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun RecipeDao():RecipeDao

    abstract fun ShoppingItemDao():ShoppingItemDao

    //singleton pattern for the database
    companion object {
        //this is making it so that any writes are readily avaialble to all threads when they happen
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }

            //this is using a background thread to avoid reasources from using it in multiple places
            // at a time causing error writes
            synchronized(lock = this){
                val instance = Room.databaseBuilder(context.applicationContext,
                    AppDatabase::class.java, "recipe")
                    .fallbackToDestructiveMigration() // this destroys the db when schema gets updated to avoid errors
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

}