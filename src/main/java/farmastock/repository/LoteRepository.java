package farmastock.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import farmastock.model.Lote;

public interface LoteRepository extends JpaRepository<Lote, Long> {
    Optional<Lote> findByProductoIdProductoAndCodigo(Long idProducto, String codigo);
}
