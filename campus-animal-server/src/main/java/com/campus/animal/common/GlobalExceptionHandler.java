package com.campus.animal.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器，利用 Spring AOP 机制拦截所有 Controller 抛出的异常。
 * <p>
 * 核心注解：
 * <ul>
 *   <li>{@code @RestControllerAdvice}：组合了 @ControllerAdvice + @ResponseBody，
 *       使所有 @ExceptionHandler 方法的返回值自动序列化为 JSON</li>
 *   <li>{@code @ExceptionHandler}：声明该方法处理哪种异常类型</li>
 *   <li>{@code @ResponseStatus}：显式指定返回的 HTTP 状态码</li>
 * </ul>
 * <p>
 * 异常处理优先级：Spring 会匹配最具体的异常类型。例如：
 * 先匹配 BusinessException，匹配不到才走兜底的 Exception。
 * <p>
 * 设计要点：
 * <ul>
 *   <li>BusinessException 用 warn 级别（可预期的业务错误，不需完整堆栈）</li>
 *   <li>未知 Exception 用 error 级别（需要排查的 bug，打印完整堆栈）</li>
 *   <li>参数校验失败时，用 Stream 将多个字段的错误信息合并为一条消息</li>
 * </ul>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常。
     * 日志级别为 warn，因为这是可预期的业务校验失败，不需要完整堆栈。
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理 Spring Security 的权限不足异常。
     * 触发场景：{@code @PreAuthorize("hasRole('ADMIN')")} 校验失败。
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleAccessDenied(AccessDeniedException e) {
        return Result.error(ResultCode.FORBIDDEN, "权限不足");
    }

    /**
     * 处理 404 — 访问不存在的接口路径。
     * 需要配合 {@code spring.mvc.throw-exception-if-no-handler-found=true} 使用。
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Void> handleNoHandlerFound(NoHandlerFoundException e) {
        return Result.error(ResultCode.NOT_FOUND, "接口不存在: " + e.getRequestURL());
    }

    /**
     * 处理 {@code @Valid} 参数校验失败异常。
     * 遍历所有字段校验错误，用逗号拼接后返回给前端。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Result.error(ResultCode.BAD_REQUEST, message);
    }

    /**
     * 兜底异常处理 — 捕获所有未被上述处理器匹配的异常。
     * 日志级别为 error，打印完整堆栈，方便排查未知 bug。
     * 返回通用的"服务器内部错误"，不向前端暴露异常细节（安全性考虑）。
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常: ", e);
        return Result.error("服务器内部错误");
    }
}
