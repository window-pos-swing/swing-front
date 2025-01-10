package org.grr.`object`

import org.grr.screen.main.MainForm

object FormManager {
    private var mainForm: MainForm? = null

    fun getMainForm(): MainForm {
        if (mainForm == null) {
            mainForm = MainForm()
        }
        return mainForm!!
    }

    fun hideMainForm() {
        mainForm?.isVisible = false
    }

    fun showMainForm() {
        getMainForm().isVisible = true
    }
}

