import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:rhasspy_mobile/logic/options/speech_to_text_options.dart';
import 'package:rhasspy_mobile/logic/settings.dart';
import 'package:rhasspy_mobile/ui/screens/custom_state.dart';

extension SpeechToTextWidget on CustomState {
  Widget speechToText() {
    return autoSaveExpandableDropDownListItem(
        title: locale.speechToText,
        option: SpeechToTextOptions(),
        setting: speechToTextSetting,
        child: Obx(() => speechToTextSettings(speechToTextSetting.value)));
  }

  Widget speechToTextSettings(SpeechToTextOption speechToTextOption) {
    if (speechToTextOption == SpeechToTextOption.remoteHTTP) {
      return Column(children: [const Divider(), autoSaveTextField(title: locale.speechToTextURL, setting: speechToTextHTTPURLSetting)]);
    } else {
      return Container();
    }
  }
}
