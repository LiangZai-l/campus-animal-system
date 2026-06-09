package com.campus.animal.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 统一响应体，所有 Controller 接口的返回值都包装为 Result&lt;T&gt;。
 * <p>
 * 设计目的：
 * <ul>
 *   <li>前端只需解析一种 JSON 结构：{@code {code, message, data}}</li>
 *   <li>通过 code 判断业务是否成功，而非仅依赖 HTTP 状态码</li>
 *   <li>data 使用泛型，可承载任意类型的业务数据</li>
 * </ul>
 * <p>
 * 使用示例：
 * <pre>{@code
 *   // 成功返回数据
 *   return Result.success(animalVO);
 *   // 成功无数据
 *   return Result.success();
 *   // 业务异常
 *   return Result.error(ResultCode.CONFLICT, "用户名已存在");
 * }</pre>
 *
 * @param <T> 业务数据的类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    /** 状态码，参考 {@link ResultCode} */
    private int code;

    /** 提示信息，用于前端展示 */
    private String message;

    /** 业务数据，可为 null */
    private T data;

    /**
     * 成功响应（带数据）。
     * @param data 业务数据
     * @param <T>  数据类型
     * @return 包含数据的成功 Result
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS, "操作成功", data);
    }

    /**
     * 成功响应（无数据），用于新增、删除、修改等无需返回数据的场景。
     * @param <T> 数据类型（此处为 Void）
     * @return 不含数据的成功 Result
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 失败响应（指定状态码和消息）。
     * @param code    业务状态码，参考 {@link ResultCode}
     * @param message 错误提示信息
     * @param <T>     数据类型
     * @return 错误 Result
     */
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 失败响应（默认 500 内部错误）。
     * @param message 错误提示信息
     * @param <T>     数据类型
     * @return 错误 Result
     */
    public static <T> Result<T> error(String message) {
        return error(ResultCode.INTERNAL_ERROR, message);
    }
}
