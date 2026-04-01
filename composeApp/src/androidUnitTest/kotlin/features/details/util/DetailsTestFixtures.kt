package features.details.util

import network.models.content.common.CastResponse
import network.models.content.common.ContentCastResponse
import network.models.content.common.ContentCreditsResponse
import network.models.content.common.ContentCrewResponse
import network.models.content.common.CountryProviderResponse
import network.models.content.common.PersonResponse
import network.models.content.common.ProviderResponse
import network.models.content.common.VideoResponse
import network.models.content.common.VideosByIdResponse
import network.models.content.common.WatchProvidersResponse
import network.models.content.person.PersonCreditsResponse
import network.models.content.person.PersonImagesResponse
import network.models.content.person.PersonProfileResponse
// ── Response factories (details-specific) ─────────────────────────────────────

fun fakePersonResponse(id: Int = 1, name: String = "Test Person") = PersonResponse(
    id = id,
    name = name,
    profilePath = "/profile.jpg",
    biography = "Test bio"
)

fun fakeContentCreditsResponse(vararg cast: ContentCastResponse, crew: List<ContentCrewResponse> = emptyList()) =
    ContentCreditsResponse(
        id = 1,
        cast = cast.toList(),
        crew = crew
    )

fun fakeContentCrewResponse(id: Int = 1, name: String = "Crew Name", job: String? = null, department: String? = null) =
    ContentCrewResponse(
        id = id,
        name = name,
        job = job,
        department = department
    )

fun fakeContentCastResponse(
    id: Int = 1,
    name: String = "Actor Name",
    profilePath: String? = "/profile.jpg",
    order: Int? = 0
) = ContentCastResponse(
    id = id,
    name = name,
    profilePath = profilePath,
    order = order
)

fun fakeWatchProvidersResponse(
    countryCode: String = "US",
    providers: List<ProviderResponse> = listOf(fakeProviderResponse())
) = WatchProvidersResponse(
    id = 1,
    results = mapOf(countryCode to CountryProviderResponse(flatrate = providers))
)

fun fakeProviderResponse(name: String = "Netflix") = ProviderResponse(
    logo_path = "/logo.png",
    provider_name = name
)

fun fakeVideosByIdResponse(count: Int = 1) = VideosByIdResponse(
    id = 1,
    results = List(count) {
        VideoResponse(key = "abc$it", name = "Trailer $it", published_at = "2024-01-01")
    }
)

fun fakePersonCreditsResponse(vararg cast: CastResponse) = PersonCreditsResponse(
    id = 1,
    cast = cast.toList()
)

fun fakeCastResponse(
    id: Int = 1,
    name: String = "Cast Name",
    posterPath: String? = "/poster.jpg",
    title: String? = "Movie Title"
) = CastResponse(
    id = id,
    name = name,
    posterPath = posterPath,
    _title = title
)

fun fakePersonImagesResponse(vararg filePaths: String?) = PersonImagesResponse(
    id = 1,
    profiles = filePaths.map { path ->
        if (path != null) PersonProfileResponse(file_path = path) else null
    }
)
