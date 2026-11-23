
package farmastock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import farmastock.model.Farmacia;
import farmastock.service.FarmaciaService;

@Controller
@RequestMapping("/farmacias")
public class FarmaciaController {

    private final FarmaciaService farmaciaService;

    public FarmaciaController(FarmaciaService farmaciaService) {
        this.farmaciaService = farmaciaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("farmacias", farmaciaService.listar());
        return "farmacias/lista";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("farmacia", new Farmacia());
        return "farmacias/form";
    }

    @PostMapping("/guardar")
    public String guardar(Farmacia farmacia) {
        farmaciaService.guardar(farmacia);
        return "redirect:/farmacias";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Farmacia farmacia = farmaciaService.buscarPorId(id);

        if (farmacia == null) {
            return "redirect:/farmacias";
        }

        model.addAttribute("farmacia", farmacia);
        return "farmacias/form";
    }

    @GetMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id) {
        farmaciaService.cambiarEstado(id);
        return "redirect:/farmacias";
    }

    @GetMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        farmaciaService.eliminar(id);
        return "redirect:/farmacias";
    }
}
