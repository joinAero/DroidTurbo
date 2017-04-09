#include "ocv_orb.h"

#include "orb.h"

#include "time_cost.hpp"

void native_ocv_orb(JNIEnv *env, jobject thiz, jlong addr) {
    DBG_LOGI(__func__);
    TIME_BEG_FUNC2;

    cv::Mat &im_rgba  = *(cv::Mat*)addr;

    ORB &orb = ORB::GetInstance();
    orb.DetectAndCompute(im_rgba);
    orb.DrawKeypoints(im_rgba);

    TIME_END_FUNC2;
}

void native_ocv_orb_gpu(JNIEnv *env, jobject thiz, jlong addr) {
    DBG_LOGI(__func__);
    TIME_BEG_FUNC2;

    cv::Mat &im_rgba  = *(cv::Mat*)addr;

    GpuORB &orb = GpuORB::GetInstance();
    orb.DetectAndCompute(im_rgba);
    orb.DrawKeypoints(im_rgba);

    TIME_END_FUNC2;
}
