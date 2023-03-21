package com.yhtx.tree.model;


import java.util.ArrayList;
import java.util.List;

/**
 * 树节点
 * <p>
 * 只增加children属性，不侵入多余字段（如排序、计数等）
 * 配合TreeConvert使用
 */
public abstract class TreeNode {

    /**
     * 子节点列表
     */
    private transient List<Object> childrenList = new ArrayList<>();

    public List<Object> getChildrenList() {
        return childrenList;
    }

    public void setChildrenList(List<Object> childrenList) {
        this.childrenList = childrenList;
    }
}
