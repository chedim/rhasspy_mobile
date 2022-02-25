import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get/get_state_manager/src/rx_flutter/rx_obx_widget.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:rhasspy_mobile/logic/microphone_permission.dart';
import 'package:rhasspy_mobile/logic/services.dart';
import 'package:rhasspy_mobile/logic/settings.dart';
import 'package:rhasspy_mobile/ui/screens/rhasspy_settings_screen.dart';
import 'package:rhasspy_mobile/ui/screens/settings_screen.dart';
import 'package:rhasspy_mobile/ui/screens/start_screen.dart';

import 'custom_state.dart';
import 'log_screen.dart';

class MainScreen extends StatefulWidget {
  const MainScreen({Key? key}) : super(key: key);

  @override
  State<MainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends CustomState<MainScreen> with SingleTickerProviderStateMixin, WidgetsBindingObserver {
  late AnimationController _controller;

  @override
  void initState() {
    _controller = AnimationController(
      duration: const Duration(milliseconds: 1000),
      vsync: this,
    );
    WidgetsBinding.instance?.addObserver(this);
    setupMicrophonePermission();
    super.initState();
  }

  ///request microphone permission when app starts because it's the main usage of the app
  void setupMicrophonePermission() async {
    microphonePermissionGranted.value = await Permission.microphone.isGranted;
    requestMicrophonePermission(context);
  }

  // check permissions when app is resumed
  // this is when permissions are changed in app settings outside of app
  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.resumed) {
      resumedApp();
    }
  }

  @override
  void dispose() {
    WidgetsBinding.instance?.removeObserver(this);
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget content() {
    return Obx(
      () => Scaffold(appBar: navigationBar(), body: getBody(), bottomNavigationBar: bottomNavigation()),
    );
  }

  /// App Navigation bar with Title and Button to settings
  PreferredSizeWidget navigationBar() {
    _controller.reset();
    return AppBar(title: Text(locale.appName), actions: getActions());
  }

  List<Widget> getActions() {
    List<Widget> actions = [];

    if (settingsChanged.value) {
      actions.add(
        IconButton(
          icon: RotationTransition(
              turns: Tween(begin: 0.0, end: 1.0).animate(_controller),
              child: const Icon(
                Icons.published_with_changes,
              )),
          onPressed: () {
            _controller.repeat();
            reloadServices();
          },
        ),
      );
    }

    if (!microphonePermissionGranted.value) {
      actions.add(
        IconButton(
          icon: Icon(Icons.warning, color: theme.colorScheme.error),
          onPressed: () {
            requestMicrophonePermission(context);
          },
        ),
      );
    }

    return actions;
  }

  Widget? getBody() {
    switch (_selectedIndex) {
      case 0:
        return const StartScreen();
      case 1:
        return const RhasspySettingsScreen();
      case 2:
        return const SettingsScreen();
      case 3:
        return const LogScreen();
      default:
        return null;
    }
  }

  int _selectedIndex = 0;

  Widget bottomNavigation() {
    List<BottomNavigationBarItem> items = <BottomNavigationBarItem>[
      BottomNavigationBarItem(
        icon: Obx(() => Icon(
              Icons.mic,
              color: wakeWordService.wakeWordRecognized.value ? theme.colorScheme.primary : null,
            )),
        label: locale.home,
      ),
      BottomNavigationBarItem(
        icon: const ImageIcon(
          AssetImage('assets/rhasspy_icon.png'),
        ),
        label: locale.configuration,
      ),
      BottomNavigationBarItem(
        icon: const Icon(Icons.settings),
        label: locale.settings,
      )
    ];

    if (showLogSetting.value) {
      items.add(BottomNavigationBarItem(
        icon: const Icon(Icons.reorder),
        label: locale.log,
      ));
    }

    return BottomNavigationBar(
      items: items,
      type: BottomNavigationBarType.fixed,
      currentIndex: _selectedIndex,
      backgroundColor: theme.colorScheme.surfaceVariant,
      selectedItemColor: theme.colorScheme.onSurfaceVariant,
      onTap: _onItemTapped,
    );
  }

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });
  }
}
