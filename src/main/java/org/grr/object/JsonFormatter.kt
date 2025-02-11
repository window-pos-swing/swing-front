package org.grr.`object`

import org.json.JSONObject
import java.text.NumberFormat
import java.util.*

object JsonFormatter {
    //  숫자를 천 단위 콤마가 있는 문자열로 변환
    fun formatNumber(number: Long): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale.US)
        return numberFormat.format(number)
    }
}