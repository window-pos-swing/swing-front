package org.example.style

import java.awt.Color

object MyColor {
    val GREY100 = Color(240, 240, 240)
    val GREY200 = Color(220, 220, 220)  // 밝은 회색 (필요시 추가)
    val GREY300 = Color(217,217,217)
    val GREY400 = Color(204,204,204)
    val GREY500 = Color(147, 147, 147)
    val GREY600 = Color(120,120,120)
    val GREY900 = Color(51,51,51)

    val DARK_NAVY = Color(27, 43, 66)
    val LOGIN_TITLEBAR = Color(65, 79, 98 )
    val DARK_RED = Color(209, 12, 29)
    val DIVISION_PINK = Color(227,101,101)
    val PINK = Color(255,185,185)

    val Yellow = Color(244,172,0)

    // 선택/비선택 상태 색상
    val SELECTED_TEXT_COLOR = Color.WHITE
    val UNSELECTED_TEXT_COLOR = GREY500   // 명확하게 GREY500과 연결
    val SELECTED_BACKGROUND_COLOR = DARK_NAVY
    val REJECT_SELECTED_BACKGROUND_COLOR = DARK_RED
    val UNSELECTED_BACKGROUND_COLOR = GREY200

    // 테두리 색상
    val BORDER_GRAY = GREY300  // 중간 회색을 테두리 색상으로 사용

}