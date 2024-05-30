package com.lokavo.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lokavo.data.local.entity.History

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(history: History)

    @Query("SELECT * FROM history ORDER BY date DESC")
    fun getAll(): LiveData<List<History>>

    @Query("SELECT * FROM history WHERE latitude = :latitude AND longitude = :longitude LIMIT 1")
    fun findByLatLong(latitude: Double, longitude: Double): History?

    @Update
    fun update(history: History)

    @Delete
    fun delete(history: History)

    @Query("DELETE FROM history")
    fun deleteAll()
}