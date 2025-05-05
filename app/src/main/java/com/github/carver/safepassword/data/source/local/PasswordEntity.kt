package com.github.carver.safepassword.data.source.local

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@kotlinx.parcelize.Parcelize
@Entity(tableName = "password")
data class PasswordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val category: String,
    val time: Long,
    val account: String,
    val password: String,
    val remark: String? = null
) : Parcelable {

    fun isTheSameContent(entity: PasswordEntity): Boolean {
        return category == entity.category
                && account == entity.account
                && password == entity.password
                && remark == entity.remark
    }
}