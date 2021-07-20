package kanban.jira

import Log
import com.atlassian.jira.rest.client.api.domain.Issue
import kanban.TicketDetails
import kanban.TicketService
import kanban.TicketType
import kanban.TicketsGroup
import java.time.*
import java.time.temporal.ChronoField

class JiraService(
    private val jira: JiraConnector
) : TicketService {

    override fun completedTickets(startDate: String, endDate: String): TicketsGroup {
        var result = TicketsGroup(startDate = startDate, endDate = endDate)
        jira.getDoneTicketsForPeriod(startDate, endDate)?.apply {
            Log debug "Search result: ${this.total} items"
            issues.forEach { issue ->
                val id = issue.key

                result = if ("skip-metrics" in issue.labels) {
                    result.skipIssue(id)
                } else {
                    computeCycleTime(issue, id)?.let { result.addIssue(it) } ?: result.skipIssue(id)
                }
            }
        }

        return result
    }

    private fun discoverIssueType(issue: Issue) = if (issue.issueType.isSubtask) {
        TicketType.SubTask
    } else {
        when (issue.issueType.name) {
            "Task" -> TicketType.Task
            "Story" -> TicketType.UserStory
            "Bug" -> TicketType.Bug
            else -> {
                Log.warning("Issue ${issue.key} has an unexpected type ${issue.issueType.name}")
                null
            }
        }
    }

    private fun computeCycleTime(issue: Issue, id: String): TicketDetails? = discoverIssueType(issue)?.let {
        // compute times
        var start: Instant? = null
        var end: Instant? = null
        val changelogs = jira.getIssueChangelog(id)
        changelogs?.forEach { group ->
            val date = Instant.ofEpochMilli(group.created.toInstant().millis)
            group.items.filter { it.field == "status" }.forEach { item ->
                if (item.toString == "In Progress") {
                    start = date
                } else if (item.field == "status" && item.toString == "Done") {
                    end = date
                }
            }
        }
        // create new issue detail if
        if (start != null && end != null) {
            val tz = ZoneId.of("UTC")
            val weekStart = LocalDateTime.ofInstant(start, tz).get(ChronoField.ALIGNED_WEEK_OF_YEAR)
            val weekEnd = LocalDateTime.ofInstant(end, tz).get(ChronoField.ALIGNED_WEEK_OF_YEAR)
            val weekendDays = 2 * (weekEnd - weekStart)

            Log debug "Calculating duration for task $id: start week: $weekStart, end week $weekEnd, weekend days: $weekendDays"
            val duration = Duration.between(start, end).minusDays(weekendDays.toLong())
            TicketDetails(id, it, duration , isIncident = issue.priority?.name == "Highest")
        } else {
            Log warning "Issue ${issue.key} has no start or end date  $start - $end"
            null
        }
    }
}