#ifndef GPU_H_
#define GPU_H_
#pragma once

#include <cassert>
#include <cstdio>

#include <algorithm>
#include <iterator>

#include <cuda_runtime.h>

#define CUDA_CALL(func) do { \
    const cudaError_t a = (func); \
    if (a != cudaSuccess) { \
        printf("\nCUDA Error: %s (err_num=%d)\n", cudaGetErrorString(a), a); \
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
        size_t min = std::min(size, GetSize());
        CUDA_CALL(cudaMemcpy(first_, host, min * sizeof(T), cudaMemcpyHostToDevice));
    }

    // Get host from devie
    void Get(T *host, size_t size) {
        size_t min = std::min(size, GetSize());
        CUDA_CALL(cudaMemcpy(host, first_, min * sizeof(T), cudaMemcpyDeviceToHost));
    }

private:
    void Allocate(size_t size) {
        cudaError_t result = cudaMalloc((void **)&first_, size * sizeof(T));
        if (result != cudaSuccess) {
            first_ = last_ = nullptr;
            printf("\nCUDA Error: %s (err_num=%d)\n",
                   cudaGetErrorString(result), result);
            cudaDeviceReset();
            assert(0);
        }
        last_ = first_ + size;
    }

    void Free() {
        if (first_) {
            cudaFree(first_);
            first_ = last_ = nullptr;
        }
    }

    T *first_;
    T *last_;
};

#endif  // GPU_H_
