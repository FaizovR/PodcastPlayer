package ru.faizovr.PodPlay.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import ru.faizovr.PodPlay.model.Episode
import ru.faizovr.PodPlay.model.Podcast
import ru.faizovr.PodPlay.repository.PodcastRepo
import ru.faizovr.PodPlay.util.DateUtils
import java.util.*

class PodcastViewModel(application: Application) :
    AndroidViewModel (application) {
    var podcastRepo: PodcastRepo? = null
    var activePodcastViewData: PodcastViewData? = null
    private var activePodcast: Podcast? = null
    var livePodcastData: LiveData<List<SearchViewModel.PodcastSummaryViewData>>? = null

    data class PodcastViewData (
        var subscribed: Boolean = false,
        var feedTitle: String? = "",
        var feedUrl: String? = "",
        var feedDesc: String? = "",
        var imageUrl: String? = "",
        var episodes: List<EpisodeViewData>
    )

    data class EpisodeViewData (
        var guid: String? = "",
        var title: String? = "",
        var description: String? = "",
        var mediaUrl: String? = "",
        var releaseDate: Date? = null,
        var duration: String? = ""
    )

    private fun episodesToEpisodesView(episodes: List<Episode>):
            List<EpisodeViewData> {
        return episodes.map {
            EpisodeViewData(it.guide, it.title, it.description, it.mediaUrl, it.releaseDate, it.duration)
        }
    }

    private fun podcastToPpdcastView(podcast: Podcast): PodcastViewData {
        return PodcastViewData(
            podcast.id != null,
            podcast.feedTitle,
            podcast.feedUrl,
            podcast.feedDesc,
            podcast.imageUrl,
            episodesToEpisodesView(podcast.episodes)
        )
    }

    fun getPodcast(podcastSummaryViewData: SearchViewModel.PodcastSummaryViewData,
                   callback: (PodcastViewData?) -> Unit)  {
        val  repo = podcastRepo ?: return
        val feedUrl = podcastSummaryViewData.feedUrl ?: return

        repo.getPodcast(feedUrl) {
            it?.let {
                it.feedTitle = podcastSummaryViewData.name ?: ""
                it.imageUrl = podcastSummaryViewData.imageUrl ?: ""
                activePodcastViewData = podcastToPpdcastView(it)
                activePodcast = it
                callback(activePodcastViewData)
            }
        }
    }

    fun saveActivePodcast() {
        val repo = podcastRepo ?: return
        activePodcast?.let {
            repo.save(it)
        }
    }

    private fun podcastToSummaryView(podcast: Podcast) : SearchViewModel.PodcastSummaryViewData {
        return SearchViewModel.PodcastSummaryViewData(
            podcast.feedTitle,
            DateUtils.dateToShortDate(podcast.lastUpdated),
            podcast.imageUrl,
            podcast.feedUrl
        )
    }

    fun getPodcasts(): LiveData<List<SearchViewModel.PodcastSummaryViewData>>? {
        val repo = podcastRepo ?: return null

        if (livePodcastData == null) {
            val liveData = repo.getAll()
            livePodcastData = Transformations.map(liveData) {podcastList ->
                podcastList.map { podcast ->
                    podcastToSummaryView(podcast)
                }
            }
        }

        return livePodcastData
    }

    fun deleteActivePodcast() {
        val repo = podcastRepo ?: return
        activePodcast?.let {
            repo.delete(it)
        }
    }
}