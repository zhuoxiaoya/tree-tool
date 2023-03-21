package com.yhtx.tree.model;

import com.yhtx.tree.annotation.TreeField;

/**
 * 构建树结构测试类
 */
public class TestTree extends TreeNode{

    @TreeField(type = TreeField.FieldType.ID)
    private int id;

    @TreeField(type = TreeField.FieldType.PARENT_ID)
    private int pid;

    @TreeField(type = TreeField.FieldType.SORT)
    private Integer sort;

    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
