include config.mk

MY_MODULE := libgpu.a

MY_FILE_LIST := \
    $(wildcard *.cpp) \
    $(wildcard *.cu)
MY_OBJ_LIST := $(MY_FILE_LIST:%.cpp=%.o)
MY_OBJ_LIST := $(MY_OBJ_LIST:%.cu=%.o)

$(info MY_FILE_LIST: $(MY_FILE_LIST))
$(info MY_OBJ_LIST: $(MY_OBJ_LIST))

MY_INCLUDES += ../ $(ALL_INCLUDES)

%.o: %.cu
	$(NVCC) $(NVCC_FLAGS) -c -o $@ $< $(MY_INCLUDES:%=-I%)

%.o: %.cpp
	$(GCC) $(GCC_FLAGS) -c -o $@ $< $(MY_INCLUDES:%=-I%)

$(MY_MODULE): $(MY_OBJ_LIST)
	$(NVCC) -lib -o $@ $^

.PHONY: all
all: $(MY_MODULE)

.PHONY: clean
clean:
	-rm -f $(wildcard *.o)
	-rm -f $(wildcard *.a)
