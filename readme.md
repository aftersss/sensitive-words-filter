# 敏感词过滤、脱敏

[TOC]

## 1、简介
`sensitive-words-filter` 可以过滤一些敏感的字符串，如：`色情`、`政治`、`暴力`、`人名`等特殊字符，防止发表一些不和谐的词条给系统带来运营成本。

目前`sensitive-words-filter`提供了如下几种算法脱敏支持：

* `TIRE` 算法表现良好，在内存占用和速度上比较均衡，适用情况比较广，比较实用；
* `HASH_BUCKET` 算法表现良好，在内存占用和速度上比较均衡；
* `DFA`算法用的内存最多，但速度最快.
* `DAT`算法用的内存最少，但速度最慢.
* `TTMP` 存在漏词的问题, 不建议使用。

以上每一种算法都有自己的特点，以供选择；

所有实现均是线程安全的。

## 2、使用方法

下面以`TIRE`算法实现类为例演示

### 2.1、初始化并添加敏感词
> put方法用于添加敏感词

```java
SensitiveWordsFilter filter = SensitiveWordsFilterFactory.newSensitiveWordsFilter(SensitiveWordsFilterFactory.SensitiveWordsFilterType.TIRE);
filter.put("美国人");
filter.put("美国男人");
filter.put("美国人民");
filter.put("人民");
filter.put("中间");
filter.put("女人");

filter.put("一举");
filter.put("一举成名");
filter.put("一举成名走四方");
filter.put("成名");
filter.put("走四方");
```

### 2.2、匹配敏感词

> 匹配敏感词会把符合的敏感词都找到，该方法有两个参数

+ **接口说明：**

```java
/**
* 返回匹配到的敏感词语
* @param partMatch 是否部分匹配; 比如content为"美国民",敏感词库中有两个敏感词:"美国","国民",则如果partMatch=true，匹配到的敏感词为：["美国"], 反之匹配到:["美国"，"国民"],也就是说partMatch=false会匹配到重叠的部分
* @param content 被匹配的语句
* @return 返回匹配的敏感词语集合
*/
public Set<String> getWords(boolean partMatch, String content);
```

+ **运行示例：**

```java
String content = "我们美国人都是好人，在他们中间有男人和女人。美国男人很高，美国人民经常吃饭。";
System.out.println(filter.getWords(true, content));
System.out.println(filter.getWords(false, content));
```

+ **运行结果：**

```
[美国人, 女人, 中间, 美国男人]
[美国人, 女人, 中间, 美国男人, 美国人民, 人民]
```
> 从上面结果发现部分匹配和完整匹配的结果集不一样，部分匹配是匹配到敏感词后立即退出当前匹配；而完整匹配会把所有词都匹配出来，也就是把敏感词中的小词也匹配到。

### 2.3、过滤敏感词

> 过滤敏感词主要是将匹配到的敏感词过滤掉，以某种字符串进行替换敏感词字符

+ **接口说明：**

```java
/**
* 过滤敏感词，并把敏感词替换为指定字符
* @param content 被匹配的语句
* @param replaceChar 替换字符
* @return 过滤后的字符串
*/
public String filter(String content, char replaceChar) ;
```

+ **运行示例：**

```java
System.out.println(filter.filter(content, '*'));
```

+ **运行结果：**
```
我们***都是好人，在他们**有男人和**。****很高，***民经常吃饭。
```

### 2.4、敏感词高亮

> 敏感词高亮就是将匹配到的敏感字符以HTML的tag进行替换，这样在前端显示的时候就会比较突出

+ **接口介绍：**

```java
/**
 * html高亮敏感词
 * @param content 被匹配的语句
 * @param template 敏感词替换模板，模板中必须包含一个%s，比如
 *                    <xmp>
 *                        <font color='red'>%s</font>
 *                    </xmp>
 * @return 返回html高亮敏感词
 */
public String highlight(String content, String template) ;
```

+ **运行示例：**
```java
System.out.println(filter.highlight(content, "<font color='red'>%s</font>"));
```

+ **运行结果：**
```html
我们<font color='red'>美国人</font>都是好人，在他们<font color='red'>中间</font>有男人和<font color='red'>女人</font>。<font color='red'>美国男人</font>很高，<font color='red'>美国人</font>民经常吃饭。
```

### 2.5、是否存在敏感词

> 判断一段文本是否包含敏感词，若包含立即返回true，否则 false

+ **接口说明：**

```java
/**
* 是否包含敏感字符
* @param content 被匹配内容
* @return 是否包含敏感字符
*/
public boolean contains(String content);
```

+ **运行示例：**

```java
System.out.println(filter.contains(true, content));
```

+ **运行结果：**

```
true
```

## 3、各算法内存占用测试

> 针对各算法进行测试，对每种算法分别放入23000条敏感词数据（共计283KB），观察堆内存使用。

+ **测试结果**

| 算法                                  |   内存消耗（MB）     |
| :--------------------------------------- | :-------------: |
| `DFA("dfa算法")`            | 11MB |
| `TIRE("tire树算法") `       | 4MB |
| `HASH_BUCKET("hash桶算法")` | 3.2MB |
| `DAT("双数组算法")`          | 1.9M  |
| `TTMP("ttmp算法")`          | 2MB  |
| `HashSet(每个敏感词简单放入HashSet中，作为对比)`  | 2MB  |

## 4、各算法性能测试说明

> 针对各算法进行测试,对每种算法分别放入23000条敏感词数据（共计283KB），然后分别测试 匹配文本 600字符、5000字符、20000字符时的性能(这些文本均不会命中敏感词)

+ **测试结果**

| 算法接口                                     |       过滤字符数       |  执行1000次的耗时(毫秒)   |    
| :---------------------------------- | :---------------: | :-------: |
| `DFA("dfa算法")`                           | 600/5000/20000 |  34/346/915  |  
| `TIRE("tire树算法")`                       | 600/5000/20000 | 148/2204/7728 | 
| `HASH_BUCKET("hash桶算法")`                 | 600/5000/20000 |  116/1362/5509  | 
| `DAT("双数组算法")`                          | 600/5000/20000 |  2094/16720/48896  | 
| `TTMP("ttmp算法")`                          | 600/5000/20000 |  44/602/1569  | 
| `HashSet(简单地一个个判断contains，作为比较)`   | 600/5000/20000 |  5952/39409/129772  | 

## 5、总结
+ `DFA`算法用的内存最多，但速度最快.
+ `DAT`算法用的内存最少，但速度最慢.
+ `HASH_BUCKET`算法表现良好，在内存占用和速度上比较均衡，比较实用，`TIRE`算法也不错;建议选用HASH_BUCKET或者TIRE算法.
+ `TTMP` 存在漏词的问题, 不建议使用。

> 请根据你的使用场景选择不同的算法
