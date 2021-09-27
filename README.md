### 文档

+ version： 0.0.1
+ 引用依赖

````
 <dependency>
          <groupId>cn.itwoods</groupId>
          <artifactId>itwoods-spring-logback</artifactId>
          <version>latest</version>
 </dependency>
````

+ 配置logback-spring.xml

````
  <appender name="RABBIT_APPENDER" class="cn.itwoods.logback.rabbit.RabbitAppender">
      <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
          <charset>UTF-8</charset>
          <!-- <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level ${PID:-} -&#45;&#45; [%t] %logger{50} - %msg%n</pattern>-->
          <pattern>%msg%n</pattern>
      </encoder>
      <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
          <level>ERROR</level>
      </filter>
      <!-- rabbitmq exchange配置 -->
      <exchange>e.logs</exchange>
      <!-- rabbitmq routingKey配置 -->
      <routingKey>rk-logback</routingKey>
      <!-- rabbitmq 连接配置，其它属性参考org.springframework.boot.autoconfigure.amqp.RabbitProperties 默认属性-->
      <rabbitProperties>  
          <host>192.168.5.11</host>
          <port>5672</port>
          <username>user</username>
          <password>pwd</password>
          <virtualHost>/xxx</virtualHost>
      </rabbitProperties>
  </appender>
  
 <logger name="cn.itwoods" additivity="false">
    <level value="ERROR"/>
    <appender-ref ref="RABBIT_APPENDER"/>
</logger>
````
+ 特别说明:
> rabbitProperties：
    此配置原则上与spring cloud amqp配置一样，只不过这里是xml方式，其它理论上无异，作者只测试过单机rabbit，没有试过集群，大家可以试下，应该是可以的；