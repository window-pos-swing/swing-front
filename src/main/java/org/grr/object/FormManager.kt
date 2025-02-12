package org.grr.`object`

import org.grr.screen.main.MainForm
import org.grr.screen.select_store.SelectStoreForm

object FormManager {
    private var mainForm: MainForm? = null
    private var selectStoreForm: SelectStoreForm? = null
    var isLogout: Boolean = false

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

//    fun getSelectStoreForm(): SelectStoreForm {
//        if (selectStoreForm == null) {
//            selectStoreForm = SelectStoreForm()
//        }
//    }
}

