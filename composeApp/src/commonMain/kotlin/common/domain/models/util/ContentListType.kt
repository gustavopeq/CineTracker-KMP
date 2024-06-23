package common.domain.models.util

enum class ContentListType(
    val type: String,
) {
    MOVIE_NOW_PLAYING("now_playing"),
    MOVIE_POPULAR("popular"),
    MOVIE_TOP_RATED("top_rated"),
    MOVIE_UPCOMING("upcoming"),
    SHOW_AIRING_TODAY("airing_today"),
    SHOW_POPULAR("popular"),
    SHOW_TOP_RATED("top_rated"),
    SHOW_ON_THE_AIR("on_the_air"),
}
