LOCAL_PATH := $(call my-dir)

OPENCV_SDK_PATH := $(MY_PROJECT_PATH)/OpenCV-android-sdk

include $(CLEAR_VARS)

OPENCV_INSTALL_MODULES := on
OPENCV_CAMERA_MODULES := on
ifneq ("","$(wildcard $(OPENCV_SDK_PATH)/OpenCV.mk)")
  include $(OPENCV_SDK_PATH)/OpenCV.mk
else
  include $(OPENCV_SDK_PATH)/sdk/native/jni/OpenCV.mk
endif

LOCAL_MODULE    := ocv_all
LOCAL_SRC_FILES := $(wildcard $(LOCAL_PATH)/*.cpp)
LOCAL_C_INCLUDES += $(MY_C_INCLUDES)
LOCAL_LDLIBS += -llog
LOCAL_CPPFLAGS += -DDEBUG -DTIME_COST

include $(BUILD_SHARED_LIBRARY)
