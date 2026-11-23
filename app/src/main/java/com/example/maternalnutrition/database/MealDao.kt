package com.example.maternalnutrition.database


import androidx.room.*

@Dao
interface MealDao {

    @Insert
    suspend fun insertMeal(meal: Meal): Long

    @Query("""
        SELECT * FROM meals
        WHERE userId = :userId
        ORDER BY mealDate DESC
    """)
    suspend fun getMealsByUser(userId: Int): List<Meal>

    @Query("""
        SELECT * FROM meals
        WHERE mealId = :mealId AND userId = :userId
        LIMIT 1
    """)
    suspend fun getMealById(mealId: Int, userId: Int): Meal?

    @Update
    suspend fun updateMeal(meal: Meal)

    @Query("""
        DELETE FROM meals
        WHERE mealId = :mealId AND userId = :userId
    """)
    suspend fun deleteMeal(mealId: Int, userId: Int)

    @Query("SELECT COUNT(*) FROM meals WHERE userId = :userId")
    suspend fun countMeals(userId: Int): Int
}