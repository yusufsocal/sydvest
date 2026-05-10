package no.uio.ifi.in2000.dylansc.team6project.data.weatherdata

/**
 * Weather model areas supported by the WMS service.
 *
 * @property area Model identifier sent as the `model` query parameter.
 */
enum class AreaData(val area: String) {
    NORDIC("meps_det_vdiv_2_5km_calculations"),
    ARCTIC("arome_arctic_det_vdiv_2_5km_calculations"),
    WORLD("ec_sfc_3h_calculations")

}