package domain.models.content

import domain.models.util.MediaType

interface BaseMediaContent {
    val id: Int
    val title: String
    val overview: String
    val poster_path: String
    val mediaType: MediaType
}
