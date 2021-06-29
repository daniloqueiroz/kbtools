import java.time.Instant

data class LogEntry(
    val severity: String,
    val message: String,
    val ts: Instant = Instant.now()
) {
    override fun toString(): String = "[$severity] $ts $message"
}

object Log {
    enum class Level(val n: Int) {
        DEBUG(0), INFO(1), WARNING(2), ERROR(3)
    }

    var level: Level = Level.ERROR

    infix fun debug(msg: String): Log = log(Level.DEBUG, msg)
    infix fun info(msg: String): Log = log(Level.INFO, msg)
    infix fun warning(msg: String): Log = log(Level.WARNING, msg)
    infix fun error(msg: String): Log = log(Level.ERROR, msg)
    infix fun exception(err: Throwable): Log = log(Level.ERROR, err.stackTraceToString())


    infix fun <T> ifFails(block: () -> T): T {
        try {
            return block()
        } catch (t: Throwable) {
            Log error t.localizedMessage exception t
            throw t
        }
    }

    // internal functions
    private fun log(level: Level, msg: String): Log {
        level.takeIf { level.n >= Log.level.n }?.let {
            val entry = LogEntry(
                severity=level.name,
                message=msg,
            )
            println(entry)
            this
        }
        return this
    }
}