MY_LOCAL_PATH := $(call my-dir)

include $(MY_LOCAL_PATH)/config.mk

# prebuilt

USE_LOCAL_PREBUILT := on
ifeq ($(USE_LOCAL_PREBUILT),on)
  ifneq ($(USE_CUDA_65),on)
    CUDA_PREBUILT_ROOT := $(MY_LOCAL_PATH)/prebuilt/cuda-7.0
  else
    CUDA_PREBUILT_ROOT := $(MY_LOCAL_PATH)/prebuilt/cuda-6.5
  endif
else
  CUDA_PREBUILT_ROOT := $(CUDA_TOOLKIT_ROOT)
endif

# cudart cufft nppc nppi npps ...

LOCAL_PATH := $(MY_LOCAL_PATH)
include $(CLEAR_VARS)
LOCAL_MODULE := cudart_static
LOCAL_SRC_FILES := $(CUDA_PREBUILT_ROOT)/targets/armv7-linux-androideabi/lib/libcudart_static.a
LOCAL_EXPORT_C_INCLUDES := $(CUDA_TOOLKIT_ROOT)/targets/armv7-linux-androideabi/include
include $(PREBUILT_STATIC_LIBRARY)

LOCAL_PATH := $(MY_LOCAL_PATH)
include $(CLEAR_VARS)
LOCAL_MODULE := cudart
LOCAL_SRC_FILES := $(CUDA_PREBUILT_ROOT)/targets/armv7-linux-androideabi/lib/libcudart.so
LOCAL_EXPORT_C_INCLUDES := $(CUDA_TOOLKIT_ROOT)/targets/armv7-linux-androideabi/include
include $(PREBUILT_SHARED_LIBRARY)

# gpu

LOCAL_PATH := $(MY_LOCAL_PATH)

include $(CLEAR_VARS)

LOCAL_ARM_NEON := true

MY_MODULE := libgpu.a

MY_FILE_LIST := \
    $(wildcard $(LOCAL_PATH)/*.cpp) \
    $(wildcard $(LOCAL_PATH)/*.cu)
MY_OBJ_LIST := $(MY_FILE_LIST:%.cpp=%.o)
MY_OBJ_LIST := $(MY_OBJ_LIST:%.cu=%.o)

$(info MY_FILE_LIST: $(MY_FILE_LIST))
$(info MY_OBJ_LIST: $(MY_OBJ_LIST))

MY_INCLUDES += $(ALL_INCLUDES)

%.o: %.cu
	$(NVCC) $(NVCC_FLAGS) -c -o $@ $< $(MY_INCLUDES:%=-I%)

%.o: %.cpp
	$(GCC) $(GCC_FLAGS) -c -o $@ $< $(MY_INCLUDES:%=-I%)

$(MY_MODULE): $(MY_OBJ_LIST)
	$(NVCC) -lib -o $@ $^

LOCAL_MODULE := gpu
LOCAL_SRC_FILES := $(MY_MODULE)

include $(PREBUILT_STATIC_LIBRARY)