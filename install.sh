#!/bin/bash
path=../../../sdk/android-sdk-linux_86/tools
if [ -n "$1" ]
then
  type=$1
else
  echo "No parameter"
  exit 0
fi

$path/adb install -r ./bin/MeasuredTimer-$type.apk
