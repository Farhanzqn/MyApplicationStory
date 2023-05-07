package com.zidan.myapplicationstory.data

import androidx.test.espresso.idling.CountingIdlingResource

object EspressoResource {

    private const val RESOURCE = "GLOBAL"

    @JvmField
    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}

inline fun <T> wrapEspressoIdlingResource(function: () -> T): T {
    EspressoResource.increment() // Set app as busy.
    return try {
        function()
    } finally {
        EspressoResource.decrement() // Set app as idle.
    }
}