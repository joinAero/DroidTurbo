include_directories(
  ${CMAKE_CURRENT_SOURCE_DIR}
)
add_library(ocv_facedetect SHARED
  DetectionBasedTracker_jni.cpp
)
target_link_libraries(ocv_facedetect
  dl log OpenCV_CORE
)