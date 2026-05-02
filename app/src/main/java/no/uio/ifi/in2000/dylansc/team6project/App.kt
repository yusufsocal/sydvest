package no.uio.ifi.in2000.dylansc.team6project

import android.app.Application
import io.ktor.client.HttpClient
import no.uio.ifi.in2000.dylansc.team6project.data.HttpClientProvider

class App : Application() {

    lateinit var httpClient: HttpClient
        private set

    lateinit var jsonHttpClient: HttpClient
        private set

    override fun onCreate() {
        super.onCreate()
        httpClient = HttpClientProvider.createDefaultClient()
        jsonHttpClient = HttpClientProvider.createJsonClient()
    }

    override fun onTerminate() {
        httpClient.close()
        jsonHttpClient.close()
        super.onTerminate()
    }
}