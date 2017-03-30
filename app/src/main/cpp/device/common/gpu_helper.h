#ifndef GPU_HELP_H_
#define GPU_HELP_H_
#pragma once

#include <cassert>
#include <cstdio>

#include <algorithm>
#include <iterator>

#include <cuda_runtime.h>
#include "helper_cuda.h"

#include "time_cost.h"

#if CUDART_VERSION < 5000

#include <cuda.h>

// This function wraps the CUDA Driver API into a template function
template <class T>
inline void getCudaAttribute(T *attribute, CUdevice_attribute device_attribute, int device) {
    CUresult error = cuDeviceGetAttribute(attribute, device_attribute, device);
    if (CUDA_SUCCESS != error) {
        LOGE("cuSafeCallNoSync() Driver API error = %04d from file <%s>, line %i.",
             error, __FILE__, __LINE__);
        assert(0);
    }
}

#endif /* CUDART_VERSION < 5000 */

#define CUDA_CALL(func) do { \
    const cudaError_t a = (func); \
    if (a != cudaSuccess) { \
        LOGE("CUDA Error: %s (err_num=%d)", cudaGetErrorString(a), a); \
        cudaDeviceReset(); \
        assert(0); \
    } \
} while (0)

template <class T>
class GpuArray {
public:
    explicit GpuArray(size_t size)
            : first_(nullptr), last_(nullptr) {
        Allocate(size);
    }

    ~GpuArray() {
        Free();
    }

    void Resize(size_t size) {
        Free();
        Allocate(size);
    }

    size_t GetSize() {
        return std::distance(first_, last_);
    }

    const T *GetData() const {
        return first_;
    }

    T *GetData() {
        return first_;
    }

    // Set host to device
    void Set(const T *host, size_t size) {
        FUNC_TIME_BEG("GpuArray");
        size_t min = std::min(size, GetSize());
        CUDA_CALL(cudaMemcpy(first_, host, min * sizeof(T), cudaMemcpyHostToDevice));
        FUNC_TIME_END("GpuArray");
    }

    // Get host from devie
    void Get(T *host, size_t size) {
        FUNC_TIME_BEG("GpuArray");
        size_t min = std::min(size, GetSize());
        CUDA_CALL(cudaMemcpy(host, first_, min * sizeof(T), cudaMemcpyDeviceToHost));
        FUNC_TIME_END("GpuArray");
    }

private:
    void Allocate(size_t size) {
        FUNC_TIME_BEG("GpuArray");
        cudaError_t result = cudaMalloc((void **)&first_, size * sizeof(T));
        if (result != cudaSuccess) {
            first_ = last_ = nullptr;
            LOGE("CUDA Error: %s (err_num=%d)", cudaGetErrorString(result), result);
            cudaDeviceReset();
            assert(0);
        }
        last_ = first_ + size;
        FUNC_TIME_END("GpuArray");
    }

    void Free() {
        FUNC_TIME_BEG("GpuArray");
        if (first_) {
            cudaFree(first_);
            first_ = last_ = nullptr;
        }
        FUNC_TIME_END("GpuArray");
    }

    T *first_;
    T *last_;
};

#endif  // GPU_HELP_H_