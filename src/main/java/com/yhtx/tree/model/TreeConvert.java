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
     * 用于处理树节点子节点渲染后的移除操作，保持树的顶级节点
     */
    private static List result = new ArrayList<>();

    /**
     * 转换树结构
     *
     * @param collection
     * @return
     */
    public synchronized static <T> List<T> convert(List<T> collection) {
        //执行操作前需要先把数据清理了
        result.clear();
        //如果集合为空 就直接返回空集合
        if(Objects.isNull(collection) || collection.isEmpty()){
            return Arrays.asList();
        }
        //检测排序对象是否包含指定的基本字段，并且返回map，用于构件树
        Map<TreeField.FieldType, Field> fieldMap = checkField(collection.get(0));
        //先拷贝一份数据至最外部，用于处理子节点渲染后从外部集合移除
        result.addAll(collection);
        //执行构件树操作
        List<T> tree = create(collection, fieldMap);
        //执行完操作后需要将集合置空
        result.clear();
        return tree;
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
        list.stream()
                .forEach(item -> {
                    //如果当前对象还存在静态量中就需要做递归处理
                    if (result.contains(item)) {
                        ReflectUtils.setFieldValue(item, "childrenList", getChildren(item, list, fieldMap));
                    }
                });
        Stream<T> stream = result.stream();
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
        //先把满足自己叶子条件的集合先过滤出来
        List<T> childrenList = list.stream()
                .filter(item -> {
                    Object pid = ReflectUtils.getFieldValue(item, fieldMap.get(TreeField.FieldType.PARENT_ID).getName());
                    Object id = ReflectUtils.getFieldValue(model, fieldMap.get(TreeField.FieldType.ID).getName());
                    return pid.equals(id);
                }).collect(Collectors.toList());
        //删除所有是子节点的节点数据
        result.removeAll(childrenList);
        //此处做处理把所有叶子节点也要全部循环处理一次把他们的叶子节点也处理了
        Stream<T> stream = childrenList.stream().map(item -> {
            ReflectUtils.setFieldValue(item, "childrenList", getChildren(item, list, fieldMap));
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
        return stream.collect(Collectors.toList());
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
