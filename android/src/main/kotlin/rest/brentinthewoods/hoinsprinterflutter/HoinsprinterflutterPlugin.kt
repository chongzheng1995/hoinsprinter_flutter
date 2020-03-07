package rest.brentinthewoods.hoinsprinterflutter

import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

import android.content.Context
import com.example.hoinprinterlib.HoinPrinter
import com.example.hoinprinterlib.module.PrinterCallback
import com.example.hoinprinterlib.module.PrinterEvent
import com.example.hoinprinterlib.common.Constant.BT_STATE_CONNECTED
import com.example.hoinprinterlib.common.Constant.BT_STATE_CONNECTING
import com.example.hoinprinterlib.common.Constant.BT_STATE_DISCONNECTED
import com.example.hoinprinterlib.common.Constant.BT_STATE_LISTEN
import com.example.hoinprinterlib.common.Constant.EVENT_WIFI_RECEIVE_DATA
import com.example.hoinprinterlib.common.Constant.WIFI_STATE_CONNECTED
import com.example.hoinprinterlib.common.Constant.WIFI_STATE_DISCONNECTED
import com.example.hoinprinterlib.common.Constant.USB_STATE_CONNECTED
import com.example.hoinprinterlib.common.Constant.USB_STATE_DISCONNECTED
import com.example.hoinprinterlib.common.ErrorCode.CONTEXT_ERROR
import com.example.hoinprinterlib.common.ErrorCode.DEVICE_NOT_CONNECTED
import com.example.hoinprinterlib.common.ErrorCode.NULL_POINTER_EXCEPTION
import com.example.hoinprinterlib.common.ErrorCode.WIFI_CONNECT_ERROR
import com.example.hoinprinterlib.common.ErrorCode.WIFI_SEND_FAILED

/** HoinsprinterflutterPlugin */
public class HoinsprinterflutterPlugin: FlutterPlugin, MethodCallHandler {

  private var applicationContext : Context? = null
  private var mHoinPrinter: HoinPrinter? = null
  private var mHPCallback: HoinPrinterCallback? = null

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    val channel = MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "hoinsprinterflutter")
    channel.setMethodCallHandler(HoinsprinterflutterPlugin());
  }

  // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.
  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "hoinsprinterflutter")
      channel.setMethodCallHandler(HoinsprinterflutterPlugin())
    }
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when(call.method){
      "getPlatformVersion" -> {
        result.success("Android ${android.os.Build.VERSION.RELEASE}")
      }
      "startPrinter" -> {

        try {
          mHPCallback = HoinPrinterCallback()
          mHoinPrinter = HoinPrinter.getInstance(applicationContext, 0, mHPCallback)
          result.success("Started Plugin")
        } catch (e : Exception) {
          result.error("",e.message, "")
        }

      }
      "connectPrinter" -> {
        val sPrinterIP : String = call.argument<String>("sPrinterIP").toString()

        try {
          mHPCallback!!.setCurrentResult(result)
          mHoinPrinter!!.connect(sPrinterIP)
        } catch (e : Exception) {
          result.error("", e.message, "")
        }
      }
      "printText" -> {
        val sMessage : String = call.argument<String>("sMessage").toString()

        try {
          mHoinPrinter!!.switchType(true)
          mHoinPrinter!!.printText(sMessage, false, false, true, false)
        } catch (e : Exception) {
          result.error("", e.message, "")
        }
      }
      "printCategoryReceipt" -> {
        val sCategory : String = call.argument<String>("sCategory").toString()
        val sMessage : String = call.argument<String>("sMessage").toString()
        val sTable : String = call.argument<String>("sTable").toString()

        try {
          /*mHPCallback!!.setCurrentResult(result)*/
          mHoinPrinter!!.switchType(true)
          mHoinPrinter!!.printText(sTable, true, true, true, true)
          mHoinPrinter!!.printText(sMessage, true, true, false, false)

          val valMutableList =  mutableListOf("2", "Print Successful")
          result.success(valMutableList)

        } catch (e : Exception) {
          result.error("", e.message, "")
        }
      }
      "printPaymentReceipt" -> {
        val sContent : String = call.argument<String>("sContent").toString()
        val sReceiptNo : String = call.argument<String>("sReceiptNo").toString()
        val sTableNo : String = call.argument<String>("sTableNo").toString()
        val sCashier : String = call.argument<String>("sCashier").toString()
        val sDateTime : String = call.argument<String>("sDateTime").toString()

        try {
          mHoinPrinter!!.switchType(true)
          mHoinPrinter!!.printText("Restaurant Teoh Heng Kee", false, false, false, true)
          mHoinPrinter!!.printText("(002754758-X)", false, false, false, true)
          mHoinPrinter!!.printText("25, Jalan Dato Haji Harun, Taman", false, false, false, true)
          mHoinPrinter!!.printText("Taynton View, 56000 Kuala Lumpur", false, false, false, true)
          mHoinPrinter!!.printText("Wilayah Persekutuan\n\n", false, false, false, true)
          mHoinPrinter!!.printText("Receipt No: " + sReceiptNo, false, false, false, false)
          mHoinPrinter!!.printText("Table No: " + sTableNo, false, false, false, false)
          mHoinPrinter!!.printText("Date/Time: " + sDateTime, false, false, false, false)
          mHoinPrinter!!.printText("Cashier: " + sCashier, false, false, false, false)
          mHoinPrinter!!.printText(sContent, false, false, false, false)

          val valMutableList =  mutableListOf("2", "Print Successful")
          result.success(valMutableList)

        } catch (e : Exception) {
          result.error("", e.message, "")
        }
      }
      "openCashDrawer" -> {
        try {
          mHoinPrinter!!.openBox()
          val valMutableList =  mutableListOf("2", "Open Successful")
          result.success(valMutableList)

        } catch (e : Exception) {
          result.error("", e.message, "")
        }
      }
      "disconnectWifi" -> {
        try {
          mHPCallback!!.setCurrentResult(null)
          mHoinPrinter!!.destroy()
          val valMutableList =  mutableListOf("2", "Close Successful")
          result.success(valMutableList)
        } catch (e : Exception) {
          result.error("", e.message, "")
        }
      }
      else -> {
        result.notImplemented()
      }
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
  }
}


