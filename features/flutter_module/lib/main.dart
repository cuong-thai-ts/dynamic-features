import 'package:flutter/material.dart';
import 'package:trust_vision_plugin/enums.dart';
import 'package:trust_vision_plugin/result/tv_card_type.dart';
import 'package:trust_vision_plugin/result/tv_detection_result.dart';
import 'package:trust_vision_plugin/result/tv_frame_batch.dart';
import 'package:trust_vision_plugin/trust_vision_plugin.dart';

final messangerKey = GlobalKey<ScaffoldMessengerState>();

void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        // This is the theme of your application.
        //
        // Try running your application with "flutter run". You'll see the
        // application has a blue toolbar. Then, without quitting the app, try
        // changing the primarySwatch below to Colors.green and then invoke
        // "hot reload" (press "r" in the console where you ran "flutter run",
        // or press Run > Flutter Hot Reload in a Flutter IDE). Notice that the
        // counter didn't reset back to zero; the application is not restarted.
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  // This widget is the home page of your application. It is stateful, meaning
  // that it has a State object (defined below) that contains fields that affect
  // how it looks.

  // This class is the configuration for the state. It holds the values (in this
  // case the title) provided by the parent (in this case the App widget) and
  // used by the build method of the State. Fields in a Widget subclass are
  // always marked "final".

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _counter = 0;

  void _initSDK() async {
    try {
      String jsonConfigurationByServer = """
      {
  \"card_types\": [
    {
      \"code\": \"vn.national_id\",
      \"name\": \"CMND cũ / CMND mới / CCCD / Hộ chiếu\",
      \"orientation\": \"horizontal\",
      \"has_back_side\": true,
      \"front_qr\": {
        \"exist\": true
      },
      \"back_qr\": {
        \"exist\": false
      }
    }
  ],
  \"country\": \"vn\",
  \"settings\": {
    \"sdk_settings\": {
      \"active_liveness_settings\": {
        \"show_gesture_arrow\": true,
        \"face_tracking_setting\": {
          \"android_terminate_threshold\": 0.002847,
          \"android_warning_threshold\": 0.001474,
          \"enable\": true,
          \"ios_terminate_threshold\": 0.003393,
          \"ios_warning_threshold\": 0.002176,
          \"limit_for\": \"all_flow\",
          \"max_interval_ms\": 2000,
          \"max_warning_time\": 5
        },
        \"flow_interval_time_ms\": 2000,
        \"limit_time_liveness_check\": {
          \"enable\": true,
          \"limit_time_second\": 20
        },
        \"record_video\": {
          \"enable\": true
        },
        \"save_encoded_frames\": {
          \"enable\": true,
          \"frames_interval_ms\": 180
        },
        \"terminate_if_no_face\": {
          \"enable\": true,
          \"max_invalid_frame\": 5,
          \"max_time_ms\": 3000
        }
      },
      \"id_detection_settings\": {
        \"exif_data_settings\": {
          \"enable\": false
        },
        \"auto_capture\": {
          \"enable\": false,
          \"show_capture_button\": true
        },
        \"blur_check\": {
          \"enable\": true,
          \"threshold\": 0.29,
          \"large_blurry_threshold\": 3000,
          \"small_blurry_threshold\": 13
        },
        \"disable_capture_button_if_alert\": true,
        \"glare_check\": {
          \"enable\": false,
          \"threshold\": 0.001
        },
        \"id_detection\": {
          \"enable\": true
        },
        \"limit_time_settings\": {
          \"enable\": true,
          \"limit_time_second\": 10
        },
        \"save_frame_settings\": {
          \"enable\": true,
          \"frames_interval_ms\": 190,
          \"quality_android\": 80,
          \"quality_ios\": 70
        },
        \"scan_nfc_settings\": {
          \"enable\": true,
          \"max_retries_android\": 5,
          \"request_read_image\": true,
          \"request_clone_detection\": true,
          \"request_integrity_check\": true
        },
        \"scan_qr_settings\": {
          \"enable\": true,
          \"limit_time_second\": 100
        }
      },
      \"liveness_settings\": {
        \"exif_data_settings\": {
          \"enable\": true
        },
        \"vertical_check\": {
          \"enable\": true,
          \"threshold\": 40
        }
      }
    }
  }
}
""";
      var initResult = await TrustVisionPlugin.instance.initialize(
        jsonConfigurationByServer: jsonConfigurationByServer,
        languageCode: 'vi',
      );

      showSnackbar("Init Result: " + initResult.toString());
    } catch (exception) {
      showSnackbar(exception.toString());
      print("Handle exception: $exception");
    }
  }

  void showSnackbar(String message) {
    messangerKey.currentState?.showSnackBar(SnackBar(content: Text(message)));
  }

  void _captureIdCard(String cardSide) async {
    try {
      var selectedCardType = TVCardType(
        id: "vn.national_id",
        name: "CMND cũ / CMND mới / CCCD / Hộ chiếu",
        hasBackSide: true,
        orientation: TVCardOrientation.HORIZONTAL,
      );

      Map<String, dynamic> idCardConfig = {
        "cardType": selectedCardType.toMap(),
        "cardSide": cardSide,
        "isEnableSound": true,
        "isEnableSanityCheck": true,
        "isReadBothSide": false,
        "skipConfirmScreen": false,
        "isEnableDetectingIdCardTampering": true,
      };

      TVDetectionResult? result =
          await TrustVisionPlugin.instance.captureIdCard(idCardConfig);

      print("_captureIdCard result: $result");
    } catch (exception) {
      showSnackbar(exception.toString());
      print("Handle exception: $exception");
    }
  }

  void _captureSelfie(LivenessMode mode) async {
    try {
      final selfieConfig = {
        "cameraOption": SelfieCameraMode.front.name,
        "isEnableSound": true,
        "isEnableSanityCheck": false,
        "isEnableVerifyLiveness": false,
        "livenessMode": mode.name,
        "skipConfirmScreen": false
      };

      List<TvFrameBatch> frameBatchResult = [];

      TVDetectionResult? result =
          await TrustVisionPlugin.instance.captureSelfie(
        selfieConfig,
        onNewFrameBatch: (TvFrameBatch frameBatch) {
          print("onNewFrameBatch-Flutter: ${frameBatch.id}");

          frameBatchResult.add(frameBatch);
        },
      );

      print("_captureSelfie result: $result");
    } catch (exception) {
      showSnackbar("Error: ${exception.toString()}");
      print("Handle exception: $exception");
    }
  }

  void _incrementCounter() {
    if (_counter == 0) {
      _initSDK();
    } else if (_counter % 2 == 0) {
      _captureSelfie(LivenessMode.passive);
    } else {
      _captureIdCard(CardSide.front.name);
    }
    setState(() {
      // This call to setState tells the Flutter framework that something has
      // changed in this State, which causes it to rerun the build method below
      // so that the display can reflect the updated values. If we changed
      // _counter without calling setState(), then the build method would not be
      // called again, and so nothing would appear to happen.
      _counter++;
    });
  }

  @override
  Widget build(BuildContext context) {
    // This method is rerun every time setState is called, for instance as done
    // by the _incrementCounter method above.
    //
    // The Flutter framework has been optimized to make rerunning build methods
    // fast, so that you can just rebuild anything that needs updating rather
    // than having to individually change instances of widgets.
    return Scaffold(
      appBar: AppBar(
        // Here we take the value from the MyHomePage object that was created by
        // the App.build method, and use it to set our appbar title.
        title: Text(widget.title),
      ),
      body: Center(
        // Center is a layout widget. It takes a single child and positions it
        // in the middle of the parent.
        child: Column(
          // Column is also a layout widget. It takes a list of children and
          // arranges them vertically. By default, it sizes itself to fit its
          // children horizontally, and tries to be as tall as its parent.
          //
          // Invoke "debug painting" (press "p" in the console, choose the
          // "Toggle Debug Paint" action from the Flutter Inspector in Android
          // Studio, or the "Toggle Debug Paint" command in Visual Studio Code)
          // to see the wireframe for each widget.
          //
          // Column has various properties to control how it sizes itself and
          // how it positions its children. Here we use mainAxisAlignment to
          // center the children vertically; the main axis here is the vertical
          // axis because Columns are vertical (the cross axis would be
          // horizontal).
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            const Text(
              'You have pushed the button this many times:',
            ),
            Text(
              '$_counter',
              style: Theme.of(context).textTheme.headlineMedium,
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _incrementCounter,
        tooltip: 'Increment',
        child: const Icon(Icons.add),
      ), // This trailing comma makes auto-formatting nicer for build methods.
    );
  }
}
