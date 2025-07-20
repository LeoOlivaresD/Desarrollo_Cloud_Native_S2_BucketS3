package cl.duoc.ejemplo.ms.administracion.archivos.service.rabbitMq.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.ejemplo.ms.administracion.archivos.config.RabbitMQConfig;
import cl.duoc.ejemplo.ms.administracion.archivos.service.rabbitMq.ProducirMensajeService;

@Service
public class ProducirMensajeServiceImpl implements ProducirMensajeService {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public ProducirMensajeServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void enviarMensaje(String mensaje) {

        rabbitTemplate.convertAndSend(RabbitMQConfig.MAIN_QUEUE, mensaje);
    }

    @Override
    public void enviarObjeto(Object objeto) {

        rabbitTemplate.convertAndSend(RabbitMQConfig.MAIN_QUEUE, objeto);
    }
}
