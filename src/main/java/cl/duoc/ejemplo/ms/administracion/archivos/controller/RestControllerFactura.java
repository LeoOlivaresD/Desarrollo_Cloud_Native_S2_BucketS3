package cl.duoc.ejemplo.ms.administracion.archivos.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cl.duoc.ejemplo.ms.administracion.archivos.dto.DtoFactura;
import cl.duoc.ejemplo.ms.administracion.archivos.entity.Factura;
import cl.duoc.ejemplo.ms.administracion.archivos.service.FacturaService;

@RestController
@RequestMapping("/facturas")
public class RestControllerFactura {

    private final FacturaService facturaService;

    public RestControllerFactura(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    /*  Crea una nueva factura a partir de los datos enviados en el cuerpo de la petición.
    El DTO recibido se transforma en una entidad Factura antes de ser persistida.
    Retorna la factura creada con estado 200 OK */
    @PostMapping
    public ResponseEntity<Factura> crearFactura(@RequestBody DtoFactura facturaDto) {
        Factura factura = new Factura();
        factura.setClienteId(facturaDto.getClienteId());
        factura.setFechaEmision(facturaDto.getFechaEmision());
        factura.setDescripcion(facturaDto.getDescripcion());
        factura.setMonto(facturaDto.getMonto());
        factura.setNombreArchivo(facturaDto.getNombreArchivo());

        return ResponseEntity.ok(facturaService.crearFactura(factura));
    }

    /* Recupera una factura específica según su ID.
     Si la factura existe, retorna 200 OK con la entidad; de lo contrario, retorna 404 Not Found.*/
     @GetMapping("/{id}")
    public ResponseEntity<Factura> obtenerFactura(@PathVariable Long id) {
        Optional<Factura> factura = facturaService.obtenerFactura(id);
        return factura.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /*  Retorna el historial completo de facturas asociadas a un cliente según su ID.
    Responde con una lista de facturas y un estado 200 OK.*/
    @GetMapping("/historial/{clienteId}")
    public ResponseEntity<List<Factura>> obtenerHistorialPorCliente(@PathVariable String clienteId) {
        return ResponseEntity.ok(facturaService.obtenerHistorialCliente(clienteId));
    }

    /*Actualiza una factura existente con los datos proporcionados.
    Si no se encuentra la factura con el ID dado, responde con 404 Not Found.
    En caso contrario, actualiza los campos y retorna la factura modificada con estado 200 OK.*/
    @PutMapping("/{id}")
    public ResponseEntity<Factura> actualizarFactura(@PathVariable Long id, @RequestBody DtoFactura facturaDto) {
        Optional<Factura> existente = facturaService.obtenerFactura(id);
        if (existente.isEmpty()) return ResponseEntity.notFound().build();

        Factura factura = existente.get();
        factura.setClienteId(facturaDto.getClienteId());
        factura.setFechaEmision(facturaDto.getFechaEmision());
        factura.setDescripcion(facturaDto.getDescripcion());
        factura.setMonto(facturaDto.getMonto());
        factura.setNombreArchivo(facturaDto.getNombreArchivo());

        return ResponseEntity.ok(facturaService.actualizarFactura(factura));
    }

    /*Elimina la factura correspondiente al ID especificado.
     Siempre responde con 204 No Content, sin importar si la factura existía o no.*/
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarFactura(@PathVariable Long id) {
        facturaService.eliminarFactura(id);
        return ResponseEntity.noContent().build();
    }

    /*Permite subir un archivo asociado a una factura existente, utilizando su ID.
    El archivo es recibido como MultipartFile y procesado por el servicio correspondiente.
    Devuelve una respuesta 200 OK si la operación se realiza correctamente.*/
    @PostMapping("/{id}/upload")
    public ResponseEntity<String> subirFactura(@PathVariable Long id,
                                               @RequestParam("archivo") MultipartFile archivo) throws IOException {
        facturaService.subirYGuardarFactura(id, archivo);
        return ResponseEntity.ok("Archivo subido correctamente");
    }
}
