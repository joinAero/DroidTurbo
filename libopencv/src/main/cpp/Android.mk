MY_LOCAL_PATH := $(call my-dir)
MY_MODULE_PATH := $(MY_LOCAL_PATH)/../../..
MY_PROJECT_PATH := $(MY_MODULE_PATH)/..

OPENCV_SDK_PATH := $(HOME)/Workspace/opencv-3.2.0/platforms/build_android_arm
#OPENCV_SDK_PATH := $(MY_PROJECT_PATH)/OpenCV-android-sdk
#$(info OPENCV_SDK_PATH=$(OPENCV_SDK_PATH))

OPENCV_INSTALL_MODULES := on
OPENCV_CAMERA_MODULES := on
ifneq ("","$(wildcard $(OPENCV_SDK_PATH)/OpenCV.mk)")
  include $(OPENCV_SDK_PATH)/OpenCV.mk
else
  include $(OPENCV_SDK_PATH)/sdk/native/jni/OpenCV.mk
endif

MY_C_INCLUDES := $(OPENCV_LOCAL_C_INCLUDES)

include $(call all-subdir-makefiles)
