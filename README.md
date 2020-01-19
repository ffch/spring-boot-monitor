[![License](http://img.shields.io/:license-apache-blue.svg "2.0")](http://www.apache.org/licenses/LICENSE-2.0.html)
[![JDK 1.8](https://img.shields.io/badge/JDK-1.8-green.svg "JDK 1.8")]()
[![Maven Central](https://img.shields.io/maven-central/v/cn.pomit/spring-boot-monitor.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22cn.pomit%22%20AND%20a:%22spring-boot-monitor%22)

## Spring-boot-monitor项目简介

在使用Spring Boot Actuator的时候，你是否想要一套界面来方便查看应用的指标呢？

在你搜索相关的ui的时候，是不是发现Spring boot Admin这个监控工具特别火呢？

Spring boot Admin的ui是真的好看，可是它却令人又爱又恨，Server ?!

是的，它必须要求你重新部署一个应用，来做Admin Server，被监控的机器做Admin Client！简直遭罪啊，如果我只想要一个鸡腿，你却给我送个鸡爪子，不知道我不爱吃鸡爪么？我得部署个Server，而且指标数据还得从client传到server，再由Server传给前端，中间得网络开销也不小（metrics接口得数据特别大，可能查询这个接口要几秒）。

所以，我就想如何把Spring boot Admin部署到单机，或许有人说，可以把Server和Client都整合到一个应用里。累不累！数据要在localhost里转一圈，还特别浪费资源，我只想要一套界面而已！

这时候Spring Boot Monitor这个工具就应运而生，它把Spring boot Admin的界面拿了出来，并修改了数据来源，直接从Actuator拿数据，就是这么简单！对代码无任何侵入！和Spring boot Admin的功能一模一样。

## [Gitee](https://gitee.com/ffch/SpringBootMonitor)
## [Github](https://github.com/ffch/spring-boot-monitor)
## [Get Started](https://www.pomit.cn/SpringBootMonitor/)

## 主要功能

v0.0.1：
 1. 单机监控SpringBoot应用指标；
 2. 无需额外配置；
 3. 将前端资源归纳到/monitor路径中，隔离其他资源；
 4. 去掉了Spring boot Admin的Server；
 5. 去掉了Spring boot Admin对thymyleaf的依赖；
 6. 去掉了Spring boot Admin的event流。

v0.0.2：
 1. 增加events接口，显示journal信息。

## 使用说明

jar包已经上传到maven中央仓库。
https://search.maven.org/search?q=spring-boot-monitor ，groupId为cn.pomit。

[使用文档地址](https://www.pomit.cn/SpringBootMonitor)

### maven依赖

```xml
<dependency>
	<groupId>cn.pomit</groupId>
	<artifactId>spring-boot-monitor</artifactId>
	<version>0.0.2</version>
</dependency>
```

### 启动

引入依赖即可。使用AutoConfiguration自动加载spring-boot-monitor相关配置。

### 配置actuator
同样，使用actuator还需加上actuator的配置，开放endpoints。

```
management.endpoints.web.exposure.include=*
```

### 访问方式

如果当前的应用地址为http://127.0.0.1:8080, spring-boot-monitor的访问地址为：http://127.0.0.1:8080/monitor。

其他操作则是前端页面操作。和spring-boot-admin完全一样。

## [Get-Started](https://www.pomit.cn/SpringBootMonitor)

## 版权声明
spring-boot-monitor使用 Apache License 2.0 协议.

## 作者信息
      
   作者博客：https://blog.csdn.net/feiyangtianyao
  
  个人网站：https://www.pomit.cn
 
   作者邮箱： fufeixiaoyu@163.com

## License
Apache License V2

