package network.di

import io.ktor.client.HttpClient
import network.client
import org.koin.dsl.module

actual val apiModule = module {
    single<HttpClient> { client }
}
