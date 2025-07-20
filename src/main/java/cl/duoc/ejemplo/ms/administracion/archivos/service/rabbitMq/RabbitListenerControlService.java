package cl.duoc.ejemplo.ms.administracion.archivos.service.rabbitMq;

public interface RabbitListenerControlService {
    
    void pausarListener(String id);

	void reanudarListener(String id);

	boolean isListenerRunning(String id);
}
