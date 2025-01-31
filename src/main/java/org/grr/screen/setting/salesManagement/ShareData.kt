package org.grr.screen.setting.salesManagement

import org.grr.widgets.RoundedButton
import javax.swing.JLabel
import javax.swing.table.DefaultTableModel

object ShareData {
    lateinit var tableModel: DefaultTableModel
    lateinit var selectedDateLabel: JLabel
    lateinit var datePickerButton: RoundedButton
}