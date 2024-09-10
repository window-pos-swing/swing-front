package org.example.observer

import org.example.model.Order

interface OrderObserver {
    fun update(order: Order)
}
