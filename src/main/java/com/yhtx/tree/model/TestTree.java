package com.yhtx.tree.model;

import com.yhtx.tree.annotation.TreeField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestTree extends TreeNode{

    @TreeField(type = TreeField.FieldType.ID)
    private int id;

    @TreeField(type = TreeField.FieldType.PARENT_ID)
    private int pid;

    @TreeField(type = TreeField.FieldType.SORT)
    private Integer sort;

    private String name;
}
