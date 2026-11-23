package com.example.maternalnutrition.database


import androidx.room.*

@Dao
interface WeightDao {

    @Insert
    suspend fun insertWeight(record: WeightRecord): Long

    @Query("""
        SELECT * FROM weight_records
        WHERE userId = :userId
        ORDER BY recordedAt DESC
    """)
    suspend fun getWeightsByUser(userId: Int): List<WeightRecord>

    @Update
    suspend fun updateWeight(record: WeightRecord)

    @Query("""
        DELETE FROM weight_records
        WHERE weightId = :weightId AND userId = :userId
    """)
    suspend fun deleteWeight(weightId: Int, userId: Int)
}