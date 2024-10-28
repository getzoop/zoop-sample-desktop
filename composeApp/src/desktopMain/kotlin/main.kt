import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.zoop.pos.type.Context

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "DesktopPluginDemoApp",
    ) {
        App(context = initialContext, MainViewModel())
    }
}