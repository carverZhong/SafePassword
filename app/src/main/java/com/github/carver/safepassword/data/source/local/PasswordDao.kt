package com.github.carver.safepassword.data.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PasswordDao {

    @Insert
    suspend fun insert(passwordEntity: PasswordEntity)

    @Update
    suspend fun update(passwordEntity: PasswordEntity)

    @Delete
    suspend fun delete(passwordEntity: PasswordEntity)

    @Query("SELECT * FROM password")
    suspend fun queryAll(): List<PasswordEntity>

    @Query("SELECT * FROM password WHERE category = :category")
    suspend fun queryByCategory(category: String): List<PasswordEntity>
}