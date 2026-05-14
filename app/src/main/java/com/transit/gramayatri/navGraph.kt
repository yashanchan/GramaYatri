package com.transit.gramayatri

object Destinations {
    const val ACCOUNT_CREATION = "account_creation"
    const val HOME             = "home"
    const val BUS_TIMELINE     = "bus_timeline/{busId}"
    const val EDIT_PROFILE     = "edit_profile"
    const val SAVED_PREFS      = "saved_preferences"
    const val SETTINGS         = "settings"

    fun busTimeline(busId: String) = "bus_timeline/$busId"
}