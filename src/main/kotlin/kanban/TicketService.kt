package kanban

interface TicketService {

    /**
     * Retrieve an IssueGroup of all issues completed between the given
     * date interval (inclusive).
     */
    fun completedTickets(startDate: String, endDate: String): TicketsGroup
}