class HoinPrinterCallback : PrinterCallback {

  private var tResult: Result? = null

  fun setCurrentResult(res : Result?){
    tResult = res
  }

  override fun onState(i: Int) {
    var valMutableList = mutableListOf("","")

    when(i){
      BT_STATE_DISCONNECTED -> {
        valMutableList =  mutableListOf("0", "Bluetooth disconnected")
      }
      BT_STATE_CONNECTING -> {
        valMutableList =  mutableListOf("1", "Bluetooth connecting")
      }
      BT_STATE_LISTEN -> {
        valMutableList =  mutableListOf("1", "Bluetooth listening")
      }
      BT_STATE_CONNECTED -> {
        valMutableList =  mutableListOf("2", "Bluetooth connected")
      }
      WIFI_STATE_CONNECTED -> {
        valMutableList =  mutableListOf("2", "Wifi connected")
      }
      WIFI_STATE_DISCONNECTED -> {
        valMutableList =  mutableListOf("0", "Wifi disconnected")
      }
      USB_STATE_CONNECTED -> {
        valMutableList =  mutableListOf("2", "Usb connected")
      }
      USB_STATE_DISCONNECTED -> {
        valMutableList =  mutableListOf("0", "Usb disconnected")
      }
    }

    if (tResult != null && valMutableList[0] != "") {
      tResult!!.success(valMutableList)
      tResult = null
    }
  }

  override fun onError(i: Int) {
    var valMutableList = mutableListOf("","")

    when(i){
      CONTEXT_ERROR -> {
        valMutableList =  mutableListOf("0", "Context error")
      }
      WIFI_SEND_FAILED -> {
        valMutableList =  mutableListOf("0", "Fail Send Wifi")
      }
      WIFI_CONNECT_ERROR -> {
        valMutableList =  mutableListOf("0", "Fail Connect Wifi")
      }
      DEVICE_NOT_CONNECTED -> {
        valMutableList =  mutableListOf("0", "Device not connected")
      }
      NULL_POINTER_EXCEPTION -> {
        valMutableList =  mutableListOf("0", "Null Pointer Exception")
      }
    }

    if (tResult != null && valMutableList[0] != "") {
      tResult!!.success(valMutableList)
      tResult = null
    }
  }

  override fun onEvent(printerEvent: PrinterEvent) {
    var valMutableList = mutableListOf("","")

    when(printerEvent.event){
      EVENT_WIFI_RECEIVE_DATA -> {
        valMutableList =  mutableListOf("2", "Wifi Receive Data")
      }
    }

    if (tResult != null && valMutableList[0] != "") {
      tResult!!.success(valMutableList)
      tResult = null
    }
  }
}

/*
class PrinterResultStreamHandler(): EventChannel.StreamHandler {
    var _eventSink: EventChannel.EventSink? = null

    override fun onListen(p0: Any?, p1: EventChannel.EventSink?) {
        this._eventSink = p1
    }

    override fun onCancel(p0: Any?) {
        this._eventSink = null
    }

    // This method is called by the printer listener as mentioned above
    override fun speechController(text: String) {
        this._eventSink?.success(text)
    }
}
*/

