package no.uio.ifi.in2000.dylansc.team6project.data.repository

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.dylansc.team6project.data.warningdata.AlertFeature
import no.uio.ifi.in2000.dylansc.team6project.data.warningdata.AlertProperties
import org.junit.Test

class AlertRepositoryTest {
    @Test
    fun `null datasource returns empty list`() = runBlocking {
        val fakeDataSource = FakeAlertDataSource(null)
        val repo = AlertRepository(fakeDataSource)

        val result = repo.getAlertList()

        assertThat(result).isEmpty()
    }

    @Test
    fun `datasource returns list with same amount of alerts`() = runBlocking {
        val fake = FakeAlertDataSource(listOf(
            alert("Oslo"),
            alert("Bergen"),
            alert("Alta")
        ))
        val repo = AlertRepository(fake)

        val result = repo.getAlertList().size

        assertThat(result).isEqualTo(3)
    }

    @Test
    fun `datasource returns alphabetically sorted list`() = runBlocking {
        val fake = FakeAlertDataSource(listOf(
            alert("Oslo"),
            alert("Bergen"),
            alert("Alta")
        ))
        val repo = AlertRepository(fake)

        val result = repo.getAlertList()

        assertThat(result).isEqualTo(listOf(
            alert("Alta"),
            alert("Bergen"),
            alert("Oslo")
        ))
    }

    @Test
    fun `datasource with null returns list with null at the end`() = runBlocking {
        val fake = FakeAlertDataSource(listOf(
            alert(null),
            alert("Bergen"),
            alert("Alta")
        ))
        val repo = AlertRepository(fake)

        val result = repo.getAlertList()

        assertThat(result).isEqualTo(listOf(
            alert("Alta"),
            alert("Bergen"),
            alert(null)
        ))
    }

    @Test
    fun `empty datasource list returns empty list`() = runBlocking {
        val fake = FakeAlertDataSource(listOf())

        val repo = AlertRepository(fake)

        val result = repo.getAlertList()

        assertThat(result).isEmpty()
    }
}

// helper class
private fun alert(area: String?): AlertFeature {
    return AlertFeature(
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
}
