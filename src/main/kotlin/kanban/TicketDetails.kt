package kanban

import java.time.Duration

data class TicketDetails(
    val id: String,
    val issueType: TicketType,
    val cycleTime: Duration,
    val isIncident: Boolean = false,
) {
    override fun toString():String = "[$issueType] $id${if (isIncident) "*" else ""} (${cycleTime.toDaysPart()}d,${cycleTime.toHoursPart()}h)"
}
