package org.grr.screen.setting.centerPanel.breakTimePanel.breakTime_modal

import org.grr.widgets.RoundButton
import org.grr.widgets.RoundButton2

object ShareButton {
    val dayButtons: MutableList<RoundButton> = mutableListOf()
    val dayButtons2: MutableList<RoundButton2> = mutableListOf()

    val selectedDays = mutableSetOf<String>()
    val selectedDay2 = mutableSetOf<String>()
}