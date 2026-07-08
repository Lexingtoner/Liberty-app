package com.svoboden.app.domain.provider

import javax.inject.Inject
import javax.inject.Singleton
import java.util.Calendar

data class Quote(val text: String, val author: String)

@Singleton
class MotivationProvider @Inject constructor() {
    private val quotes = listOf(
        Quote("Секрет того, чтобы вырваться вперед, в том, чтобы начать.", "Марк Твен"),
        Quote("Дисциплина — это выбор между тем, что вы хотите сейчас, и тем, что вы хотите больше всего.", "Авраам Линкольн"),
        Quote("Ваше будущее создается тем, что вы делаете сегодня, а не завтра.", "Роберт Кийосаки"),
        Quote("Неважно, как медленно вы идете, пока вы не остановитесь.", "Конфуций"),
        Quote("Успех — это не окончание, неудача — это не фатальность: значение имеет мужество продолжать.", "Уинстон Черчилль"),
        Quote("Тяга — это просто чувство, а чувства всегда проходят.", "Неизвестный"),
        Quote("Вы сильнее, чем ваша зависимость.", "Неизвестный")
    )

    fun getDailyQuote(): Quote {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        return quotes[dayOfYear % quotes.size]
    }
}
