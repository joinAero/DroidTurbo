#include "ocv_orb.h"
#include "jni_helper.h"

#include <opencv2/features2d.hpp>
#include <opencv2/imgproc.hpp>

using namespace cv;

void native_ocv_orb(JNIEnv *env, jobject thiz, jlong addr) {
    DBG_LOGI(__func__);
    TIME_BEG_FUNC2;

    Mat& im_rgba  = *(Mat*)addr;
    Mat im_gray;
    cvtColor(im_rgba, im_gray, COLOR_BGR2GRAY);

    // find the keypoints with ORB
    orb->detect(im_gray, keypoints);
    // compute the descriptors with ORB
    orb->compute(im_gray, keypoints, descriptors);

    // draw only keypoints location, not size and orientation
    drawKeypoints(im_rgba, keypoints, im_rgba, Scalar(0,255,0), DrawMatchesFlags::DEFAULT);

    TIME_END_FUNC2;
}
