LOCAL_PATH := $(call my-dir)

OPENCV_SDK_PATH := $(HOME)/Workspace/opencv-3.2.0/platforms/build_android_arm
#OPENCV_SDK_PATH := $(MY_PROJECT_PATH)/OpenCV-android-sdk

include $(CLEAR_VARS)

OPENCV_INSTALL_MODULES := on
ifneq ("","$(wildcard $(OPENCV_SDK_PATH)/OpenCV.mk)")
  include $(OPENCV_SDK_PATH)/OpenCV.mk
else
  include $(OPENCV_SDK_PATH)/sdk/native/jni/OpenCV.mk
endif

LOCAL_MODULE    := ocv_grayscale
LOCAL_SRC_FILES := \
  jni_main.cpp \
  ocv_grayscale.cpp
LOCAL_C_INCLUDES += $(MY_C_INCLUDES)
LOCAL_LDLIBS += -llog

include $(BUILD_SHARED_LIBRARY)
