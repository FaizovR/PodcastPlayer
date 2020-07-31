package ru.faizovr.PodPlay.repository

import kotlinx.coroutines.*
import ru.faizovr.PodPlay.model.Episode
import ru.faizovr.PodPlay.model.Podcast
import ru.faizovr.PodPlay.service.FeedService
import ru.faizovr.PodPlay.service.RssFeedResponse
import ru.faizovr.PodPlay.service.RssFeedService
import ru.faizovr.PodPlay.util.DateUtils

class PodcastRepo (private var feedService: FeedService) {
    fun getPodcast(feedUrl: String, callback: (Podcast?) -> Unit) {
        feedService.getFeed(feedUrl) { feedResponse ->
            var podcast: Podcast? = null
            if (feedResponse != null) {
                podcast = rssResponseToPodcast(feedUrl, "", feedResponse)
            }
            GlobalScope.launch(Dispatchers.Main) {
                callback(podcast)
            }
        }
    }

    private fun rssItemsToEpisodes(episodeResponses: List<RssFeedResponse.EpisodeResponse>): List<Episode> {
        return episodeResponses.map {
            Episode(
                it.guid ?: "",
                it.title ?: "",
                it.description ?: "",
                it.url ?: "",
                it.type ?: "",
                DateUtils.xmlDateToDate(it.pubDate),
                it.duration ?: ""
            )
        }
    }

    private fun rssResponseToPodcast(feedUrl: String, imageUrl: String, rssResponse: RssFeedResponse) : Podcast? {
        val items = rssResponse.episodes ?: return null

        val description = if (rssResponse.description == "")
            rssResponse.summary else rssResponse.description
        return Podcast(feedUrl, rssResponse.title, description, imageUrl, rssResponse.lastUpdated, episodes = rssItemsToEpisodes(items))
    }
}