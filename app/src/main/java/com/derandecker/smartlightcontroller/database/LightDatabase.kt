package com.derandecker.smartlightcontroller.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LightEntity::class], version = 1, exportSchema = false)
abstract class LightDatabase : RoomDatabase() {
    abstract fun lightDao(): LightDao
}
