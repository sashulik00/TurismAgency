package com.example.travelagency.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.travelagency.data.entity.AuditEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AuditDao {
    @Query("SELECT * FROM audit_log ORDER BY at DESC, id DESC LIMIT 200")
    fun observeRecent(): Flow<List<AuditEntryEntity>>
}
