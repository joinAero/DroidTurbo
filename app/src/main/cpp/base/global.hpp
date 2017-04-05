/**
 * <p>Macros for different platforms:</p>
 * <ul>
 * <li>OS_ANDROID: Android
 * <li>OS_IPHONE: iOS
 * </ul>
 */
#ifndef GLOBAL_HPP_
#define GLOBAL_HPP_
#pragma once

// http://stackoverflow.com/questions/5919996/
// how-to-detect-reliably-mac-os-x-ios-linux-windows-in-c-preprocessor
#if defined(__APPLE__)
#  include "TargetConditionals.h"
#  if TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR
#    define OS_IPHONE
#  endif
#elif defined(__ANDROID__)
#  define OS_ANDROID
#endif

#define DISABLE_COPY(Class) \
    Class(const Class &) = delete; \
    Class &operator=(const Class &) = delete;

// http://stackoverflow.com/questions/15763937/unused-parameter-in-c11
template<typename ... T> void unused(T && ...) {}

#endif  // GLOBAL_HPP_
