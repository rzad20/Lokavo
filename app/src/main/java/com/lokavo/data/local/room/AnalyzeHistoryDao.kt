package com.lokavo.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lokavo.data.local.entity.AnalyzeHistory

@Dao
interface AnalyzeHistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(analyzeHistory: AnalyzeHistory)

    @Query("SELECT * FROM analyze_history WHERE user_id = :userId ORDER BY date DESC")
    fun getAll(userId: String): LiveData<List<AnalyzeHistory>>

    @Query("SELECT * FROM analyze_history WHERE user_id = :userId AND latitude = :latitude AND longitude = :longitude LIMIT 1")
    fun findByLatLong(userId: String, latitude: Double, longitude: Double): AnalyzeHistory?

    @Update
    fun update(analyzeHistory: AnalyzeHistory)

    @Delete
    fun delete(analyzeHistory: AnalyzeHistory)

    @Query("DELETE FROM analyze_history WHERE user_id = :userId")
    fun deleteAll(userId: String)
}