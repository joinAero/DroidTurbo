#!/bin/bash

cd "$(dirname "$0")"

ecol() {
    local text="$1"; shift
    if [ -z "$text" ]; then
        echo; return
    fi
    local code="$1"; shift
    [ -n "$code" ] || code="1;36"
    echo -e "\033[${code}m${text}\033[0m"
}

ered()   { ecol "$1" "1;31"; }
egreen() { ecol "$1" "1;32"; }
eblue()  { ecol "$1" "1;34"; }

# OpenCV

OCV_VERSION=3.2.0
OCV_ZIP=opencv-${OCV_VERSION}-android-sdk.zip
OCV_URL=https://nchc.dl.sourceforge.net/project/opencvlibrary/opencv-android/${OCV_VERSION}/${OCV_ZIP}
OCV_DIR=OpenCV-android-sdk

if [[ ! -e "$OCV_DIR" ]]; then
    # fetch the ocv sdk zip
    if [[ ! -e "$OCV_ZIP" ]]; then
        egreen "Fetch OpenCV: ${OCV_URL}"
        curl -O "${OCV_URL}"
    fi
    # unzip the ocv sdk zip
    egreen "Unzip OpenCV SDK: ${OCV_ZIP}"
    unzip "$OCV_ZIP"
fi

egreen "Get ready for OpenCV SDK $OCV_VERSION"
