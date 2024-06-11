package com.example.ventilrechneropel

import android.app.Application
import android.content.Context

class MyApp() : Application() {
    init {
        instance = this
    }

    companion object {

        @JvmStatic
        private var instance: MyApp? = null

        @JvmStatic
        fun getContext(): Context {
            if (instance != null) {
                return instance!!.applicationContext
            }
            throw NullPointerException("Class was not instantiated once?!")
        }

        /**
         * Returns [Boolean] true if this function is called on device (not local).
         * That is, an app context is available and present.
         *
         * @return Returns true if context is present, else false.
         */
        fun hasAppContext(): Boolean {
            return getContext() != null
        }

        fun getApplicationDirectory(): String {
            return getContext().filesDir.absolutePath
        }

    }
}