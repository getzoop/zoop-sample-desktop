package util

import com.zoop.pos.Zoop
import com.zoop.pos.desktop.DesktopPlugin
import com.zoop.pos.plugin.DashboardConfirmationResponse
import com.zoop.pos.type.Context
import com.zoop.pos.type.Environment
import com.zoop.pos.type.LogLevel

class DesktopPluginManager(private val credentials: DashboardConfirmationResponse.Credentials? = null) {

    fun initialize(context: Context) {
        Zoop.initialize(context) {
            if (credentials != null) {
                credentials {
                    marketplace = credentials.marketplace
                    seller= credentials.seller
                    accessKey= credentials.accessKey
                }
            }
        }
        Zoop.setEnvironment(Environment.Production)
        Zoop.setLogLevel(LogLevel.Trace)
        Zoop.setStrict(false)
        Zoop.setTimeout(15 * 1000L)
        try {
            Zoop.plug(DesktopPlugin(Zoop.constructorParameters()))
        } catch (ex: Exception) {
            println("Failed to initialize the desktop plugin. Reason: ${ex.message}")
        }
    }
}