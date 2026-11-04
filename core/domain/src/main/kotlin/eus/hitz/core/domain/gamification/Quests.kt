package eus.hitz.core.domain.gamification

import kotlin.random.Random

enum class QuestType { EarnXp, CompleteSessions, PerfectSessions, CompleteStory, CompleteReview }

/** A daily quest. Its title is built by the UI from [type] and [target]. */
data class Quest(val type: QuestType, val target: Int, val gems: Int) {
    val id: String get() = "${type.name}-$target"
}

data class QuestProgress(val quest: Quest, val progress: Int, val claimed: Boolean) {
    val isDone: Boolean get() = progress >= quest.target
}

/**
 * Three quests a day, the same for the whole day: always an XP goal, plus two of the others.
 * Progress counts today's sessions; the gems are granted as soon as a quest is done.
 */
object DailyQuests {
    private val xpGoals =
        listOf(Quest(QuestType.EarnXp, 20, 5), Quest(QuestType.EarnXp, 30, 10), Quest(QuestType.EarnXp, 50, 15))
    private val others = listOf(
        listOf(Quest(QuestType.CompleteSessions, 2, 5), Quest(QuestType.CompleteSessions, 3, 10)),
        listOf(Quest(QuestType.PerfectSessions, 1, 5), Quest(QuestType.PerfectSessions, 2, 10)),
        listOf(Quest(QuestType.CompleteStory, 1, 10)),
        listOf(Quest(QuestType.CompleteReview, 1, 10)),
    )

    fun forDay(epochDay: Long): List<Quest> {
        val random = Random(epochDay)
        return listOf(xpGoals.random(random)) + others.shuffled(random).take(2).map { it.random(random) }
    }

    fun progress(quest: Quest, today: List<Activity>): Int = when (quest.type) {
        QuestType.EarnXp -> today.sumOf { it.xp }
        QuestType.CompleteSessions -> today.size
        QuestType.PerfectSessions -> today.count { it.perfect }
        QuestType.CompleteStory -> today.count { it.kind == ActivityKind.Story }
        QuestType.CompleteReview -> today.count { it.kind == ActivityKind.Review }
    }.coerceAtMost(quest.target)

    fun rewardId(epochDay: Long, quest: Quest): String = "quest:$epochDay:${quest.id}"
}
