LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

ifneq ("","$(wildcard $(OPENCV_SDK_PATH)/OpenCV.mk)")
  include $(OPENCV_SDK_PATH)/OpenCV.mk
else
  include $(OPENCV_SDK_PATH)/sdk/native/jni/OpenCV.mk
endif

LOCAL_MODULE    := ocv_mixed_sample
LOCAL_SRC_FILES := jni_part.cpp
LOCAL_C_INCLUDES += $(MY_OPENCV_C_INCLUDES)
LOCAL_LDLIBS += -llog -ldl

include $(BUILD_SHARED_LIBRARY)

ifeq ($(MY_OPENCV_C_INCLUDES),)
  MY_OPENCV_C_INCLUDES := $(OPENCV_LOCAL_C_INCLUDES)
endif
