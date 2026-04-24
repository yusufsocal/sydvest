package no.uio.ifi.in2000.dylansc.team6project.ui.map

import no.uio.ifi.in2000.dylansc.team6project.data.weatherdata.AreaData
import org.junit.Assert.*
import org.junit.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Unit tests for logic currently embedded in MapScreen.kt.
 *
 * These tests are written BEFORE refactoring so that the same tests can be run
 * AFTER moving logic into a utility object / ViewModel to verify nothing broke.
 *
 * Each test group maps to a function you should extract during refactoring:
 *
 *   sliderPositionToIsoTime()        → MapUtils.kt  (or MapViewModel)
 *   stripLayerSuffix()               → MapUtils.kt
 *   translateLayerTitle()            → MapUtils.kt
 *   getAllowedLayerTitles()          → MapUtils.kt
 *   alertColorToHex()                → MapUtils.kt
 *   buildWmsBbox()                   → MapUtils.kt
 *   buildWmsUrl()                    → MapUtils.kt
 */
class MapScreenLogicTest {

    // -------------------------------------------------------------------------
    // 1. sliderPositionToIsoTime
    //    Source: LaunchedEffect(sliderPosition) + Slider.onValueChange
    //
    //    Logic:
    //      val now = OffsetDateTime.now(UTC)
    //          .withMinute(0).withSecond(0).withNano(0)
    //          .plusHours(sliderPosition.toLong())
    //      return now.format(DateTimeFormatter.ISO_INSTANT)
    // -------------------------------------------------------------------------

    /** Helper that replicates exactly what the screen does today. */
    private fun sliderPositionToIsoTime(sliderPosition: Float, fakeNow: OffsetDateTime): String {
        val base = fakeNow.withMinute(0).withSecond(0).withNano(0)
        return base.plusHours(sliderPosition.toLong())
            .format(DateTimeFormatter.ISO_INSTANT)
    }

    @Test
    fun `sliderPosition 0 returns current hour in UTC ISO format`() {
        val fakeNow = OffsetDateTime.of(2024, 6, 1, 14, 37, 22, 500, ZoneOffset.UTC)
        val result = sliderPositionToIsoTime(0f, fakeNow)
        assertEquals("2024-06-01T14:00:00Z", result)
    }

    @Test
    fun `sliderPosition 1 adds one hour`() {
        val fakeNow = OffsetDateTime.of(2024, 6, 1, 14, 0, 0, 0, ZoneOffset.UTC)
        val result = sliderPositionToIsoTime(1f, fakeNow)
        assertEquals("2024-06-01T15:00:00Z", result)
    }

    @Test
    fun `sliderPosition 24 advances by one full day`() {
        val fakeNow = OffsetDateTime.of(2024, 6, 1, 0, 0, 0, 0, ZoneOffset.UTC)
        val result = sliderPositionToIsoTime(24f, fakeNow)
        assertEquals("2024-06-02T00:00:00Z", result)
    }

    @Test
    fun `sliderPosition 240 is the maximum allowed value`() {
        val fakeNow = OffsetDateTime.of(2024, 6, 1, 0, 0, 0, 0, ZoneOffset.UTC)
        val result = sliderPositionToIsoTime(240f, fakeNow)
        assertEquals("2024-06-11T00:00:00Z", result)
    }

    @Test
    fun `sliderPosition strips minutes and seconds from base time`() {
        val fakeNow = OffsetDateTime.of(2024, 6, 1, 10, 59, 59, 999_000_000, ZoneOffset.UTC)
        val result = sliderPositionToIsoTime(0f, fakeNow)
        // Minutes, seconds, nanos should all be zeroed
        assertEquals("2024-06-01T10:00:00Z", result)
    }

    // -------------------------------------------------------------------------
    // 2. stripLayerSuffix
    //    Source: selectedOptionText calculation in the dropdown
    //
    //    Logic:
    //      layer.title
    //        .removeSuffix(" in MEPS VDIV")
    //        .removeSuffix(" in Arctic VDIV")
    //        .removeSuffix(" in ECMWF SFC")
    //        .trim()
    // -------------------------------------------------------------------------

