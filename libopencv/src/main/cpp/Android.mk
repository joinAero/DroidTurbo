MY_LOCAL_PATH := $(call my-dir)
MY_MODULE_PATH := $(MY_LOCAL_PATH)/../../..
MY_PROJECT_PATH := $(MY_MODULE_PATH)/..

MY_SUBDIR_MKS := $(call all-subdir-makefiles)

LOCAL_PATH := $(MY_LOCAL_PATH)

include $(CLEAR_VARS)

OPENCV_SDK_PATH := $(MY_PROJECT_PATH)/OpenCV-android-sdk
$(info OPENCV_SDK_PATH=$(OPENCV_SDK_PATH))

OPENCV_INSTALL_MODULES := on
OPENCV_CAMERA_MODULES := on
ifneq ("","$(wildcard $(OPENCV_SDK_PATH)/OpenCV.mk)")
  include $(OPENCV_SDK_PATH)/OpenCV.mk
else
  include $(OPENCV_SDK_PATH)/sdk/native/jni/OpenCV.mk
endif

LOCAL_MODULE := opencv
include $(BUILD_SHARED_LIBRARY)

$(info MY_SUBDIR_MKS=$(MY_SUBDIR_MKS))
include $(MY_SUBDIR_MKS)
