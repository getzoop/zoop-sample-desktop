package zoop.android.desktop.demo

import App
import MainViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
//import com.zoop.pos.desktop.usb.SerialPort


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            //SerialPort.setContext(this)
            App(context = applicationContext, MainViewModel())
        }
    }

}