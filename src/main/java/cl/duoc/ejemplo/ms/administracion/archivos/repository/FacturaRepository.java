package cl.duoc.ejemplo.ms.administracion.archivos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.ejemplo.ms.administracion.archivos.entity.Factura;

import java.util.List;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
    List<Factura> findByClienteId(String clienteId);
}
