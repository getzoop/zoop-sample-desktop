import zoop.android.desktop.demo.AndroidApp


typealias Context = android.content.Context

actual val initialContext: Context
    get() {
        return AndroidApp.context
    }