package com.svoboden.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class HabitType(val displayName: String, val defaultUnit: String) {
    SMOKING("Курение", "сигарет"),
    ALCOHOL("Алкоголь", "дней без алкоголя"),
    DRUGS("Наркотики", "дней без употребления"),
    OVEREATING("Переедание", "дней без срыва"),
    GAMBLING("Азартные игры", "дней без игры"),
    OTHER("Другое", "дней")
}
