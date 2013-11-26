#!/bin/sh

cd "${REPO}"
"${ANDROID_SDK}"/tools/android update project --path SwEng2013QuizApp
"${ANDROID_SDK}"/tools/android update project --path SwEng2013QuizAppTest
"${ANDROID_SDK}"/tools/android update test-project --path SwEng2013QuizAppTest --main ../SwEng2013QuizApp

"${ANDROID_SDK}"/tools/emulator -avd SwEngAndroidDevice &
echo "Please make sure the emulator is fully started and unlocked before proceding."
