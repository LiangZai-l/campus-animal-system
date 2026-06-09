package com.campus.animal.common;

/**
 * 统一响应状态码常量接口。
 * <p>
 * 将 HTTP 状态码集中定义为常量，方便全局统一引用，
 * 避免在各处硬编码数字，提高代码可读性和可维护性。
 * <p>
 * 使用接口而非类是因为接口字段默认为 public static final，
 * 天然适合常量定义场景。
 */
public interface ResultCode {

    /** 请求成功 */
    int SUCCESS = 200;

    /** 请求参数错误（校验失败、格式不对等） */
    int BAD_REQUEST = 400;

    /** 未认证（未登录或 token 失效） */
    int UNAUTHORIZED = 401;

    /** 权限不足（已登录但角色不匹配） */
    int FORBIDDEN = 403;

    /** 资源不存在 */
    int NOT_FOUND = 404;

    /** 数据冲突（如用户名已存在） */
    int CONFLICT = 409;

    /** 服务器内部错误（兜底异常） */
    int INTERNAL_ERROR = 500;
}
