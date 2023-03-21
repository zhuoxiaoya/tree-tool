package com.yhtx.tree.model;


import com.yhtx.tree.annotation.TreeField;
import com.yhtx.tree.utils.ReflectUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 树结构转换工具类
 */
public class TreeConvert<T> implements Serializable {

    /**
     * 转换树结构
     *
     * @param collection
     * @return
     */
    public static <T> List<T> convert(List<T> collection) {
        //如果集合为空 就直接返回空集合
        if(Objects.isNull(collection) || collection.isEmpty()){
            return Arrays.asList();
        }
        //检测排序对象是否包含指定的基本字段，并且返回map，用于构件树
        Map<TreeField.FieldType, Field> fieldMap = checkField(collection.get(0));
        //执行构件树操作
        return create(collection, fieldMap);
    }

    /**
     * 检测对象是否符合条件以及字段是否有对应的注解
     *
     * @param o
     */
    private static Map<TreeField.FieldType, Field> checkField(Object o) {
        Map<TreeField.FieldType, Field> fieldMap = new HashMap<>();
        if (o instanceof TreeNode) {
            Class<?> aClass = o.getClass();
            //获取所有的字段列
            Field[] fields = aClass.getDeclaredFields();
            Arrays.stream(fields).forEach(field -> {
                TreeField treeField = field.getAnnotation(TreeField.class);
                if (Objects.nonNull(treeField)) {
                    fieldMap.put(treeField.type(), field);
                }
            });
            //检测是否包含主节点
            Assert.notNull(fieldMap.get(TreeField.FieldType.ID), () -> new RuntimeException("树节点主节点标识未设置异常").getMessage());
            //检测是否包含父节点
            Assert.notNull(fieldMap.get(TreeField.FieldType.PARENT_ID), () -> new RuntimeException("树节点父级节点标识未设置异常").getMessage());
            //判断是否存在排序字段，有排序字段则需要校验排序的类型
            if(fieldMap.containsKey(TreeField.FieldType.SORT)){
                Assert.isTrue(Integer.class.equals(fieldMap.get(TreeField.FieldType.SORT).getType()), () -> new RuntimeException("排序字段仅支持Integer类型").getMessage());
            }
            return fieldMap;
        } else {
            throw new RuntimeException("树结构对象实例异常");
        }
    }

    /**
     * 查询的应用根据节点上下级创建为树
     * @param list
     * @return
     */
    private static <T> List<T> create(List<T> list,Map<TreeField.FieldType, Field> fieldMap) {
        Stream<T> stream = list.stream()
                .filter(item -> {
                    String pid = ReflectUtils.getFieldValue(item, fieldMap.get(TreeField.FieldType.PARENT_ID).getName()).toString();
                    return pid.equals("0");
                })
                .map(item -> {
                    ReflectUtils.setFieldValue(item,"childrenList",getChildren(item, list,fieldMap));
                    return item;
                });
        //判断是否需要排序
        if(fieldMap.containsKey(TreeField.FieldType.SORT)){
            //流使用一次后就会失效，此处相当于从新赋值一次，变成一个新的流
            stream = stream.sorted(
                    //顺序排序
                    Comparator.comparing(
                            //方法内部接收一个Function用于判断大小顺序，此处就返回指定的字段的值即可
                            getFunction(fieldMap.get(TreeField.FieldType.SORT))));
        }
        return stream.collect(Collectors.toList());
    }

    /**
     * 此方法将被递归调用
     * @param model
     * @param list
     * @param fieldMap
     * @param <T>
     * @return
     */
    private static <T> List<T> getChildren(T model, List<T> list,Map<TreeField.FieldType, Field> fieldMap) {
        Stream<T> stream = list.stream()
                .filter(item -> {
                    Object pid = ReflectUtils.getFieldValue(item, fieldMap.get(TreeField.FieldType.PARENT_ID).getName());
                    Object id = ReflectUtils.getFieldValue(model, fieldMap.get(TreeField.FieldType.ID).getName());
                    return pid.equals(id);
                })
                .map(item -> {
                    ReflectUtils.setFieldValue(item,"childrenList",getChildren(item,list,fieldMap));
                    return item;
                });
        //判断是否需要排序
        if(fieldMap.containsKey(TreeField.FieldType.SORT)){
            stream = stream.sorted(
                    //顺序排序
                    Comparator.comparing(
                            //方法内部接收一个Function用于判断大小顺序，此处就返回指定的字段的值即可
                            getFunction(fieldMap.get(TreeField.FieldType.SORT))));
        }
        return  stream.collect(Collectors.toList());
    }

    /**
     * 返回指定字段的function
     * @return
     */
    private static <T> Function getFunction(Field field) {
        return new Function<T,Integer>() {
            @Override
            public Integer apply(T t) {
                return ReflectUtils.getFieldValue(t,field.getName());
            }
        };
    }

}
