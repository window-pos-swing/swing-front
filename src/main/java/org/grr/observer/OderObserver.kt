package org.grr.observer

import org.grr.model.ReceiveOrderModel

interface OrderObserver {
    fun update(order: ReceiveOrderModel)
}
