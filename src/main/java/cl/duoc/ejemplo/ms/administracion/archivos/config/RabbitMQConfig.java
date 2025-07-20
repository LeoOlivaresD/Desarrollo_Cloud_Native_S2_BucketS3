package cl.duoc.ejemplo.ms.administracion.archivos.config;

import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String MAIN_QUEUE = "myQueue";
	public static final String DLX_QUEUE = "dlx-queue";
	public static final String MAIN_EXCHANGE = "myExchange";
	public static final String DLX_EXCHANGE = "dlx-exchange";
	public static final String DLX_ROUTING_KEY = "dlx-routing-key";

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

		return new Queue(MAIN_QUEUE, true, false, false,
				Map.of("x-dead-letter-exchange", DLX_EXCHANGE, "x-dead-letter-routing-key", DLX_ROUTING_KEY));
	}

	@Bean
	Queue dlxQueue() {

		return new Queue(DLX_QUEUE);
	}

	@Bean
	DirectExchange myExchange() {

		return new DirectExchange(MAIN_EXCHANGE);
	}

	@Bean
	DirectExchange dlxExchange() {

		return new DirectExchange(DLX_EXCHANGE);
	}

	@Bean
	Binding binding(Queue myQueue, DirectExchange myExchange) {

		return BindingBuilder.bind(myQueue).to(myExchange).with("");
	}

	@Bean
	Binding dlxBinding() {

		return BindingBuilder.bind(dlxQueue()).to(dlxExchange()).with(DLX_ROUTING_KEY);
	}

    /*
    public static final String QUEUE1 = "queue1";
    public static final String QUEUE2 = "dlxQueue";
    public static final String MAIN_EXCHANGE = "mainExchange";
    public static final String DLX_EXCHANGE = "dlx-exchange_errors";

    public static final String MAIN_QUEUE = null;
    public static final String DLX_ROUTING_KEY = "dlx-routing-key_errors";
    
    
     //Conversor JSON para mensajes
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
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(messageConverter());
		return template;
	}

    @Bean
    Queue queue1() {

        return new Queue(QUEUE1, true, false, false,
                Map.of("x-dead-letter-exchange", DLX_EXCHANGE, "x-dead-letter-routing-key", DLX_ROUTING_KEY));
    }

    @Bean
	Queue dlxQueue() {
		return new Queue(QUEUE2);
	}

    @Bean
	DirectExchange mainExchange() {
		return new DirectExchange(MAIN_EXCHANGE);
	}

    @Bean
	DirectExchange dlxExchange() {
		return new DirectExchange(DLX_EXCHANGE);
	}
    
    @Bean
	Binding bindingQueue1() {
		return BindingBuilder.bind(queue1()).to(mainExchange()).with("routingKeyQueue1");
	}
    @Bean
	Binding bindingCola2() {
		return BindingBuilder.bind(dlxQueue()).to(dlxExchange()).with(DLX_ROUTING_KEY);
	}
        */
}
