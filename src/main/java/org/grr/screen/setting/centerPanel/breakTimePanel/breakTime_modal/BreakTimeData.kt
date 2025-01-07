package org.grr.screen.setting.centerPanel.breakTimePanel.breakTime_modal

data class BreakTimeData (
    val labelText: String,  // 요일 정보 (e.g., "전체요일", "평일", "월")
    val timeRangeText: String  // 시간 범위 정보 (e.g., "09:00 ~ 18:00")
)