package cl.duoc.ejemplo.ms.administracion.archivos.service.rabbitMq;

import cl.duoc.ejemplo.ms.administracion.archivos.dto.BindingDTO;

public interface AdminRabbitService {
    
    public void crearCola(String nombreCola);

	public void crearExchange(String nombreExchange);

	public void crearBinding(BindingDTO request);

	public void eliminarCola(String nombreCola);

	public void eliminarExchange(String nombreExchange);
}
