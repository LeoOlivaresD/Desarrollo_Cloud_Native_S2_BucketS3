package cl.duoc.ejemplo.ms.administracion.archivos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BindingDTO {

	private String nombreCola;
	private String nombreExchange;
	private String routingKey;
}
