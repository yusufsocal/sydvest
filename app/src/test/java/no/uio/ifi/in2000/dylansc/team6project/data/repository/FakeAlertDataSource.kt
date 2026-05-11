package no.uio.ifi.in2000.dylansc.team6project.data.repository

import no.uio.ifi.in2000.dylansc.team6project.data.warning.AlertDataSource
import no.uio.ifi.in2000.dylansc.team6project.data.warning.AlertFeature

class FakeAlertDataSource(
    private val result: List<AlertFeature>?
) : AlertDataSource {
    override suspend fun alertDataSource(): List<AlertFeature>? {
        return result
    }
}