    private fun stripLayerSuffix(title: String): String =
        title
            .removeSuffix(" in MEPS VDIV")
            .removeSuffix(" in Arctic VDIV")
            .removeSuffix(" in ECMWF SFC")
            .trim()

    @Test
    fun `stripLayerSuffix removes MEPS suffix`() {
        assertEquals("Air temperature 2m", stripLayerSuffix("Air temperature 2m in MEPS VDIV"))
    }

    @Test
    fun `stripLayerSuffix removes Arctic suffix`() {
        assertEquals("Wind 10m speed", stripLayerSuffix("Wind 10m speed in Arctic VDIV"))
    }

    @Test
    fun `stripLayerSuffix removes ECMWF suffix`() {
        assertEquals("Precipitation amount 3h", stripLayerSuffix("Precipitation amount 3h in ECMWF SFC"))
    }

    @Test
    fun `stripLayerSuffix leaves title unchanged when no suffix matches`() {
        assertEquals("Air temperature 2m", stripLayerSuffix("Air temperature 2m"))
    }

    @Test
    fun `stripLayerSuffix trims surrounding whitespace`() {
        assertEquals("Wind 10m vector", stripLayerSuffix("  Wind 10m vector  "))
    }

    // -------------------------------------------------------------------------
    // 3. stripLayerSuffixByArea
    //    Source: updatedList mapping inside ExposedDropdownMenu
    //
    //    Logic:
    //      when (area) {
    //        AreaData.NORDEN -> title.removeSuffix(" in MEPS VDIV")
    //        AreaData.ARKTIS -> title.removeSuffix(" in Arctic VDIV")
    //        AreaData.VERDEN -> title.removeSuffix(" in ECMWF SFC")
    //        else            -> title
    //      }
    // -------------------------------------------------------------------------

    private fun stripLayerSuffixByArea(title: String, area: AreaData?): String =
        when (area) {
            AreaData.NORDEN -> title.removeSuffix(" in MEPS VDIV")
            AreaData.ARKTIS -> title.removeSuffix(" in Arctic VDIV")
            AreaData.VERDEN -> title.removeSuffix(" in ECMWF SFC")
            else -> title
        }

    @Test
    fun `stripLayerSuffixByArea NORDEN removes MEPS suffix`() {
        assertEquals(
            "Air temperature 2m",
            stripLayerSuffixByArea("Air temperature 2m in MEPS VDIV", AreaData.NORDEN)
        )
    }

    @Test
    fun `stripLayerSuffixByArea ARKTIS removes Arctic suffix`() {
        assertEquals(
            "Wind 10m speed",
            stripLayerSuffixByArea("Wind 10m speed in Arctic VDIV", AreaData.ARKTIS)
        )
    }

    @Test
    fun `stripLayerSuffixByArea VERDEN removes ECMWF suffix`() {
        assertEquals(
            "Precipitation amount 3h",
            stripLayerSuffixByArea("Precipitation amount 3h in ECMWF SFC", AreaData.VERDEN)
        )
    }

    @Test
    fun `stripLayerSuffixByArea null area returns title unchanged`() {
        val title = "Air temperature 2m in MEPS VDIV"
        assertEquals(title, stripLayerSuffixByArea(title, null))
    }

    // -------------------------------------------------------------------------
    // 4. getAllowedLayerTitles
    //    Source: allowedLayers val inside ExposedDropdownMenu
    //
    //    Logic:
    //      when (area) {
    //        AreaData.VERDEN -> setOf("Air temperature 2m", "Precipitation amount 3h",
    //                                "Wind 10m speed", "Wind 10m vector")
    //        else            -> setOf("Air temperature 2m", "Precipitation amount 1h",
    //                                "Wind 10m speed", "Wind 10m vector")
    //      }
    // -------------------------------------------------------------------------

