package com.campus.animal.common;

import lombok.Getter;

/**
 * 自定义业务异常，用于在 Service 层抛出可预期的业务错误。
 * <p>
 * 与普通 RuntimeException 的区别：
 * <ul>
 *   <li>携带业务状态码（code），便于 GlobalExceptionHandler 精确返回</li>
 *   <li>不会被打印完整堆栈（GlobalExceptionHandler 中仅 warn 级别记录）</li>
 *   <li>语义更明确：看到 BusinessException 就知道是业务规则校验不通过</li>
 * </ul>
 * <p>
 * 使用场景：用户名重复、密码错误、资源不存在、权限不足等业务校验失败。
 * <p>
 * 使用示例：
 * <pre>{@code
 *   throw new BusinessException(ResultCode.CONFLICT, "用户名已存在");
 *   throw new BusinessException("服务器错误"); // 默认 code=500
 * }</pre>
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 业务状态码，参考 {@link ResultCode} */
    private final int code;

    /**
     * 创建带指定状态码的业务异常。
     * @param code    业务状态码
     * @param message 错误描述
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 创建默认 500 错误的业务异常。
     * @param message 错误描述
     */
    public BusinessException(String message) {
        this(ResultCode.INTERNAL_ERROR, message);
    }
}
