#include "gpu.h"

bool deviceQuery() {
    LOGI(" CUDA Device Query (Runtime API) version (CUDART static linking)\n");

    int deviceCount = 0;
    cudaError_t error_id = cudaGetDeviceCount(&deviceCount);

    if (error_id != cudaSuccess) {
        LOGI("cudaGetDeviceCount returned %d\n-> %s", (int)error_id, cudaGetErrorString(error_id));
        LOGI("Result = FAIL");
        return false;
    }

    // This function call returns 0 if there are no CUDA capable devices.
    if (deviceCount == 0) {
        LOGI("There are no available device(s) that support CUDA");
    } else {
        LOGI("Detected %d CUDA Capable device(s)", deviceCount);
    }

    int dev, driverVersion = 0, runtimeVersion = 0;

    for (dev = 0; dev < deviceCount; ++dev) {
        cudaSetDevice(dev);
        cudaDeviceProp deviceProp;
        cudaGetDeviceProperties(&deviceProp, dev);

        LOGI("\nDevice %d: \"%s\"", dev, deviceProp.name);

        // Console log
        cudaDriverGetVersion(&driverVersion);
        cudaRuntimeGetVersion(&runtimeVersion);
        LOGI("  CUDA Driver Version / Runtime Version          %d.%d / %d.%d",
             driverVersion/1000, (driverVersion%100)/10, runtimeVersion/1000, (runtimeVersion%100)/10);
        LOGI("  CUDA Capability Major/Minor version number:    %d.%d", deviceProp.major, deviceProp.minor);

        char msg[256];
        sprintf(msg, "  Total amount of global memory:                 %.0f MBytes (%llu bytes)",
                (float)deviceProp.totalGlobalMem/1048576.0f, (unsigned long long) deviceProp.totalGlobalMem);
        LOGI("%s", msg);

        LOGI("  (%2d) Multiprocessors, (%3d) CUDA Cores/MP:     %d CUDA Cores",
             deviceProp.multiProcessorCount,
             _ConvertSMVer2Cores(deviceProp.major, deviceProp.minor),
             _ConvertSMVer2Cores(deviceProp.major, deviceProp.minor) * deviceProp.multiProcessorCount);
        LOGI("  GPU Max Clock rate:                            %.0f MHz (%0.2f GHz)",
             deviceProp.clockRate * 1e-3f, deviceProp.clockRate * 1e-6f);

#if CUDART_VERSION >= 5000
        // This is supported in CUDA 5.0 (runtime API device properties)
        LOGI("  Memory Clock rate:                             %.0f Mhz", deviceProp.memoryClockRate * 1e-3f);
        LOGI("  Memory Bus Width:                              %d-bit",   deviceProp.memoryBusWidth);

        if (deviceProp.l2CacheSize) {
            LOGI("  L2 Cache Size:                                 %d bytes", deviceProp.l2CacheSize);
        }
#else
        // This only available in CUDA 4.0-4.2 (but these were only exposed in the CUDA Driver API)
        int memoryClock;
        getCudaAttribute<int>(&memoryClock, CU_DEVICE_ATTRIBUTE_MEMORY_CLOCK_RATE, dev);
        LOGI("  Memory Clock rate:                             %.0f Mhz", memoryClock * 1e-3f);
        int memBusWidth;
        getCudaAttribute<int>(&memBusWidth, CU_DEVICE_ATTRIBUTE_GLOBAL_MEMORY_BUS_WIDTH, dev);
        LOGI("  Memory Bus Width:                              %d-bit", memBusWidth);
        int L2CacheSize;
        getCudaAttribute<int>(&L2CacheSize, CU_DEVICE_ATTRIBUTE_L2_CACHE_SIZE, dev);

        if (L2CacheSize) {
            LOGI("  L2 Cache Size:                                 %d bytes", L2CacheSize);
        }
#endif

        LOGI("  Maximum Texture Dimension Size (x,y,z)         1D=(%d), 2D=(%d, %d), 3D=(%d, %d, %d)",
             deviceProp.maxTexture1D   , deviceProp.maxTexture2D[0], deviceProp.maxTexture2D[1],
             deviceProp.maxTexture3D[0], deviceProp.maxTexture3D[1], deviceProp.maxTexture3D[2]);
        LOGI("  Maximum Layered 1D Texture Size, (num) layers  1D=(%d), %d layers",
             deviceProp.maxTexture1DLayered[0], deviceProp.maxTexture1DLayered[1]);
        LOGI("  Maximum Layered 2D Texture Size, (num) layers  2D=(%d, %d), %d layers",
             deviceProp.maxTexture2DLayered[0], deviceProp.maxTexture2DLayered[1], deviceProp.maxTexture2DLayered[2]);

        LOGI("  Total amount of constant memory:               %lu bytes", deviceProp.totalConstMem);
        LOGI("  Total amount of shared memory per block:       %lu bytes", deviceProp.sharedMemPerBlock);
        LOGI("  Total number of registers available per block: %d", deviceProp.regsPerBlock);
        LOGI("  Warp size:                                     %d", deviceProp.warpSize);
        LOGI("  Maximum number of threads per multiprocessor:  %d", deviceProp.maxThreadsPerMultiProcessor);
        LOGI("  Maximum number of threads per block:           %d", deviceProp.maxThreadsPerBlock);
        LOGI("  Max dimension size of a thread block (x,y,z): (%d, %d, %d)",
             deviceProp.maxThreadsDim[0],
             deviceProp.maxThreadsDim[1],
             deviceProp.maxThreadsDim[2]);
        LOGI("  Max dimension size of a grid size    (x,y,z): (%d, %d, %d)",
             deviceProp.maxGridSize[0],
             deviceProp.maxGridSize[1],
             deviceProp.maxGridSize[2]);
        LOGI("  Maximum memory pitch:                          %lu bytes", deviceProp.memPitch);
        LOGI("  Texture alignment:                             %lu bytes", deviceProp.textureAlignment);
        LOGI("  Concurrent copy and kernel execution:          %s with %d copy engine(s)", (deviceProp.deviceOverlap ? "Yes" : "No"), deviceProp.asyncEngineCount);
        LOGI("  Run time limit on kernels:                     %s", deviceProp.kernelExecTimeoutEnabled ? "Yes" : "No");
        LOGI("  Integrated GPU sharing Host Memory:            %s", deviceProp.integrated ? "Yes" : "No");
        LOGI("  Support host page-locked memory mapping:       %s", deviceProp.canMapHostMemory ? "Yes" : "No");
        LOGI("  Alignment requirement for Surfaces:            %s", deviceProp.surfaceAlignment ? "Yes" : "No");
        LOGI("  Device has ECC support:                        %s", deviceProp.ECCEnabled ? "Enabled" : "Disabled");
#if defined(WIN32) || defined(_WIN32) || defined(WIN64) || defined(_WIN64)
        LOGI("  CUDA Device Driver Mode (TCC or WDDM):         %s", deviceProp.tccDriver ? "TCC (Tesla Compute Cluster Driver)" : "WDDM (Windows Display Driver Model)");
#endif
        LOGI("  Device supports Unified Addressing (UVA):      %s", deviceProp.unifiedAddressing ? "Yes" : "No");
        LOGI("  Device PCI Domain ID / Bus ID / location ID:   %d / %d / %d", deviceProp.pciDomainID, deviceProp.pciBusID, deviceProp.pciDeviceID);

        const char *sComputeMode[] = {
            "Default (multiple host threads can use ::cudaSetDevice() with device simultaneously)",
            "Exclusive (only one host thread in one process is able to use ::cudaSetDevice() with this device)",
            "Prohibited (no host thread can use ::cudaSetDevice() with this device)",
            "Exclusive Process (many threads in one process is able to use ::cudaSetDevice() with this device)",
            "Unknown",
            NULL
        };
        LOGI("  Compute Mode:");
        LOGI("     < %s >", sComputeMode[deviceProp.computeMode]);
    }

    // If there are 2 or more GPUs, query to determine whether RDMA is supported
    if (deviceCount >= 2) {
        cudaDeviceProp prop[64];
        int gpuid[64]; // we want to find the first two GPUs that can support P2P
        int gpu_p2p_count = 0;

        for (int i=0; i < deviceCount; i++) {
            checkCudaErrors(cudaGetDeviceProperties(&prop[i], i));

            // Only boards based on Fermi or later can support P2P
            if ((prop[i].major >= 2)
#if defined(WIN32) || defined(_WIN32) || defined(WIN64) || defined(_WIN64)
                // on Windows (64-bit), the Tesla Compute Cluster driver for windows must be enabled to support this
                && prop[i].tccDriver
#endif
                ) {
                // This is an array of P2P capable GPUs
                gpuid[gpu_p2p_count++] = i;
            }
        }

        // Show all the combinations of support P2P GPUs
        int can_access_peer;

        if (gpu_p2p_count >= 2) {
            for (int i = 0; i < gpu_p2p_count; i++) {
                for (int j = 0; j < gpu_p2p_count; j++) {
                    if (gpuid[i] == gpuid[j]) {
                        continue;
                    }
                    checkCudaErrors(cudaDeviceCanAccessPeer(&can_access_peer, gpuid[i], gpuid[j]));
                    LOGI("> Peer access from %s (GPU%d) -> %s (GPU%d) : %s", prop[gpuid[i]].name, gpuid[i],
                         prop[gpuid[j]].name, gpuid[j] ,
                         can_access_peer ? "Yes" : "No");
                }
            }
        }
    }

    // csv masterlog info
    // *****************************
    // exe and CUDA driver name
    LOGI("\n");
    std::string sProfileString = "deviceQuery, CUDA Driver = CUDART";
    char cTemp[16];
    // driver version
    sProfileString += ", CUDA Driver Version = ";
#if defined(WIN32) || defined(_WIN32) || defined(WIN64) || defined(_WIN64)
    sprintf_s(cTemp, 10, "%d.%d", driverVersion/1000, (driverVersion%100)/10);
#else
    sprintf(cTemp, "%d.%d", driverVersion/1000, (driverVersion%100)/10);
#endif
    sProfileString += cTemp;

    // Runtime version
    sProfileString += ", CUDA Runtime Version = ";
#if defined(WIN32) || defined(_WIN32) || defined(WIN64) || defined(_WIN64)
    sprintf_s(cTemp, 10, "%d.%d", runtimeVersion/1000, (runtimeVersion%100)/10);
#else
    sprintf(cTemp, "%d.%d", runtimeVersion/1000, (runtimeVersion%100)/10);
#endif
    sProfileString += cTemp;

    // Device count
    sProfileString += ", NumDevs = ";
#if defined(WIN32) || defined(_WIN32) || defined(WIN64) || defined(_WIN64)
    sprintf_s(cTemp, 10, "%d", deviceCount);
#else
    sprintf(cTemp, "%d", deviceCount);
#endif
    sProfileString += cTemp;

    // Print Out all device Names
    for (dev = 0; dev < deviceCount; ++dev) {
#if defined(WIN32) || defined(_WIN32) || defined(WIN64) || defined(_WIN64)
        sprintf_s(cTemp, 13, ", Device%d = ", dev);
#else
        sprintf(cTemp, ", Device%d = ", dev);
#endif
        cudaDeviceProp deviceProp;
        cudaGetDeviceProperties(&deviceProp, dev);
        sProfileString += cTemp;
        sProfileString += deviceProp.name;
    }
    LOGI("%s", sProfileString.c_str());

    LOGI("Result = PASS");
    return true;
}
