package ru.faizovr.PodPlay.model

import java.util.*

data class Episode (
    var guide: String = "",
    var title: String = "",
    var description: String = "",
    var mediaUrl: String = "",
    var mimeType: String = "",
    var releaseDate: Date = Date(),
    var duration: String = ""
)