package com.zn.servlet.annotation;

import java.lang.annotation.*;

/**
 * @author 张男
 * @date: 2024/3/12---16:53
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebListener {
}
