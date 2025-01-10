package org.grr.enum

enum class ServerOrderStatus {
    REQUEST,
    ACCEPT,
    COOKING,
    COOKED,
    PICKUP_COMPLETE,
    DELIVERY,
    DELIVERY_COMPLETE,
    STORE_CANCEL,
    USER_CANCEL,
}