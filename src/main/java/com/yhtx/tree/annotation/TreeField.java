package com.yhtx.tree.annotation;

import java.lang.annotation.*;

/**
 * 树节点排序注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TreeField {

    /**
     * 字段类型
     */
    FieldType type();

    /**
     * 字段类型
     */
    enum FieldType {
        ID,
        PARENT_ID,
        SORT,
        COUNT
    }
}
