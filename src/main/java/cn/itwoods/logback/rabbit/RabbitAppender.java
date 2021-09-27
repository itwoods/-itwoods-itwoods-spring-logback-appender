package cn.itwoods.logback.rabbit;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.PropertyMapper;

import java.time.Duration;

/**
 * @param <E>
 * @author itwoods.cn
 */
public class RabbitAppender<E extends ILoggingEvent> extends UnsynchronizedAppenderBase<E> {
    private RabbitTemplate rabbitTemplate;
    private String exchange;
    private String routingKey;
    private RabbitProperties rabbitProperties;


    @Override
    protected void append(E eventObject) {
        rabbitTemplate.convertAndSend(exchange
                , routingKey
                , eventObject.getMessage());
    }

    public void start() {
        try {
            this.rabbitTemplate = new RabbitTemplate(rabbitConnectionFactory(rabbitProperties));
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.start();
    }


    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public void setRabbitProperties(RabbitProperties rabbitProperties) {
        this.rabbitProperties = rabbitProperties;
    }

    public RabbitProperties getRabbitProperties() {
        return rabbitProperties;
    }


    private CachingConnectionFactory rabbitConnectionFactory(RabbitProperties properties) throws Exception {
        CachingConnectionFactory factory = new CachingConnectionFactory(getRabbitConnectionFactoryBean(properties).getObject());
        PropertyMapper map = PropertyMapper.get();
        map.from(properties::determineAddresses).to(factory::setAddresses);
        map.from(properties::getAddressShuffleMode).whenNonNull().to(factory::setAddressShuffleMode);
        map.from(properties::isPublisherReturns).to(factory::setPublisherReturns);
        map.from(properties::getPublisherConfirmType).whenNonNull().to(factory::setPublisherConfirmType);
        RabbitProperties.Cache.Channel channel = properties.getCache().getChannel();
        map.from(channel::getSize).whenNonNull().to(factory::setChannelCacheSize);
        map.from(channel::getCheckoutTimeout).whenNonNull().as(Duration::toMillis)
                .to(factory::setChannelCheckoutTimeout);
        RabbitProperties.Cache.Connection connection = properties.getCache().getConnection();
        map.from(connection::getMode).whenNonNull().to(factory::setCacheMode);
        map.from(connection::getSize).whenNonNull().to(factory::setConnectionCacheSize);
        return factory;
    }

    private RabbitConnectionFactoryBean getRabbitConnectionFactoryBean(RabbitProperties properties) {
        RabbitConnectionFactoryBean factory = new RabbitConnectionFactoryBean();
        PropertyMapper map = PropertyMapper.get();
        map.from(properties::determineHost).whenNonNull().to(factory::setHost);
        map.from(properties::determinePort).to(factory::setPort);
        map.from(properties::determineUsername).whenNonNull().to(factory::setUsername);
        map.from(properties::determinePassword).whenNonNull().to(factory::setPassword);
        map.from(properties::determineVirtualHost).whenNonNull().to(factory::setVirtualHost);
        map.from(properties::getRequestedHeartbeat).whenNonNull().asInt(Duration::getSeconds)
                .to(factory::setRequestedHeartbeat);
        map.from(properties::getRequestedChannelMax).to(factory::setRequestedChannelMax);
        RabbitProperties.Ssl ssl = properties.getSsl();
        if (ssl.determineEnabled()) {
            factory.setUseSSL(true);
            map.from(ssl::getAlgorithm).whenNonNull().to(factory::setSslAlgorithm);
            map.from(ssl::getKeyStoreType).to(factory::setKeyStoreType);
            map.from(ssl::getKeyStore).to(factory::setKeyStore);
            map.from(ssl::getKeyStorePassword).to(factory::setKeyStorePassphrase);
            map.from(ssl::getTrustStoreType).to(factory::setTrustStoreType);
            map.from(ssl::getTrustStore).to(factory::setTrustStore);
            map.from(ssl::getTrustStorePassword).to(factory::setTrustStorePassphrase);
            map.from(ssl::isValidateServerCertificate)
                    .to((validate) -> factory.setSkipServerCertificateValidation(!validate));
            map.from(ssl::getVerifyHostname).to(factory::setEnableHostnameVerification);
        }
        map.from(properties::getConnectionTimeout).whenNonNull().asInt(Duration::toMillis)
                .to(factory::setConnectionTimeout);
        map.from(properties::getChannelRpcTimeout).whenNonNull().asInt(Duration::toMillis)
                .to(factory::setChannelRpcTimeout);
        factory.afterPropertiesSet();
        return factory;
    }

}
