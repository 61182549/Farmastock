package farmastock.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import farmastock.model.Lote;
import farmastock.service.LoteService;
import farmastock.service.ProductoService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/inventario")
public class LoteController {
    private final LoteService loteService;
    private final ProductoService productoService;

    public LoteController(LoteService loteService, ProductoService productoService) {
        this.loteService = loteService;
        this.productoService = productoService;
    }

    @InitBinder("lote")
    public void configurarCampos(WebDataBinder binder) {
        binder.setAllowedFields("idLote", "producto.idProducto", "codigo", "fechaVencimiento", "cantidad");
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("lotes", loteService.listar());
        return "inventario/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("lote", new Lote());
        return formulario(model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Lote lote = loteService.buscarPorId(id);
        if (lote == null) return "redirect:/inventario";
        model.addAttribute("lote", lote);
        return formulario(model);
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("lote") Lote lote, BindingResult errores, Model model) {
        if (errores.hasErrors()) return formulario(model);
        try {
            loteService.guardar(lote);
        } catch (IllegalArgumentException ex) {
            errores.reject("lote.invalido", ex.getMessage());
            return formulario(model);
        } catch (DataIntegrityViolationException ex) {
            errores.reject("lote.conflicto", "No se pudo guardar. Verifica el producto y que el código de lote no esté repetido.");
            return formulario(model);
        }
        return "redirect:/inventario";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        loteService.eliminar(id);
        return "redirect:/inventario";
    }

    private String formulario(Model model) {
        model.addAttribute("productos", productoService.listar());
        return "inventario/form";
    }
}
