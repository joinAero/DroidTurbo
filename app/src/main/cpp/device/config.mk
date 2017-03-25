
USE_CUDA_65 := off

ifneq ($(USE_CUDA_65),on)
  NDK_ROOT := $(HOME)/Android/Sdk/ndk-bundle
  CUDA_TOOLKIT_ROOT := $(HOME)/NVPACK/cuda-7.0
else
  NDK_ROOT := $(HOME)/NVPACK/android-ndk-r10e
  CUDA_TOOLKIT_ROOT := $(HOME)/NVPACK/cuda-6.5
endif

$(info NDK_ROOT: $(NDK_ROOT))
$(info CUDA_TOOLKIT_ROOT: $(CUDA_TOOLKIT_ROOT))

ifneq ($(USE_CUDA_65),on)
  NDK_TOOLCHAIN := $(NDK_ROOT)/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64
else
  NDK_TOOLCHAIN := $(NDK_ROOT)/toolchains/arm-linux-androideabi-4.6/gen_standalone/linux-x86_64
endif

GCC := $(NDK_TOOLCHAIN)/bin/arm-linux-androideabi-g++
NVCC := $(CUDA_TOOLKIT_ROOT)/bin/nvcc -ccbin $(GCC) -target-cpu-arch=ARM -m32 -arch=sm_30 -O3 \
    -Xptxas '-dlcm=ca' -target-os-variant=Android --use_fast_math

$(info GCC: $(GCC))
$(info NVCC: $(NVCC))

CC_FLAGS := -std=c++11
GCC_FLAGS := $(CC_FLAGS) -O3 -march=armv7-a
ifneq ($(USE_CUDA_65),on)
  GCC_FLAGS += -mtune=cortex-a57
endif
NVCC_FLAGS := $(CC_FLAGS)

NDK_INCLUDES :=
ifneq ($(USE_CUDA_65),on)
  NDK_INCLUDES := \
    $(NDK_ROOT)/sysroot/usr/include \
    $(NDK_ROOT)/sysroot/usr/include/arm-linux-androideabi \
    $(NDK_ROOT)/sources/cxx-stl/gnu-libstdc++/4.9/include \
    $(NDK_ROOT)/sources/cxx-stl/gnu-libstdc++/4.9/libs/armeabi-v7a/include
endif
CUDA_INCLUDES := $(CUDA_TOOLKIT_ROOT)/targets/armv7-linux-androideabi/include
ALL_INCLUDES := $(NDK_INCLUDES) $(CUDA_INCLUDES)
