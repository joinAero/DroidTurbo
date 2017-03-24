LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_ARM_NEON := true

GCC := $(NDK_ROOT)/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64/bin/arm-linux-androideabi-g++
NVCC := $(CUDA_TOOLKIT_ROOT)/bin/nvcc -ccbin $(GCC) -target-cpu-arch=ARM -m32 -arch=sm_30 -O3 \
    -Xptxas '-dlcm=ca' -target-os-variant=Android --use_fast_math

MY_MODULE := libgpu.a

MY_FILE_LIST := \
    $(wildcard $(LOCAL_PATH)/*.cpp) \
    $(wildcard $(LOCAL_PATH)/*.cu)
MY_OBJ_LIST := $(MY_FILE_LIST:%.cpp=%.o)
MY_OBJ_LIST := $(MY_OBJ_LIST:%.cu=%.o)

MY_INCLUDES += $(CUDA_TOOLKIT_ROOT)/targets/armv7-linux-androideabi/include

$(info MY_FILE_LIST: $(MY_FILE_LIST))
$(info MY_OBJ_LIST: $(MY_OBJ_LIST))

%.o: %.cu
	$(NVCC) $(CFLAGS) $(EXTRA_CFLAGS) -c -o $@ $<

%.o: %.cpp
	$(GCC) -O3 -march=armv7-a -mtune=cortex-a57 -c -o $@ $< $(MY_INCLUDES:%=-I%)

$(MY_MODULE): $(MY_OBJ_LIST)
	$(NVCC) -lib -o $@ $^

LOCAL_MODULE := gpu
LOCAL_SRC_FILES := $(MY_MODULE)

include $(PREBUILT_STATIC_LIBRARY)
