package com.yhtx.tree.model;

import lombok.Data;

import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * 树节点
 * <p>
 * 只增加children属性，不侵入多余字段（如排序、计数等）
 * 配合TreeConvert使用
 */
@Data
public abstract class TreeNode {
    /**
     * 子节点列表
     */
    @Transient
    private transient List<Object> childrenList = new ArrayList<>();
}
