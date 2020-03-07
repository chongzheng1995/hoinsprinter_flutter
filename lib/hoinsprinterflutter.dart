import 'dart:async';

import 'package:flutter/services.dart';

class HoinsPrinter {
  static const MethodChannel _channel =
      const MethodChannel('hoinsprinterflutter');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<Null> startPrinter() async {
    await _channel.invokeMethod('startPrinter');
    return null;
  }

  static Future<PrinterStatus> connectPrinter(String sPrinterIP) async {
    Map<String, dynamic> args = <String, dynamic>{};
    args.putIfAbsent("sPrinterIP", () => sPrinterIP);
    List<dynamic> sResult = await _channel.invokeMethod('connectPrinter', args);

    return PrinterStatus(iStatus: int.parse(sResult[0]), sMessage: sResult[1]);
  }

  static Future<PrinterStatus> printText(String sMessage) async {
    Map<String, dynamic> args = <String, dynamic>{};
    args.putIfAbsent("sMessage", () => sMessage);
    List<dynamic> sResult = await _channel.invokeMethod('printText', args);

    return PrinterStatus(iStatus: int.parse(sResult[0]), sMessage: sResult[1]);
  }

  static Future<PrinterStatus> printCategoryReceipt(
      String sCategory, String sTable, String sMessage) async {
    Map<String, dynamic> args = <String, dynamic>{};
    args.putIfAbsent("sCategory", () => sCategory);
    args.putIfAbsent("sTable", () => sTable);
    args.putIfAbsent("sMessage", () => sMessage);
    List<dynamic> sResult =
    await _channel.invokeMethod('printCategoryReceipt', args);

    return PrinterStatus(iStatus: int.parse(sResult[0]), sMessage: sResult[1]);
  }

  static Future<PrinterStatus> printPaymentReceipt(
      String sReceiptNo,
      String sContent,
      String sTableNo,
      String sCashier,
      String sDateTime,
      ) async {
    Map<String, dynamic> args = <String, dynamic>{};
    args.putIfAbsent("sReceiptNo", () => sReceiptNo);
    args.putIfAbsent("sContent", () => sContent);
    args.putIfAbsent("sTableNo", () => sTableNo);
    args.putIfAbsent("sCashier", () => sCashier);
    args.putIfAbsent("sDateTime", () => sDateTime);
    List<dynamic> sResult = await _channel
        .invokeMethod('printPaymentReceipt', args)
        .catchError((e) {
      return ["0", e];
    });

    return PrinterStatus(iStatus: int.parse(sResult[0]), sMessage: sResult[1]);
  }

  static Future<Null> openCashDrawer() async {
    await _channel.invokeMethod('openCashDrawer');
    return null;
  }

  static Future<Null> disconnectWifi() async {
    List<dynamic> sResult = await _channel.invokeMethod('disconnectWifi');
    return null;
  }
}

class PrinterStatus {
  int iStatus;
  String sMessage = "";

  PrinterStatus({this.iStatus, this.sMessage});
}
