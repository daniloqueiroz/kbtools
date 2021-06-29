package kanban.jira

import Log
import com.atlassian.jira.rest.client.api.IssueRestClient
import com.atlassian.jira.rest.client.api.domain.ChangelogGroup
import com.atlassian.jira.rest.client.api.domain.SearchResult
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory
import java.net.URI


class JiraConnector(
    url: URI,
    username: String,
    password: String,
    private val projectName: String,
    private val labelFilters: Set<String> = setOf()
) {
    private val client = AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(url, username, password)

    fun getDoneTicketsForPeriod(startDate: String, endDate: String): SearchResult? {
        val labelsFilterQuery = if (labelFilters.isNotEmpty()) {
            "AND labels in (${labelFilters.joinToString(",")})"
        } else {
            ""
        }
        val query = """
            project = $projectName
            AND status = Done
            AND resolutiondate >= $startDate
            AND resolutiondate <= $endDate
            $labelsFilterQuery
            ORDER BY resolutiondate
        """
        Log.debug("Executing JQL: $query")
        return this.client.searchClient.searchJql(query).claim()
    }

    fun getIssueChangelog(key: String): MutableIterable<ChangelogGroup>? =
        this.client.issueClient.getIssue(key, setOf(IssueRestClient.Expandos.CHANGELOG)).claim().changelog
}