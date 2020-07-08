# microservice-sentinel

#### 简介

该项目分为两个服务，sentinel-service8101服务是对sentinel的基本使用，包括需要引入的依赖、需要的配置以及注解的使用；另一个服务sentinel-dashboard是控制台，在源码的基础上进行了改造，因为源码提供的控制台不能自动持久化，改造之后，只需要在控制台进行配置，会自动将配置同步到nacos中。

#### 项目搭建

打包sentinel-dashboard服务，nacos的地址默认为`localhost:8848`，可以在`application.properties`中配置

启动

```shell
nohup java -Dserver.port=8080 -jar sentinel-dashboard.jar > sentinel.log 2>&1 &
```

访问sentinel控制台：

http://localhost:8080/



sentinel客户端项目中引入依赖：

```xml
<dependency>
  <groupId>com.alibaba.cloud</groupId>
  <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
</dependency>
```

配置文件：

```properties
# sentinel dashboard 地址
spring.cloud.sentinel.transport.dashboard=localhost:8080
```

启动项目，访问controller

访问 http://localhost:8080/ 可以看到服务监控信息



#### **流控规则：**

- 资源名：唯一名称，默认请求路径

- 针对来源：Sentinel可以针对调用者进行限流，填写微服务名，默认default（不区分来源）

- 阈值类型/单机阈值：

  ​     QPS（每秒钟的请求数量）：当调用该api的QPS达到阈值时，进行限流

  ​     线程数：当调用该api的线程数达到阈值时，进行限流

- 是否需要集群：不需要集群

- 流控模式：

  ​     直接：api达到限流条件时，直接限流

  ​     关联：当关联的资源达到阈值时，就限流自己

  ​     链路：只记录链路上的流量（指定资源从入口资源进来的流量，如果达到阈值，就进行限流）「API级别的针对来源」

- 流控效果：

  ​     快速失败：直接失败，抛异常

  ​     Warm Up：根据coldFactor（冷加载因子，默认3）的值，从阈值/coldFactor，经过预热时长，才达到设置的QPS阈值

  ​     排队等待：匀速排队，让请求以匀速的速度通过，阈值类型必须设置为QPS，否则无效

#### **降级规则：**

- RT（平均响应时间，秒级）：

​         	平均响应时间 超出阈值 且 在时间窗口内通过的请求>=5，两个条件同时满足后触发降级

​         	窗口期过后关闭断路器

​         	RT最大4900（更大的需要通过-Dcsp.sntinel.statistic.max.rt=xxx才能生效）

- 异常比例（秒级）：

​         	QPS>=5且异常比例（秒级统计）超过阈值时，触发降级；时间窗口结束后，关闭降级。

- 异常数（分钟级）：

​         	异常数（分钟统计）超过阈值时，触发降级，时间窗口结束后，关闭降级。



#### 配置持久化

要想达到持久化的效果，还需要在sentinel客户端中加入以下配置，但是如果每个sentient客户端都加这些配置的话，就会显的很臃肿，所以我实现了一种使用代码替换这些固定配置的方式`com.sixj.sentinel.service8101.config.SentinelNacosConfig`，加了这个配置类之后就不需要下面这些配置了：

```properties
# 限流规则
spring.cloud.sentinel.datasource.flow.nacos.server-addr=${spring.cloud.nacos.discovery.server-addr}
spring.cloud.sentinel.datasource.flow.nacos.dataId=${spring.application.name}-flow-rules
spring.cloud.sentinel.datasource.flow.nacos.groupId=SENTINEL_GROUP
spring.cloud.sentinel.datasource.flow.nacos.rule-type=flow
spring.cloud.sentinel.datasource.flow.nacos.namespace=${spring.cloud.nacos.discovery.namespace}
# 降级规则
spring.cloud.sentinel.datasource.degrade.nacos.server-addr=${spring.cloud.nacos.discovery.server-addr}
spring.cloud.sentinel.datasource.degrade.nacos.dataId=${spring.application.name}-degrade-rules
spring.cloud.sentinel.datasource.degrade.nacos.groupId=SENTINEL_GROUP
spring.cloud.sentinel.datasource.degrade.nacos.rule-type=degrade
spring.cloud.sentinel.datasource.degrade.nacos.namespace=${spring.cloud.nacos.discovery.namespace}
# 系统规则
spring.cloud.sentinel.datasource.system.nacos.server-addr=${spring.cloud.nacos.discovery.server-addr}
spring.cloud.sentinel.datasource.system.nacos.dataId=${spring.application.name}-system-rules
spring.cloud.sentinel.datasource.system.nacos.groupId=SENTINEL_GROUP
spring.cloud.sentinel.datasource.system.nacos.rule-type=system
spring.cloud.sentinel.datasource.system.nacos.namespace=${spring.cloud.nacos.discovery.namespace}
# 授权规则
spring.cloud.sentinel.datasource.authority.nacos.server-addr=${spring.cloud.nacos.discovery.server-addr}
spring.cloud.sentinel.datasource.authority.nacos.dataId=${spring.application.name}-authority-rules
spring.cloud.sentinel.datasource.authority.nacos.groupId=SENTINEL_GROUP
spring.cloud.sentinel.datasource.authority.nacos.rule-type=authority
spring.cloud.sentinel.datasource.authority.nacos.namespace=${spring.cloud.nacos.discovery.namespace}
# 热点规则
spring.cloud.sentinel.datasource.param-flow.nacos.server-addr=${spring.cloud.nacos.discovery.server-addr}
spring.cloud.sentinel.datasource.param-flow.nacos.dataId=${spring.application.name}-param-flow-rules
spring.cloud.sentinel.datasource.param-flow.nacos.groupId=SENTINEL_GROUP
spring.cloud.sentinel.datasource.param-flow.nacos.rule-type=param_flow
spring.cloud.sentinel.datasource.param-flow.nacos.namespace=${spring.cloud.nacos.discovery.namespace}
```

