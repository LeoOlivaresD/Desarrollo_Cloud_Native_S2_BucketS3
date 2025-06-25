package cl.duoc.ejemplo.ms.administracion.archivos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoFactura {
private String clienteId;
    private LocalDate fechaEmision;
    private String descripcion;
    private BigDecimal monto;
    private String nombreArchivo;
}
