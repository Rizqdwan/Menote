package com.example.noteapp.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Notifikasi")
data class NotifEntity (

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "action")
    var action: String? = null,

    @ColumnInfo(name = "massage")
    var massage: String? = null,

    @ColumnInfo(name = "date_time")
    var dataTime: String? = null

//    @ColumnInfo(name = "time_stamp")
//    var timeStamp : Long? = null

)