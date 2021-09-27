### itwoods-spring-logback-appender
+ Info：
> logback日志采集传输，使用rabbitmq为传输通道，可结合其它第3方日志处理协同使用，如：es、logstash……，使用方式与原生logback无异，无代码侵入。
+ Version： 0.0.1
+ Dependencies：

````
 <dependency>
    <groupId>cn.itwoods</groupId>
    <artifactId>itwoods-spring-logback</artifactId>
    <version>latest</version>
 </dependency>
 <dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
````

+ Setting logback-spring.xml：

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
+ Notes:
> rabbitProperties：
    此配置原则上与spring cloud amqp配置一样，只不过这里是xml方式，其它理论上无异，作者只测试过单机rabbit，没有试过集群，大家可以试下，应该是可以的；