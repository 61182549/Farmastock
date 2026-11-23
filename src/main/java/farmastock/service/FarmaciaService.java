
package farmastock.service;

import java.util.List;

import org.springframework.stereotype.Service;

import farmastock.model.Farmacia;
import farmastock.repository.FarmaciaRepository;

@Service
public class FarmaciaService {

    private final FarmaciaRepository farmaciaRepository;

    public FarmaciaService(FarmaciaRepository farmaciaRepository) {
        this.farmaciaRepository = farmaciaRepository;
    }

    public List<Farmacia> listar() {
        return farmaciaRepository.findAll();
    }

    public Farmacia guardar(Farmacia farmacia) {
        return farmaciaRepository.save(farmacia);
    }

    public Farmacia buscarPorId(Long id) {
        return farmaciaRepository.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        farmaciaRepository.deleteById(id);
    }

    public void cambiarEstado(Long id) {
        Farmacia farmacia = buscarPorId(id);

        if (farmacia != null) {
            farmacia.setEstado(
                "Activo".equals(farmacia.getEstado())
                ? "Inactivo"
                : "Activo"
            );

            farmaciaRepository.save(farmacia);
        }
    }
}
