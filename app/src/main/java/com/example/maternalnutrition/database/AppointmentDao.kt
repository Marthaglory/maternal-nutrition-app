package com.example.maternalnutrition.database

import androidx.room.*

@Dao
interface AppointmentDao {

    @Insert
    suspend fun insertAppointment(appointment: Appointment): Long

    @Query("""
        SELECT * FROM appointments
        WHERE userId = :userId
        ORDER BY appointmentDate ASC
    """)
    suspend fun getAppointmentsByUser(userId: Int): List<Appointment>

    @Update
    suspend fun updateAppointment(appointment: Appointment)

    @Query("""
        DELETE FROM appointments
        WHERE appointmentId = :appointmentId AND userId = :userId
    """)
    suspend fun deleteAppointment(appointmentId: Int, userId: Int)
}