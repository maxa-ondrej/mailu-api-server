package cz.majksa.mailu.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
data class User(
    val email: String,
    val displayName: String,
    val name: String,
    val domain: String,
    val enabled: Boolean,
    val storage: Storage,
    val forward: ForwardRule?,
    val replyRule: ReplyRule?,
    val settings: Settings
)

@Serializable
data class CreateUser(
    val name: String,
    val displayName: String,
    val password: String
)

@Serializable
data class ChangePassword(val password: String)

@Serializable
data class Storage(
    val allocated: Long,
    val used: Long,
    val remaining: Long,
)

@Serializable
data class Settings(
    val admin: Boolean,
    val imap: Boolean,
    val pop: Boolean,
    val spam: Boolean,
    val spamThreshold: Int,
)

@Serializable
data class ForwardRule(
    val keep: Boolean,
    val destination: String
)

@Serializable
data class ReplyRule(
    val subject: String?,
    val body: String?,
    val start: String,
    val end: String
)

object Users : Table("user") {
    /**
     * Important user data
     */
    val email = varchar("email", 255)
    val displayedName = varchar("displayed_name", 160)
    val localPart = varchar("localpart", 80)
    val domainName = varchar("domain_name", 80)
    val password = varchar("password", 255)
    val enabled = bool("enabled").default(true)

    /**
     * Allocated storage
     */
    val quotaBytes = long("quota_bytes").default(1_000_000_000)
    val quotaBytesUsed = long("quota_bytes_used").default(0)

    /**
     * Settings
     */
    val admin = bool("global_admin").default(false)
    val imapEnabled = bool("enable_imap").default(true)
    val popEnabled = bool("enable_pop").default(true)
    val spamEnabled = bool("spam_enabled").default(true)
    val spamThreshold = integer("spam_threshold").default(80)

    /**
     * Forwarding emails
     */
    val forwardEnabled = bool("forward_enabled").default(false)
    val forwardDestination = varchar("forward_destination", 255).nullable()
    val forwardKeep = bool("forward_keep").default(true)

    /**
     * Automatic response to emails
     */
    val replyEnabled = bool("reply_enabled").default(false)
    val replySubject = varchar("reply_subject", 255).nullable()
    val replyBody = text("reply_body").nullable()
    val replyStart = date("reply_startdate").default(LocalDate.of(1900, 1, 1))
    val replyEnd = date("reply_enddate").default(LocalDate.of(2999, 12, 31))

    override val primaryKey = PrimaryKey(email)
}

fun getEmail(domain: Domain, name: String) = "$name@${domain.name}"

fun getAlias(user: User, alias: String) = "$alias@${user.domain}"

fun resultRowToUser(row: ResultRow) = User(
    email = row[Users.email],
    domain = row[Users.domainName],
    name = row[Users.localPart],
    enabled = row[Users.enabled],
    displayName = row[Users.displayedName],
    forward = resultRowToForward(row),
    replyRule = resultRowToReply(row),
    storage = Storage(
        allocated = row[Users.quotaBytes],
        used = row[Users.quotaBytesUsed],
        remaining = row[Users.quotaBytes] - row[Users.quotaBytesUsed],
    ),
    settings = Settings(
        admin = row[Users.admin],
        imap = row[Users.imapEnabled],
        pop = row[Users.popEnabled],
        spam = row[Users.spamEnabled],
        spamThreshold = row[Users.spamThreshold]
    )
)

private fun resultRowToForward(row: ResultRow): ForwardRule? {
    if (row[Users.forwardEnabled]) {
        return ForwardRule(
            keep = row[Users.forwardKeep],
            destination = row[Users.forwardDestination] ?: ""
        )
    }

    return null;
}

private fun resultRowToReply(row: ResultRow): ReplyRule? {
    if (row[Users.replyEnabled]) {
        return ReplyRule(
            body = row[Users.replyBody],
            subject = row[Users.replySubject],
            start = row[Users.replyStart].format(DateTimeFormatter.ISO_DATE),
            end = row[Users.replyEnd].format(DateTimeFormatter.ISO_DATE),
        )
    }

    return null;
}