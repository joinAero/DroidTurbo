MY_LOCAL_PATH := $(call my-dir)
MY_MODULE_PATH := $(MY_LOCAL_PATH)/../../..
MY_PROJECT_PATH := $(MY_MODULE_PATH)/..

OPENCV_SDK_PATH := $(MY_PROJECT_PATH)/OpenCV-android-sdk
#$(info OPENCV_SDK_PATH=$(OPENCV_SDK_PATH))

include $(call all-subdir-makefiles)
