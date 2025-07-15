package cl.duoc.ejemplo.ms.administracion.archivos.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String MAIN_QUEUE = "myQueue";
    public static final String MAIN_EXCHANGE = "myExchange";

    @Bean
    Jackson2JsonMessageConverter messageConverter() {

        return new Jackson2JsonMessageConverter();
    }

    @Bean
    CachingConnectionFactory connectionFactory() {

        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost("54.158.115.212");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        return factory;
    }

    @Bean
    Queue myQueue() {

        return new Queue(MAIN_QUEUE, true, false, false, null);
    }

    @Bean
    DirectExchange myExchange() {

        return new DirectExchange(MAIN_EXCHANGE);
    }

    @Bean
    Binding binding(Queue myQueue, DirectExchange myExchange) {

        return BindingBuilder.bind(myQueue).to(myExchange).with("");
    }
}
