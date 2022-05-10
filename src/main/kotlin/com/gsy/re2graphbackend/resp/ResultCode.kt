package com.gsy.re2graphbackend.resp

import com.fasterxml.jackson.annotation.JsonFormat

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class ResultCode(
    val code: Int,
    val message: String
) {

    SUCCESS(0, "成功"),
    INTERNAL_ERROR(-1, "内部错误"),
    PARAM_ERROR(-2, "参数错误");
}