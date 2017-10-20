add_library(ocv_facedetect SHARED
  DetectionBasedTracker_jni.cpp
)
target_link_libraries(ocv_facedetect
  dl log opencv_java
)