## Tavern
========
根据jar包进行Web项目模块化与集成的工具。

## 目标：

为常用开源组件提供模块化支持。

## Spring

为不同jar包提供不同ApplicationContext上下文，并支持双亲委托机制。从而使不同jar包的bean不再冲突！

## Struts

TODO

1. ### 为不同jar包提供名空间冲突检查。
2. ### 提供单Action内URL路由功能

## Freemarker

为freemarker提供自定义ftl路径功能。

## iBatis

TODO

提供数据源复用支持。

=========

## 使用：

在jar包`src/main/resource`目录配置`app.xml`文件：

```xml
	<?xml version="1.0" encoding="UTF-8"?>
	<Application>
	    <name>tavern</name>
	    <parent></parent>
	    <contextPath>classpath*:spring/appcontext-*.xml</contextPath>
	    <packageBase>com.dianping.tavern</packageBase>
	</Application>
```
	
ftl请放在`src/main/resources/ftl/`目录下。
	
在`web.xml`中配置:

    <listener>
        <listener-class>com.dianping.tavern.web.TavernContextLoaderListener</listener-class>
    </listener>