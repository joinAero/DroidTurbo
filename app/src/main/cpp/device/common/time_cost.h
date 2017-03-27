#ifndef TIME_COST_H_
#define TIME_COST_H_
#pragma once

#include <cmath>
#include <ctime>
#include <chrono>
#include <functional>
#include <iomanip>
#include <sstream>
#include <stdexcept>
#include <tr1/unordered_map>

#include "jni_helper.h"

using system_clock = std::chrono::system_clock;

//using ToTMFunc = std::function<std::tm *(const std::time_t *)>;

inline system_clock::time_point now() {
    return system_clock::now();
}

template<typename Duration>
inline system_clock::time_point to_time_point(const Duration &d) {
    return system_clock::time_point(d);
}

template<typename Duration>
inline Duration cast(const system_clock::duration &d) {
    return std::chrono::duration_cast<Duration>(d);
}

template<typename Duration>
inline system_clock::time_point cast(const system_clock::time_point &d) {
    // C++17, floor
    return std::chrono::time_point_cast<Duration>(d);
}

template<typename Duration>
inline system_clock::duration cast_mod(const system_clock::time_point &t) {
    return t - cast<Duration>(t);
}

template<typename Duration>
inline int64_t count(const system_clock::duration &d) {
    return cast<Duration>(d).count();
}

inline std::string to_string(const system_clock::time_point &t,
        const char *fmt = "%F %T", int precision = 6) {
    auto t_c = system_clock::to_time_t(t);
    std::stringstream ss;
    char foo[20];
    strftime(foo, sizeof(foo), fmt, std::localtime(&t_c));
    ss << foo;
    if (precision > 0) {
        if (precision > 6) precision = 6;
        ss << '.' << std::setfill('0') << std::setw(precision) <<
        cast_mod<std::chrono::seconds>(t).count() / std::pow(10, 6-precision);
    }
    return ss.str();
}

// std::string to_string(const system_clock::duration &d) {
//     std::stringstream ss;
//     ss << (count<std::chrono::microseconds>(d) * 0.001f) << " ms";
//     return ss.str();
// }

class TimeCost {
public:
    using tag_map_t = std::tr1::unordered_map<std::string, TimeCost>;

    explicit TimeCost(const std::string &tag) : tag_(tag) {}
    ~TimeCost() {}

    std::ostream &ToString(std::ostream &os) const {
        float ms = count<std::chrono::microseconds>(elapsed()) * 0.001f;
        os << tag_ << std::endl
            << "BEG: " << to_string(beg_) << std::endl
            << "END: " << to_string(end_) << std::endl
            << "COST: " << ms << "ms";
        return os;
    }

    std::ostream &ToLineString(std::ostream &os) const {
        float ms = count<std::chrono::microseconds>(elapsed()) * 0.001f;
        os << tag_ << ": " << ms << "ms, "
            << to_string(beg_, "%T") << " > " << to_string(end_, "%T");
        return os;
    }

    std::string ToString() const {
        std::stringstream ss;
        ToString(ss);
        return ss.str();
    }

    std::string ToLineString() const {
        std::stringstream ss;
        ToLineString(ss);
        return ss.str();
    }

    std::string tag() const { return tag_; }
    system_clock::time_point beg() const { return beg_; }
    system_clock::time_point end() const { return end_; }
    system_clock::duration elapsed() const { return end_ - beg_; }

    void set_beg(const system_clock::time_point &t) { beg_ = t; }
    void set_end(const system_clock::time_point &t) { end_ = t; }

    static TimeCost &Beg(const std::string &tag) {
        TimeCost cost(tag);
        cost.beg_ = now();
        tag_map_t &map = GetTagMap();
        auto it = map.insert({tag, cost});
        if (!it.second)
            throw std::logic_error("This tag already in use");
        return it.first->second;
    }

    static TimeCost &End(const std::string &tag) {
        tag_map_t &map = GetTagMap();
        auto it = map.find(tag);
        if (it == map.end())
            throw std::logic_error("This tag not Beg before End");
        TimeCost &cost = it->second;
        cost.end_ = now();
        return cost;
    }

    static tag_map_t &GetTagMap() {
        static tag_map_t map;
        return map;
    }

private:
    std::string tag_;
    system_clock::time_point beg_;
    system_clock::time_point end_;
};

#ifdef TIME_COST
  #define TIME_BEG(tag) TimeCost::Beg(tag)
  #define TIME_END(tag) LOGI(TimeCost::End(tag).ToLineString().c_str())
  #define FUNC_TIME_BEG(tag) do { \
    char buff[256] = {'\0'}; \
    sprintf(buff, "%s::%s", tag, __func__); \
    TIME_BEG(buff); \
  } while (0)
  #define FUNC_TIME_END(tag) do { \
    char buff[256] = {'\0'}; \
    sprintf(buff, "%s::%s", tag, __func__); \
    TIME_END(buff); \
  } while (0)
#else
  #define TIME_BEG(tag)
  #define TIME_END(tag)
  #define FUNC_TIME_BEG(tag)
  #define FUNC_TIME_END(tag)
#endif

#endif  // TIME_COST_H_
