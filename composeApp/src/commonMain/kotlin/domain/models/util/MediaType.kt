package domain.models.util

enum class MediaType {
    MOVIE,
    SHOW,
    PERSON,
    UNKNOWN,
    ;

    companion object {
        fun getType(typeName: String?): MediaType =
            when (typeName?.lowercase()) {
                "movie" -> MOVIE
                "show", "tv" -> SHOW
                "person", "people" -> PERSON
                else -> UNKNOWN
            }
    }
}
