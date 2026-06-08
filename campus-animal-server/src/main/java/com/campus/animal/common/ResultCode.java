package com.campus.animal.common;

public interface ResultCode {

    int SUCCESS = 200;
    int BAD_REQUEST = 400;
    int UNAUTHORIZED = 401;
    int FORBIDDEN = 403;
    int NOT_FOUND = 404;
    int CONFLICT = 409;
    int INTERNAL_ERROR = 500;
}
