add_library(ocv_mixed_sample SHARED
  jni_part.cpp
)
target_link_libraries(ocv_mixed_sample
  dl log opencv_java
)