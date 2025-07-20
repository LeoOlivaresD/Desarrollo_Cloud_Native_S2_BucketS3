package cl.duoc.ejemplo.ms.administracion.archivos.service.rabbitMq;

public interface ProducirMensajeService {

    void enviarMensaje(String mensaje);

    public void enviarObjeto(Object objeto);
}
