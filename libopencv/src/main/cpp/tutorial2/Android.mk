LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

OPENCV_INSTALL_MODULES := on
OPENCV_CAMERA_MODULES := on
ifneq ("","$(wildcard $(OPENCV_SDK_PATH)/OpenCV.mk)")
  include $(OPENCV_SDK_PATH)/OpenCV.mk
else
  include $(OPENCV_SDK_PATH)/sdk/native/jni/OpenCV.mk
endif

LOCAL_MODULE    := ocv_mixed_sample
LOCAL_SRC_FILES := jni_part.cpp
LOCAL_LDLIBS += -llog -ldl

include $(BUILD_SHARED_LIBRARY)