    private fun getAllowedLayerTitles(area: AreaData?): Set<String> =
        when (area) {
            AreaData.VERDEN -> setOf(
                "Air temperature 2m",
                "Precipitation amount 3h",
                "Wind 10m speed",
                "Wind 10m vector"
            )
            else -> setOf(
                "Air temperature 2m",
                "Precipitation amount 1h",
                "Wind 10m speed",
                "Wind 10m vector"
            )
        }

    @Test
    fun `getAllowedLayerTitles VERDEN contains 3h precipitation`() {
        val titles = getAllowedLayerTitles(AreaData.VERDEN)
        assertTrue(titles.contains("Precipitation amount 3h"))
        assertFalse(titles.contains("Precipitation amount 1h"))
    }

    @Test
    fun `getAllowedLayerTitles NORDEN contains 1h precipitation`() {
        val titles = getAllowedLayerTitles(AreaData.NORDEN)
        assertTrue(titles.contains("Precipitation amount 1h"))
        assertFalse(titles.contains("Precipitation amount 3h"))
    }

    @Test
    fun `getAllowedLayerTitles null area falls back to 1h set`() {
        val titles = getAllowedLayerTitles(null)
        assertTrue(titles.contains("Precipitation amount 1h"))
    }

    @Test
    fun `getAllowedLayerTitles always contains temperature and wind layers`() {
        listOf(AreaData.VERDEN, AreaData.NORDEN, AreaData.ARKTIS, null).forEach { area ->
            val titles = getAllowedLayerTitles(area)
            assertTrue("$area missing temperature", titles.contains("Air temperature 2m"))
            assertTrue("$area missing wind speed", titles.contains("Wind 10m speed"))
            assertTrue("$area missing wind vector", titles.contains("Wind 10m vector"))
        }
    }

    @Test
    fun `getAllowedLayerTitles returns exactly 4 entries for every area`() {
        listOf(AreaData.VERDEN, AreaData.NORDEN, AreaData.ARKTIS, null).forEach { area ->
            assertEquals("$area should have 4 layers", 4, getAllowedLayerTitles(area).size)
        }
    }

    // -------------------------------------------------------------------------
    // 5. translateLayerTitle
    //    Source: nyTitle calculation in the dropdown forEach
    //
    //    Logic:
    //      when (title) {
    //        "Air temperature 2m"      -> "Temperature"
    //        "Precipitation amount 1h" -> "Rainfall"
    //        "Precipitation amount 3h" -> "Rainfall"
    //        "Wind 10m speed"          -> "Wind speed"
    //        "Wind 10m vector"         -> "Wind direction"
    //        else                      -> error(...)
    //      }
    // -------------------------------------------------------------------------

    private fun translateLayerTitle(title: String): String =
        when (title) {
            "Air temperature 2m"      -> "Temperature"
            "Precipitation amount 1h" -> "Rainfall"
            "Precipitation amount 3h" -> "Rainfall"
            "Wind 10m speed"          -> "Wind speed"
            "Wind 10m vector"         -> "Wind direction"
            else -> error("Unexpected layer title: $title")
        }

    @Test
    fun `translateLayerTitle maps temperature correctly`() {
        assertEquals("Temperature", translateLayerTitle("Air temperature 2m"))
    }

    @Test
    fun `translateLayerTitle maps 1h precipitation to Rainfall`() {
        assertEquals("Rainfall", translateLayerTitle("Precipitation amount 1h"))
    }

    @Test
    fun `translateLayerTitle maps 3h precipitation to Rainfall`() {
        assertEquals("Rainfall", translateLayerTitle("Precipitation amount 3h"))
    }

    @Test
    fun `translateLayerTitle maps wind speed correctly`() {
        assertEquals("Wind speed", translateLayerTitle("Wind 10m speed"))
    }

    @Test
    fun `translateLayerTitle maps wind vector to Wind direction`() {
        assertEquals("Wind direction", translateLayerTitle("Wind 10m vector"))
    }

    @Test
    fun `translateLayerTitle throws for unknown title`() {
        assertThrows(IllegalStateException::class.java) {
            translateLayerTitle("Some unknown layer")
        }
    }

