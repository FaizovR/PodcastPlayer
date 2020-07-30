package ru.faizovr.PodPlay.repository

import ru.faizovr.PodPlay.model.Podcast

class PodcastRepo {
    fun getPodcast(feedUrl: String, callback: (Podcast?) -> Unit) {
        callback(
            Podcast(feedUrl, "No name", "No description", "No image")
        )
    }
}