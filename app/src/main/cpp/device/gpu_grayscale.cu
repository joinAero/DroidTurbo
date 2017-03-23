#include "gpu_grayscale.h"
#include "gpu.h"

__global__ void kernel_grayscale(
        uint32_t * const rgba_pixels, const uint32_t n) {
    const unsigned int idx = (blockIdx.x * blockDim.x) + threadIdx.x;
    const unsigned int idy = (blockIdx.y * blockDim.y) + threadIdx.y;
    const unsigned int tid = ((gridDim.x * blockDim.x) * idy) + idx;
    if (tid < n) {
        // Y = 0.299*R + 0.587*G + 0.114*B
        uint32_t &rgba = rgba_pixels[tid];
        uint32_t gray = (uint32_t) (
                0.299 * ((rgba >> 24) & 0xFF) +
                0.587 * ((rgba >> 16) & 0xFF) +
                0.114 * ((rgba >> 8) & 0xFF));
        rgba = ((gray & 0xFF) << 24) +
                ((gray & 0xFF) << 16) +
                ((gray & 0xFF) << 8) +
                (gray & 0xFF);
    }
}

void gpu_grayscale(const AndroidBitmapInfo &info, uint32_t *rgba_pixels) {
     const uint32_t n = info.width * info.height;
     GpuArray<uint32_t> gpu_rgba_pixels(n);
     gpu_rgba_pixels.Set(rgba_pixels, n);
     kernel_grayscale<<<n/256+1, 256>>>(gpu_rgba_pixels.GetData(), n);
     gpu_rgba_pixels.Get(rgba_pixels, n);
}
