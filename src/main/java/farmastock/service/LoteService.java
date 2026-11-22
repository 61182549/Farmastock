package farmastock.service;

import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import farmastock.model.Lote;
import farmastock.model.Producto;
import farmastock.repository.LoteRepository;
import farmastock.repository.ProductoRepository;

@Service
@Validated
public class LoteService {
    private final LoteRepository loteRepository;
    private final ProductoRepository productoRepository;

    public LoteService(LoteRepository loteRepository, ProductoRepository productoRepository) {
        this.loteRepository = loteRepository;
        this.productoRepository = productoRepository;
    }

    public List<Lote> listar() {
        return loteRepository.findAll(Sort.by("fechaVencimiento").ascending().and(Sort.by("idLote")));
    }

    public Lote buscarPorId(Long id) {
        return loteRepository.findById(id).orElse(null);
    }

    @Transactional
    public Lote guardar(@Valid Lote lote) {
        if (lote.getIdLote() != null && !loteRepository.existsById(lote.getIdLote())) {
            throw new IllegalArgumentException("El lote ya no existe");
        }
        Long idProducto = lote.getProducto().getIdProducto();
        Producto producto = idProducto == null ? null : productoRepository.findById(idProducto).orElse(null);
        if (producto == null) {
            throw new IllegalArgumentException("Selecciona un producto existente");
        }
        String codigo = lote.getCodigo().trim();
        loteRepository.findByProductoIdProductoAndCodigo(idProducto, codigo).ifPresent(existente -> {
            if (!Objects.equals(existente.getIdLote(), lote.getIdLote())) {
                throw new IllegalArgumentException("Ya existe ese código de lote para el producto");
            }
        });
        lote.setCodigo(codigo);
        lote.setProducto(producto);
        return loteRepository.saveAndFlush(lote);
    }

    @Transactional
    public void eliminar(Long id) {
        loteRepository.findById(id).ifPresent(loteRepository::delete);
    }
}
