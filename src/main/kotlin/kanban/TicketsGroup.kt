package kanban

data class TicketsGroup(
    val issuesDetails: Set<TicketDetails> = setOf(),
    val skippedIssues: Set<String> = setOf(),
    val startDate: String,
    val endDate: String,
) {
    fun skipIssue(issueId: String): TicketsGroup = copy(skippedIssues = skippedIssues union setOf(issueId))
    fun addIssue(details: TicketDetails): TicketsGroup = copy(issuesDetails = issuesDetails union setOf(details))

    fun issues(vararg types: TicketType): List<TicketDetails> =
        issuesDetails.filter { it.issueType in types }

    fun count(vararg types: TicketType): Int = issuesDetails.filter { it.issueType in types }.size

    fun incidents(): List<TicketDetails> = issuesDetails.filter { it.isIncident }

    fun average(vararg types: TicketType): Long = issuesDetails.filter {
        it.issueType in types
    }.map {
        it.cycleTime.toDays()
    }.average().toLong()

    fun min(vararg types: TicketType): Long? = issuesDetails.filter {
        it.issueType in types
    }.map {
        it.cycleTime.toDays()
    }.minOrNull()

    fun max(vararg types: TicketType): Long? = issuesDetails.filter {
        it.issueType in types
    }.map {
        it.cycleTime.toDays()
    }.maxOrNull()

}
