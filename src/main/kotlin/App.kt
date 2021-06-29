import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import kanban.TicketType
import kanban.jira.JiraConnector
import kanban.jira.JiraService
import java.net.URI

class KbTool : CliktCommand() {
    val verbose by option().flag("--no-verbose")
    override fun run() {
        Log.level = if (verbose) {
            Log.Level.DEBUG
        } else {
            Log.Level.INFO
        }
    }
}

class KanbanMetrics : CliktCommand() {
    val startDate: String by argument(help="Start date for the metrics. ie.: 2021-06-01")
    val endDate: String by argument(help="End date for the metrics. ie.: 2021-06-01")
    override fun run() {
        Log debug "Initializing Jira / Issues tracker: ${URI(System.getenv("ATLASSIAN_URL"))}"
        val jira = JiraConnector(
            url = URI(System.getenv("ATLASSIAN_URL")),
            username = System.getenv("ATLASSIAN_USERNAME"),
            password = System.getenv("ATLASSIAN_APIKEY"),
            projectName = System.getenv("JIRA_PROJECT"),
            labelFilters = System.getenv("JIRA_LABELS")?.split(",")?.toSet() ?: emptySet()
        )
        val ticketTracker = JiraService(jira)
        ticketTracker.completedTickets(startDate, endDate).let {
            echo("## User Stories metrics")
            echo("* Throughput: ${it.count(TicketType.UserStory)}")
            echo("* Avg Cycle Time: ${it.average(TicketType.UserStory)}")
            echo("* Min Cycle Time: ${it.min(TicketType.UserStory)}")
            echo("* Max Cycle Time: ${it.min(TicketType.UserStory)}")
            echo("## Tasks/Bugs/Sub-Tasks metrics")
            echo("* Throughput: ${it.count(TicketType.Task, TicketType.SubTask, TicketType.Bug)}")
            echo("* Avg Cycle Time: ${it.average(TicketType.Task, TicketType.SubTask, TicketType.Bug)}")
            echo("* Min Cycle Time: ${it.min(TicketType.Task, TicketType.SubTask, TicketType.Bug)}")
            echo("* Max Cycle Time: ${it.min(TicketType.Task, TicketType.SubTask, TicketType.Bug)}")
            echo("## Other metrics")
            echo("* n. of Bugs: ${it.count(TicketType.Bug)}")
            echo("* n. of Highest Priority Tickets: ${it.incidents().size}")
            echo("* n. of Skipped Tickets: ${it.skippedIssues.size}")
            val issuesSummary = it.issues(TicketType.UserStory,TicketType.Task, TicketType.SubTask, TicketType.Bug).joinToString()
            echo("Tickets: $issuesSummary")
        }
    }
}

fun main(args: Array<String>) = KbTool().subcommands(KanbanMetrics()).main(args)
