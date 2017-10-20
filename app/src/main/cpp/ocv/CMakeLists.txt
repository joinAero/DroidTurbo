file(GLOB OCV_SRCS "*.cpp")
add_library(ocv_all SHARED
  ${OCV_SRCS}
)
target_link_libraries(ocv_all
  log OpenCV_CORE
)