#!/bin/sh

echo "Make sure the emulator is started and unlocked!"
"${ANDROID_SDK}"/platform-tools/adb kill-server
"${ANDROID_SDK}"/platform-tools/adb start-server
"${ANDROID_SDK}"/platform-tools/adb devices

cd "${REPO}"/SwEng2013QuizAppTest
ant clean emma debug install test
open bin/coverage.html
