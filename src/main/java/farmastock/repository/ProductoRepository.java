package farmastock.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import farmastock.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}