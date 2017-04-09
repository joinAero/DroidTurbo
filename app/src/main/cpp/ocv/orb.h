#ifndef ORB_H_
#define ORB_H_
#pragma once

#include <opencv2/cudafeatures2d.hpp>
#include <opencv2/cudaimgproc.hpp>
#include <opencv2/features2d.hpp>
#include <opencv2/imgproc.hpp>

#include "global.hpp"

// ORB

class ORB {
public:
    static ORB &GetInstance() {
        static ORB instance;
        return instance;
    }

    void DetectAndCompute(const cv::Mat &im) {
        if (im.channels() == 3) {  // rgb
            cv::cvtColor(im, im_gray_, CV_RGB2GRAY);
        } else if (im.channels() == 4) {  // rgba
            cv::cvtColor(im, im_gray_, CV_RGBA2GRAY);
        } else {
            im_gray_ = im;
        }

        // find the keypoints with ORB
        orb_->detect(im_gray_, keypoints_);
        // compute the descriptors with ORB
        orb_->compute(im_gray_, keypoints_, im_desc_);
    }

    void DrawKeypoints(const cv::Mat &im) {
        // draw only keypoints location, not size and orientation
        cv::drawKeypoints(im, keypoints_, im, cv::Scalar(0,255,0),
            cv::DrawMatchesFlags::DRAW_OVER_OUTIMG);
    }

    std::vector<cv::KeyPoint> keypoints() const { return keypoints_; }
    cv::Mat descriptors() const { return im_desc_; }

private:
    ORB() : orb_(cv::ORB::create()) {}

    cv::Ptr<cv::ORB> orb_;

    std::vector<cv::KeyPoint> keypoints_;

    cv::Mat im_gray_;
    cv::Mat im_desc_;

    DISABLE_COPY(ORB)
};

// GPU ORB

class GpuORB {
public:
    static GpuORB &GetInstance() {
        static GpuORB instance;
        return instance;
    }

    void DetectAndCompute(const cv::Mat &im) {
        gpu_src_.upload(im);

        if (im.channels() == 3) {  // rgb
            cv::cuda::cvtColor(gpu_src_, gpu_gray_, CV_RGB2GRAY);
        } else if (im.channels() == 4) {  // rgba
            cv::cuda::cvtColor(gpu_src_, gpu_gray_, CV_RGBA2GRAY);
        } else {
            gpu_gray_ = gpu_src_;
        }

        // find the keypoints with ORB
        orb_->detect(gpu_gray_, keypoints_);
        // compute the descriptors with ORB
        orb_->compute(gpu_gray_, keypoints_, gpu_desc_);
    }

    void DrawKeypoints(const cv::Mat &im) {
        // draw only keypoints location, not size and orientation
        cv::drawKeypoints(im, keypoints_, im, cv::Scalar(0,255,0),
                          cv::DrawMatchesFlags::DRAW_OVER_OUTIMG);
    }

    std::vector<cv::KeyPoint> keypoints() const { return keypoints_; }
    cv::cuda::GpuMat descriptors() const { return gpu_desc_; }

private:
    GpuORB() : orb_(cv::cuda::ORB::create()) {}

    cv::Ptr<cv::cuda::ORB> orb_;

    std::vector<cv::KeyPoint> keypoints_;

    cv::cuda::GpuMat gpu_src_;
    cv::cuda::GpuMat gpu_gray_;
    cv::cuda::GpuMat gpu_desc_;

    DISABLE_COPY(GpuORB)
};

#endif  // ORB_H_