    // -------------------------------------------------------------------------
    // 6. alertColorToArgbHex
    //    Source: color val inside drawAlerts → addPolygonToFolder
    //
    //    Logic:
    //      val hex = when (riskMatrixColor) {
    //        "Yellow" -> "FFFF00"
    //        "Orange" -> "FFA500"
    //        "Red"    -> "FF0000"
    //        else     -> "FFFFFF"
    //      }
    //      return "#80$hex"   // 50 % alpha prefix
    // -------------------------------------------------------------------------

    private fun alertColorToArgbHex(riskMatrixColor: String?): String {
        val hex = when (riskMatrixColor) {
            "Yellow" -> "FFFF00"
            "Orange" -> "FFA500"
            "Red"    -> "FF0000"
            else     -> "FFFFFF"
        }
        return "#80$hex"
    }

    @Test
    fun `alertColorToArgbHex Yellow maps to semi-transparent yellow`() {
        assertEquals("#80FFFF00", alertColorToArgbHex("Yellow"))
    }

    @Test
    fun `alertColorToArgbHex Orange maps to semi-transparent orange`() {
        assertEquals("#80FFA500", alertColorToArgbHex("Orange"))
    }

    @Test
    fun `alertColorToArgbHex Red maps to semi-transparent red`() {
        assertEquals("#80FF0000", alertColorToArgbHex("Red"))
    }

    @Test
    fun `alertColorToArgbHex unknown color falls back to semi-transparent white`() {
        assertEquals("#80FFFFFF", alertColorToArgbHex("Green"))
    }

    @Test
    fun `alertColorToArgbHex null falls back to semi-transparent white`() {
        assertEquals("#80FFFFFF", alertColorToArgbHex(null))
    }

    // -------------------------------------------------------------------------
    // 7. buildWmsBbox
    //    Source: bbox calculation inside getTileURLString in updateWmsLayer
    //
    //    Logic (OSM tile → CRS:84 bounding box):
    //      val n    = 2^zoom
    //      lonMin   = x / n * 360 - 180
    //      lonMax   = (x+1) / n * 360 - 180
    //      latMin   = toDegrees(atan(sinh(π * (1 - 2*(y+1)/n))))
    //      latMax   = toDegrees(atan(sinh(π * (1 - 2*y/n))))
    //      return   "$lonMin,$latMin,$lonMax,$latMax"
    // -------------------------------------------------------------------------

    private fun buildWmsBbox(zoom: Int, x: Int, y: Int): String {
        val n = Math.pow(2.0, zoom.toDouble())
        val lonMin = x / n * 360.0 - 180.0
        val lonMax = (x + 1) / n * 360.0 - 180.0
        val latMin = Math.toDegrees(Math.atan(Math.sinh(Math.PI * (1 - 2 * (y + 1) / n))))
        val latMax = Math.toDegrees(Math.atan(Math.sinh(Math.PI * (1 - 2 * y / n))))
        return "$lonMin,$latMin,$lonMax,$latMax"
    }

    @Test
    fun `buildWmsBbox zoom 0 tile 0-0 covers the whole world`() {
        val bbox = buildWmsBbox(0, 0, 0)
        val parts = bbox.split(",").map { it.toDouble() }
        assertEquals(4, parts.size)
        assertEquals(-180.0, parts[0], 0.001)   // lonMin
        assertTrue("latMin should be large negative", parts[1] < -80.0)
        assertEquals(180.0, parts[2], 0.001)    // lonMax
        assertTrue("latMax should be large positive", parts[3] > 80.0)
    }

    @Test
    fun `buildWmsBbox produces 4 comma-separated values`() {
        val bbox = buildWmsBbox(5, 10, 12)
        assertEquals(4, bbox.split(",").size)
    }

    @Test
    fun `buildWmsBbox lonMin is always less than lonMax`() {
        listOf(1 to 0, 5 to 10, 10 to 500).forEach { (zoom, x) ->
            val parts = buildWmsBbox(zoom, x, 0).split(",").map { it.toDouble() }
            assertTrue("lonMin >= lonMax at zoom=$zoom x=$x", parts[0] < parts[2])
        }
    }

