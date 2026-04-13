package auth.network

import com.projects.cinetracker.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

actual val supabaseClient: HttpClient = HttpClient(Darwin) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }
            )
        }
        defaultRequest {
            url(BuildKonfig.SUPABASE_URL)
            headers.append("apikey", BuildKonfig.SUPABASE_ANON_KEY)
            contentType(ContentType.Application.Json)
        }
    }
