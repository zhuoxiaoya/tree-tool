# tree-tool

## 一.介绍
最容易上手的树形结构集合构建工具，不依赖其他外部jar包，完全轻量级，通过注解完成标记条件字段，静态转换类实现构建，完美优化你的项目，传入任何实体对象都可完成构建。


## 🤔 为什么要做 tree-tool ?
你是否在业务中遇到很多需要构建树形结构数据的情况，某个父级ID等于另一个ID，然后实现排序功能，例如菜单列表，例如部门列表一类，此工具类可以最简易的上手使用。

#### 软件架构
## 🛸 模块说明 | Module
软件架构说明
```lua
tree-tool
├── annotation -- 核心注解声明
├── model -- 放置核心实体类
└── utils -- 工具类,放字符处理，时间处理，反射处理工具类
```


## 二. 安装教程
1.只需要引入以下依赖包即可
``` xml
<dependency>
    <groupId>com.houlangmark</groupId>
    <artifactId>tree-tool</artifactId>
    <version>1.0.3</version>
</dependency>
```

## 三. 使用说明
1.核心顶级抽象类，需要继承此类，如果没有此类，无法利用反射进行设置子节点值
``` java
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
}
```
2.只需要在需要构建的集合对象上继承核心树节点对象，在集合内的对象上打上标记主节点和上级节点的注解，即可使用静态方法构建树集合。举个栗子🌰如下：
``` java
public class TestTree extends TreeNode{

    /**
     * 主节点字段需要打上ID，必须项
     */
    @TreeField(type = TreeField.FieldType.ID)
    private int id;

    /**
     * 上级节点字段需要打上PARENT_ID，必须项
     */
    @TreeField(type = TreeField.FieldType.PARENT_ID)
    private int pid;
    
    /**
     * 排序字段注解，非必须项，允许构建树结构的集合不排序，也支持排序
     */
    @TreeField(type = TreeField.FieldType.SORT)
    private Integer sort;

    private String name;
}
``` 

## 四. 调用示例
### 1.以下调用为举例，使用TestTree类测试
``` java
 public static void main(String[] args) throws InterruptedException {
        //构建几个符合树结构的树，必须有顶级节点和pid，pid可以不强制必须为0。
        //有顶级节点则渲染为树，没有顶级节点则渲染为森林
        List<TestTree> list = new ArrayList<>();
        list.add(new TestTree(6,1,2,"顶级节点"));
        list.add(new TestTree(1,0,1,"顶级节点"));
        list.add(new TestTree(4,2,1,"顶级节点"));
        list.add(new TestTree(2,0,2,"顶级节点"));
        list.add(new TestTree(5,1,2,"顶级节点"));
        list.add(new TestTree(3,1,1,"顶级节点"));
        //调用只需要使用TreeConvert类的静态方法convert即可
        List<TestTree> convert = TreeConvert.convert(list);
        System.out.println(convert);
    }
``` 
### 2.测试处理前集合数据样式为以下json
``` json
[
    {
        "childrenList": [],
        "id": 6,
        "pid": 1,
        "sort": 2,
        "name": "节点6"
    },
    {
        "childrenList": [],
        "id": 1,
        "pid": 0,
        "sort": 1,
        "name": "节点1"
    },
    {
        "childrenList": [],
        "id": 4,
        "pid": 2,
        "sort": 1,
        "name": "节点4"
    },
    {
        "childrenList": [],
        "id": 2,
        "pid": 0,
        "sort": 2,
        "name": "节点2"
    },
    {
        "childrenList": [],
        "id": 5,
        "pid": 1,
        "sort": 2,
        "name": "节点5"
    },
    {
        "childrenList": [],
        "id": 3,
        "pid": 1,
        "sort": 1,
        "name": "节点3"
    }
]
``` 
### 3.利用工具类
``` json
[
    {
        "childrenList": [
            {
                "childrenList": [],
                "id": 3,
                "pid": 1,
                "sort": 1,
                "name": "节点3"
            },
            {
                "childrenList": [],
                "id": 6,
                "pid": 1,
                "sort": 2,
                "name": "节点6"
            },
            {
                "childrenList": [],
                "id": 5,
                "pid": 1,
                "sort": 2,
                "name": "节点5"
            }
        ],
        "id": 1,
        "pid": 0,
        "sort": 1,
        "name": "节点1"
    },
    {
        "childrenList": [
            {
                "childrenList": [],
                "id": 4,
                "pid": 2,
                "sort": 1,
                "name": "节点4"
            }
        ],
        "id": 2,
        "pid": 0,
        "sort": 2,
        "name": "节点2"
    }
]
``` 


#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


### ⭐️ tree-tool 使用 MulanPSL2 协议，源代码完全开源，无商业限制。 开源不易如果喜欢请给作者 Star 鼓励 👇

---

<p align="center">
    <a href="https://github.com/zhuoxiaoya/tree-tool">Github 仓库</a> &nbsp; | &nbsp; 
    <a href="https://gitee.com/zxy130359/tree-tool">码云仓库</a> &nbsp; | &nbsp; 
</p>

---