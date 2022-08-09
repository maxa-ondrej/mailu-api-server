package cz.majksa.mailu.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

open class Table(table: String) : Table(table) {
    val createdAt = date("created_at")
    val updatedAt = date("updated_at").nullable()
    val comment = varchar("comment", 255).nullable()
}

@Serializable
data class RenameData(val name: String)