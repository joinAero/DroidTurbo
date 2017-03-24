
NDK_ROOT := $(HOME)/Android/Sdk/ndk-bundle

$(info NDK_ROOT: $(NDK_ROOT))
$(info CUDA_TOOLKIT_ROOT: $(CUDA_TOOLKIT_ROOT))

GCC := $(NDK_ROOT)/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64/bin/arm-linux-androideabi-g++
NVCC := $(CUDA_TOOLKIT_ROOT)/bin/nvcc -ccbin $(GCC) -target-cpu-arch=ARM -m32 -arch=sm_30 -O3 \
    -Xptxas '-dlcm=ca' -target-os-variant=Android --use_fast_math

$(info GCC: $(GCC))
$(info NVCC: $(NVCC))

MY_MODULE := libgpu.a

MY_FILE_LIST := \
    $(wildcard *.cpp) \
    $(wildcard *.cu)
MY_OBJ_LIST := $(MY_FILE_LIST:%.cpp=%.o)
MY_OBJ_LIST := $(MY_OBJ_LIST:%.cu=%.o)

#MY_OBJ_LIST += $(CUDA_TOOLKIT_ROOT)/targets/armv7-linux-androideabi/lib/libcudart_static.a

MY_INCLUDES += ../ \
    $(NDK_ROOT)/sysroot/usr/include \
    $(NDK_ROOT)/sysroot/usr/include/arm-linux-androideabi \
    $(NDK_ROOT)/sources/cxx-stl/gnu-libstdc++/4.9/include \
    $(NDK_ROOT)/sources/cxx-stl/gnu-libstdc++/4.9/libs/armeabi-v7a/include \
    $(CUDA_TOOLKIT_ROOT)/targets/armv7-linux-androideabi/include

$(info MY_FILE_LIST: $(MY_FILE_LIST))
$(info MY_OBJ_LIST: $(MY_OBJ_LIST))

CFLAGS += -std=c++11
$(info CFLAGS: $(CFLAGS))

%.o: %.cu
	$(NVCC) $(CFLAGS) -c -o $@ $< $(MY_INCLUDES:%=-I%)

%.o: %.cpp
	$(GCC) $(CFLAGS) -O3 -march=armv7-a -mtune=cortex-a57 -c -o $@ $< $(MY_INCLUDES:%=-I%)

$(MY_MODULE): $(MY_OBJ_LIST)
	$(NVCC) -lib -o $@ $^

.PHONY: all
all: $(MY_MODULE)

.PHONY: clean
clean:
	-rm -f $(wildcard *.o)
	-rm -f $(wildcard *.a)
