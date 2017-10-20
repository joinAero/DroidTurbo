include_directories(
  ${CMAKE_CURRENT_SOURCE_DIR}
)
add_library(jni_utils SHARED
  jni_main.cpp
  jni_utils.cpp
)
target_link_libraries(jni_utils
  log jnigraphics
)