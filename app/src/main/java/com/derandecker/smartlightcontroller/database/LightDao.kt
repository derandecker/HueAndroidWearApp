package com.derandecker.smartlightcontroller.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LightDao {
    @Query("SELECT * FROM lightentity ORDER BY name ASC")
    fun getAll(): List<LightEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertLight(light: LightEntity)
}
