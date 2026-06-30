package me.rerere.rikkahub.data.service

/**
 * Memory bank service for storing and retrieving assistant memories.
 * Stub — will be extended with SQLite-based memory storage later.
 */
class MemoryBankService {
    /**
     * Search memories relevant to a query for a given assistant.
     */
    suspend fun searchMemories(query: String, assistantId: String): List<String> = emptyList()

    /**
     * Get daily summary logs for a given assistant.
     */
    suspend fun getDailySummaries(assistantId: String): List<String> = emptyList()

    /**
     * Get usage stats for a given assistant.
     */
    suspend fun getStats(assistantId: String): Map<String, Any> = emptyMap()
}
