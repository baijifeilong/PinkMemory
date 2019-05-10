package io.github.baijifeilong.pinkmemory

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.*

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity(), AnkoLogger {

    private lateinit var switch: Switch

    private fun fetchIp(): String {
        val cmd = "ip route get 1.2.3.4"
        return Scanner(Runtime.getRuntime().exec(cmd).inputStream).useDelimiter("\\A").next().split("\\s+".toRegex())[6]
    }

    private fun fetchAdbPort(): String? {
        val cmd = "getprop service.adb.tcp.port"
        val port = try {
            Scanner(Runtime.getRuntime().exec(cmd).inputStream).useDelimiter("\\A").next().trim()
        } catch (e: NoSuchElementException) {
            null
        }
        return if ("-1" == port) null else port
    }

    private fun startAdb() {
        val runtime = Runtime.getRuntime().exec("su")
        runtime.outputStream.write("setprop service.adb.tcp.port 5555\n".toByteArray())
        runtime.outputStream.write("start adbd\n".toByteArray())
        runtime.outputStream.write("exit\n".toByteArray())
        runtime.outputStream.flush()
        val scanner = Scanner(runtime.inputStream).useDelimiter("\\A");
        val output = if (scanner.hasNext()) scanner.next() else null
        toast("ADB已启用, 控制台输出: $output")
    }

    private fun stopAdb() {
        val runtime = Runtime.getRuntime().exec("su")
        runtime.outputStream.write("setprop service.adb.tcp.port -1\n".toByteArray());
        runtime.outputStream.write("stop adbd\n".toByteArray());
        runtime.outputStream.write("exit\n".toByteArray())
        runtime.outputStream.flush()
        val scanner = Scanner(runtime.inputStream).useDelimiter("\\A");
        val output = if (scanner.hasNext()) scanner.next() else null
        toast("ADB已禁用, 控制台输出: $output")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        relativeLayout {
            switch {
                switch = this
                info { "CLICKED" }
                onClick {
                    switch.isEnabled = false
                    if (fetchAdbPort() == null) startAdb() else stopAdb()
                    switch.isEnabled = true
                    refreshLayout()
                }
            }.lparams {
                centerInParent()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshLayout()
    }

    private fun refreshLayout() {
        val port = fetchAdbPort()
        val ip = fetchIp()
        if (port == null) {
            switch.text = "已禁用"
            switch.isChecked = false
        } else {
            switch.text = "已启用($ip:$port)"
            switch.isChecked = true
        }
    }
}
