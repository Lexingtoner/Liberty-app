package com.svoboden.app.domain.model

data class TriggerTemplate(
    val id: Long = 0,
    val label: String,
    val isCustom: Boolean = false
) {
    companion object {
        /** Предзаполненные триггеры для диалога срыва. */
        fun defaults(): List<TriggerTemplate> = listOf(
            TriggerTemplate(1L, "Стресс / тревога"),
            TriggerTemplate(2L, "Компания / давление"),
            TriggerTemplate(3L, "Скука"),
            TriggerTemplate(4L, "Грусть / одиночество"),
            TriggerTemplate(5L, "Алкоголь рядом"),
            TriggerTemplate(6L, "Усталость"),
            TriggerTemplate(7L, "Другое")
        )
    }
}
