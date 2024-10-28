package zoop.android.desktop.demo

import android.app.Application


class AndroidApp : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        val context = MainActivity()
        const val ACTION_USB_PERMISSION: String = "com.android.example.USB_PERMISSION"
    }
}