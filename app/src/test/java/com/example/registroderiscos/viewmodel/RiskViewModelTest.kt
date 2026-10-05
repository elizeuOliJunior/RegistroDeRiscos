package com.example.registroderiscos.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.GregorianCalendar

class RiskViewModelTest {
    @Test
    fun formatRiskDate_zeroPadsMonthAndDay() {
        val calendar = GregorianCalendar(2025, Calendar.JANUARY, 2)

        assertEquals("2025-01-02", formatRiskDate(calendar))
    }
}
