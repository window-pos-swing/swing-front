package org.grr.observer

import org.grr.model.Order

interface OrderObserver {
    fun update(order: Order)
}
