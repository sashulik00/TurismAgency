package com.example.travelagency.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.travelagency.data.entity.EmployeeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeesDao {
    @Query("SELECT * FROM employees ORDER BY fullName")
    fun observeAll(): Flow<List<EmployeeEntity>>

    @Query("SELECT * FROM employees WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): EmployeeEntity?

    @Insert
    suspend fun insert(employee: EmployeeEntity): Long

    @Update
    suspend fun update(employee: EmployeeEntity)

    @Query("DELETE FROM employees WHERE id = :id")
    suspend fun deleteById(id: Long)
}
