package no.uio.ifi.in2000.dylansc.team6project.ui.map

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.dylansc.team6project.data.repository.AlertRepository
import no.uio.ifi.in2000.dylansc.team6project.data.repository.LocationRepository
import no.uio.ifi.in2000.dylansc.team6project.data.repository.SearchRepository
import no.uio.ifi.in2000.dylansc.team6project.data.repository.WeatherRepository
import no.uio.ifi.in2000.dylansc.team6project.data.warningdata.AlertFeature
import no.uio.ifi.in2000.dylansc.team6project.data.warningdata.AlertProperties
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.Capability
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.ParentLayer
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WMSCapabilities
import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WMSLayer
import org.junit.Rule
import org.junit.Test
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoroutinesApi::class)
class MapViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // init
    @Test
    fun `init loads layers and alerts and stops loading`() = runTest {
        // lage fake layers og alerts og repos og viewmodel
        val fakeLayers = listOf(layer(title = "Air temperature 2m in MEPS VDIV"))
        val fakeAlerts = listOf(alert("Oslo"))

        val locationRepo = LocationRepository(FakeWMSDataSource(capabilities(fakeLayers)))
        val alertRepo = AlertRepository(FakeAlertDataSource(fakeAlerts))
        val searchRepo = SearchRepository(FakeSearchDataSource())
        val weatherRepo = WeatherRepository(FakeLocationforecastDataSource())

        val viewModel = MapViewModel(locationRepo, alertRepo, searchRepo,AreaData.NORDIC, weatherRepo)

        // vent til alle coroutine-ne er ferdig i viewmodel
        advanceUntilIdle()

        // sjekk at tilstandene er som ble gitt
        val state = viewModel.uiState.value
        assertThat(state.layerList).isEqualTo(fakeLayers)
        assertThat(state.alertList).isEqualTo(fakeAlerts)
        assertThat(state.isLoading).isFalse()
        assertThat(state.area).isEqualTo(AreaData.NORDIC)
    }

    // setSelectedLayer
    @Test
    fun `setSelectedLayer sets empty time when dimension is null`() = runTest {
        // lage fake layers og alerts og repos og viewmodel
        val fakeLayer = layer()

        val fakeLayers = listOf(layer(title = "Temperature"))
        val fakeAlerts = listOf(alert("Oslo"))

        val locationRepo = LocationRepository(FakeWMSDataSource(capabilities(fakeLayers)))
        val alertRepo = AlertRepository(FakeAlertDataSource(fakeAlerts))
        val searchRepo = SearchRepository(FakeSearchDataSource())
        val weatherRepo = WeatherRepository(FakeLocationforecastDataSource())


        val viewModel = MapViewModel(locationRepo, alertRepo, searchRepo,AreaData.NORDIC, weatherRepo)

        advanceUntilIdle()

        viewModel.setSelectedLayer(fakeLayer)

        val state = viewModel.uiState.value
        assertThat(state.selectedLayer).isEqualTo(fakeLayer)
        assertThat(state.selectedTime).isEmpty()
    }

    // setSelectedLayer
    @Test
    fun `setSelectedLayer sets time when dimension exists`() = runTest {
        val fakeLayer = layer(
            title = "Temperature",
            dimension = "2025-01-01T00:00/2025-01-02T00:00/PT1H"
        )

        val fakeLayers = listOf(layer(title = "Temperature"))
        val fakeAlerts = listOf(alert("Oslo"))

        val locationRepo = LocationRepository(FakeWMSDataSource(capabilities(fakeLayers)))
        val alertRepo = AlertRepository(FakeAlertDataSource(fakeAlerts))
        val searchRepo = SearchRepository(FakeSearchDataSource())
        val weatherRepo = WeatherRepository(FakeLocationforecastDataSource())


        val viewModel = MapViewModel(locationRepo, alertRepo, searchRepo,AreaData.NORDIC, weatherRepo)

        advanceUntilIdle()

        viewModel.setSelectedLayer(fakeLayer)

        val state = viewModel.uiState.value

        assertThat(state.selectedLayer).isEqualTo(fakeLayer)
        assertThat(state.selectedTime).isNotEmpty() // sjekker om tiden har faktisk fått et verdi
    }

    // updateTime
    @Test
    fun `updateTime with same time keeps selectedTime as a valid timestamp`() = runTest {
        val fakeLayer = layer(
            title = "Temperature",
            dimension = "2025-01-01T00:00+00:00/2025-01-02T00:00+00:00/PT1H"
        )

        val locationRepo = LocationRepository(FakeWMSDataSource(capabilities(listOf(layer(title = "Temperature")))))
        val alertRepo = AlertRepository(FakeAlertDataSource(listOf(alert("Bergen"))))
        val searchRepo = SearchRepository(FakeSearchDataSource())
        val weatherRepo = WeatherRepository(FakeLocationforecastDataSource())


        val viewModel = MapViewModel(locationRepo, alertRepo, searchRepo, AreaData.NORDIC, weatherRepo)
        advanceUntilIdle()
        viewModel.setSelectedLayer(fakeLayer)

        val before = viewModel.uiState.value.selectedTime!!
        assertThat(before).isNotEmpty()

        viewModel.updateTime(before)
        advanceUntilIdle()

        val after = viewModel.uiState.value.selectedTime
        assertThat(after).isNotEmpty()
        assertThat(runCatching { OffsetDateTime.parse(after) }.isSuccess).isTrue()
    }

    // updateTime
    @Test
    fun `updateTime changes selectedTime when given a new time`() = runTest {
        // Dimension end must be far in the future so coerceTimeToDimension never clamps newTime
        val farFuture = OffsetDateTime.now(java.time.ZoneOffset.UTC).plusYears(5)
        val dimensionEnd = farFuture.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mmZ"))
        val dimension = "2025-01-01T00:00+00:00/$dimensionEnd/PT1H"

        val fakeLayer = layer(title = "Temperature", dimension = dimension)

        val locationRepo = LocationRepository(FakeWMSDataSource(capabilities(listOf(layer(title = "Temperature")))))
        val alertRepo = AlertRepository(FakeAlertDataSource(listOf(alert("Bergen"))))
        val searchRepo = SearchRepository(FakeSearchDataSource())
        val weatherRepo = WeatherRepository(FakeLocationforecastDataSource())


        val viewModel = MapViewModel(locationRepo, alertRepo, searchRepo, AreaData.NORDIC, weatherRepo)
        advanceUntilIdle()
        viewModel.setSelectedLayer(fakeLayer)

        val before = viewModel.uiState.value.selectedTime!!
        val newTime = OffsetDateTime.parse(before)
            .plusHours(1)
            .format(DateTimeFormatter.ISO_INSTANT)

        viewModel.updateTime(newTime)
        advanceUntilIdle()

        val after = viewModel.uiState.value.selectedTime
        assertThat(after).isEqualTo(newTime)
    }
}
// helper classes
private fun alert(area: String?) = AlertFeature(
    geometry = null,
    properties = AlertProperties(
        area = area,
        description = null,
        event = null,
        riskMatrixColor = null,
        severity = null,
        title = null
    )
)

private fun layer(
    name: String = "test_name",
    title: String = "Test Layer",
    dimension: String? = null
) = WMSLayer(
    name = name,
    title = title,
    dimensions = if (dimension != null)
        listOf(no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.WMSDimension(name = "time", value = dimension))
    else emptyList()
)

fun capabilities(layers: List<WMSLayer>) = WMSCapabilities(
    version = "1.3.0",
    capability = Capability(
        rootLayer = ParentLayer(
            title = "root",
            wmsListe = layers
        )
    )
)