    @Test
    fun `buildWmsBbox latMin is always less than latMax`() {
        val parts = buildWmsBbox(5, 10, 12).split(",").map { it.toDouble() }
        assertTrue("latMin should be less than latMax", parts[1] < parts[3])
    }

    @Test
    fun `buildWmsBbox known tile zoom 1 x 0 y 0 expected longitude range`() {
        // Tile (1, 0, 0) should be the western hemisphere top
        val parts = buildWmsBbox(1, 0, 0).split(",").map { it.toDouble() }
        assertEquals(-180.0, parts[0], 0.001)
        assertEquals(0.0, parts[2], 0.001)
    }

    // -------------------------------------------------------------------------
    // 8. buildWmsUrl  (integration-style pure function test)
    //    Source: url StringBuilder inside getTileURLString
    //
    //    Verifies that the URL contains the mandatory parameters and
    //    that TIME is included only when selectedTime is not empty.
    // -------------------------------------------------------------------------

    private fun buildWmsUrl(
        layerName: String,
        modelParam: String,
        bbox: String,
        selectedTime: String?
    ): String {
        val url = StringBuilder("https://public-victoria.met.no/wms?")
        url.append("SERVICE=WMS")
        url.append("&VERSION=1.3.0")
        url.append("&REQUEST=GetMap")
        url.append("&LAYERS=$layerName")
        url.append("&STYLES=")
        url.append("&CRS=CRS:84")
        url.append("&BBOX=$bbox")
        url.append("&WIDTH=256")
        url.append("&HEIGHT=256")
        url.append("&FORMAT=image/png")
        url.append("&TRANSPARENT=TRUE")
        url.append("&model=$modelParam")
        if (!selectedTime.isNullOrEmpty()) {
            url.append("&TIME=$selectedTime")
        }
        return url.toString()
    }

    @Test
    fun `buildWmsUrl contains required WMS parameters`() {
        val url = buildWmsUrl("temp_layer", "meps", "-10.0,-5.0,10.0,5.0", null)
        assertTrue(url.contains("SERVICE=WMS"))
        assertTrue(url.contains("VERSION=1.3.0"))
        assertTrue(url.contains("REQUEST=GetMap"))
        assertTrue(url.contains("WIDTH=256"))
        assertTrue(url.contains("HEIGHT=256"))
        assertTrue(url.contains("FORMAT=image/png"))
        assertTrue(url.contains("TRANSPARENT=TRUE"))
        assertTrue(url.contains("CRS=CRS:84"))
    }

    @Test
    fun `buildWmsUrl includes layer name and model`() {
        val url = buildWmsUrl("air_temp_2m", "ec", "0,0,1,1", null)
        assertTrue(url.contains("LAYERS=air_temp_2m"))
        assertTrue(url.contains("model=ec"))
    }

    @Test
    fun `buildWmsUrl appends TIME when selectedTime is non-empty`() {
        val time = "2024-06-01T12:00:00Z"
        val url = buildWmsUrl("wind_10m", "meps", "0,0,1,1", time)
        assertTrue(url.contains("TIME=$time"))
    }

    @Test
    fun `buildWmsUrl omits TIME parameter when selectedTime is null`() {
        val url = buildWmsUrl("wind_10m", "meps", "0,0,1,1", null)
        assertFalse(url.contains("TIME="))
    }

    @Test
    fun `buildWmsUrl omits TIME parameter when selectedTime is empty`() {
        val url = buildWmsUrl("wind_10m", "meps", "0,0,1,1", "")
        assertFalse(url.contains("TIME="))
    }

    @Test
    fun `buildWmsUrl starts with correct base URL`() {
        val url = buildWmsUrl("layer", "meps", "0,0,1,1", null)
        assertTrue(url.startsWith("https://public-victoria.met.no/wms?"))
    }
}