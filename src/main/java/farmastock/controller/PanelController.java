package farmastock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import farmastock.repository.LoteRepository;
import farmastock.repository.ProductoRepository;

@Controller
public class PanelController {

    private final ProductoRepository productoRepository;
    private final LoteRepository loteRepository;

    @Autowired
    public PanelController(ProductoRepository productoRepository, LoteRepository loteRepository) {
        this.productoRepository = productoRepository;
        this.loteRepository = loteRepository;
    }

    @GetMapping("/inicio")
    public String inicio(Model model) {
        model.addAttribute("totalProductos", productoRepository.count());
        model.addAttribute("totalLotes", loteRepository.count());
        return "panel/inicio";
    }
}