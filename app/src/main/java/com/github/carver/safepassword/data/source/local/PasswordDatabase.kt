package com.github.carver.safepassword.data.source.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.carver.safepassword.SafePasswordApplication

@Database(version = 1, entities = [PasswordEntity::class])
abstract class PasswordDatabase : RoomDatabase() {

    abstract fun getPasswordDao(): PasswordDao

    companion object {
        @Volatile
        private var instance: PasswordDatabase? = null

        fun getDatabase(): PasswordDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    SafePasswordApplication.getInstance(),
                    PasswordDatabase::class.java,
                    "password_database"
                ).build().also { instance = it }
            }
        }
    }
}