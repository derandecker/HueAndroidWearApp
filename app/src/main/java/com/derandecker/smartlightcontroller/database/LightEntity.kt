package com.derandecker.smartlightcontroller.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LightEntity(
    @PrimaryKey
    val id: String,
    val name: String = "Light not named"
)
