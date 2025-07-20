package cl.duoc.ejemplo.ms.administracion.archivos.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.ejemplo.ms.administracion.archivos.service.rabbitMq.ConsumirMensajeService;
import cl.duoc.ejemplo.ms.administracion.archivos.service.rabbitMq.ProducirMensajeService;

@RestController
@RequestMapping("/api")
public class MensajeController {

    private final ProducirMensajeService producirMensajeService;
    private final ConsumirMensajeService consumirMensajeService;

    public MensajeController(ProducirMensajeService mensajeService, ConsumirMensajeService consumirMensajeService) {

        this.producirMensajeService = mensajeService;
        this.consumirMensajeService = consumirMensajeService;
    }

    @PostMapping("/mensajes")
    public ResponseEntity<String> enviar(@RequestBody String mensaje) {

        producirMensajeService.enviarMensaje(mensaje);
        return ResponseEntity.ok("Mensaje enviado: " + mensaje);
    }

    @GetMapping("/mensaje/ultimo")
    public ResponseEntity<String> obtenerUltimoMensaje() {

        String message = consumirMensajeService.obtenerUltimoMensaje();

        if (null == message) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(message);
        }
    }
}
