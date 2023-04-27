package com.francisco.paliouras.shefu_final.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.francisco.paliouras.shefu_final.data.dao.RecipeDao
import com.francisco.paliouras.shefu_final.data.entities.Recipe

//database annotation is looking for an abstract class
@Database(entities = [Recipe::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun RecipeDao():RecipeDao

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
                    AppDatabase::class.java, "recipe").build()
                INSTANCE = instance
                return instance
            }
        }
    }

}