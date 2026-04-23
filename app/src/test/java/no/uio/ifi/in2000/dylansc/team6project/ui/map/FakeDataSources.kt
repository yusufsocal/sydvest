package no.uio.ifi.in2000.dylansc.team6project.ui.map

import no.uio.ifi.in2000.dylansc.team6project.data.warningdata.AlertDataSource
import no.uio.ifi.in2000.dylansc.team6project.data.warningdata.AlertFeature
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WMSCapabilities
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WMSDataSource

class FakeAlertDataSource(
    private val result: List<AlertFeature>?
) : AlertDataSource {
    override suspend fun alertDataSource(): List<AlertFeature>? = result
}

class FakeWMSDataSource(
    private val result: WMSCapabilities?
) : WMSDataSource {
    override suspend fun fetchWmsCapabilities(model: AreaData): WMSCapabilities? = result
}