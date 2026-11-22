package farmastock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import farmastock.model.Lote;
import farmastock.model.Producto;
import farmastock.repository.LoteRepository;
import farmastock.repository.ProductoRepository;

@SpringBootTest
@AutoConfigureMockMvc
class InventarioIntegrationTests {
    @Autowired MockMvc mvc;
    @Autowired LoteRepository lotes;
    @Autowired ProductoRepository productos;
    private Producto producto;

    @BeforeEach
    void preparar() {
        lotes.deleteAll();
        productos.deleteAll();
        producto = new Producto();
        producto.setCodigo("P001");
        producto.setNombre("Paracetamol 500 mg");
        producto.setEstado("Activo");
        producto = productos.saveAndFlush(producto);
    }

    @Test
    void crearListarEditarEliminarYRenderizar() throws Exception {
        mvc.perform(get("/inventario")).andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("No hay lotes registrados")));
        guardar("LOTE-001", "40", "2027-12-31", producto.getIdProducto().toString())
            .andExpect(redirectedUrl("/inventario"));
        Lote lote = lotes.findAll().getFirst();
        assertThat(lote.getProducto().getIdProducto()).isEqualTo(producto.getIdProducto());
        String lista = mvc.perform(get("/inventario")).andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("LOTE-001")))
            .andReturn().getResponse().getContentAsString();
        String form = mvc.perform(get("/inventario/{id}/editar", lote.getIdLote())).andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("2027-12-31")))
            .andReturn().getResponse().getContentAsString();
        Files.createDirectories(Path.of("target/evidencia"));
        Files.writeString(Path.of("target/evidencia/lista.html"), lista);
        Files.writeString(Path.of("target/evidencia/form.html"), form);
        mvc.perform(post("/inventario/guardar").param("idLote", lote.getIdLote().toString())
            .param("producto.idProducto", producto.getIdProducto().toString()).param("codigo", "LOTE-001")
            .param("cantidad", "25").param("fechaVencimiento", "2027-12-31"))
            .andExpect(redirectedUrl("/inventario"));
        assertThat(lotes.findById(lote.getIdLote()).orElseThrow().getCantidad()).isEqualTo(25);
        mvc.perform(post("/inventario/{id}/eliminar", lote.getIdLote())).andExpect(redirectedUrl("/inventario"));
        assertThat(lotes.count()).isZero();
        assertThat(productos.existsById(producto.getIdProducto())).isTrue();
    }

    @Test
    void rechazaDatosInvalidosSinPersistir() throws Exception {
        guardar(" ", "-1", "", producto.getIdProducto().toString())
            .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("lote", "codigo", "cantidad", "fechaVencimiento"));
        guardar("L1", "abc", "fecha", producto.getIdProducto().toString())
            .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("lote", "cantidad", "fechaVencimiento"));
        guardar("L1", "1", "2027-12-31", "999999")
            .andExpect(status().isOk()).andExpect(model().attributeHasErrors("lote"));
        guardar("L1", "1", "2027-12-31", "")
            .andExpect(status().isOk()).andExpect(model().attributeHasErrors("lote"));
        assertThat(lotes.count()).isZero();
    }

    @Test
    void rechazaDuplicadoPeroPermiteMismoCodigoParaOtroProducto() throws Exception {
        guardar("L1", "0", "2027-12-31", producto.getIdProducto().toString()).andExpect(status().is3xxRedirection());
        guardar(" L1 ", "5", "2027-12-31", producto.getIdProducto().toString())
            .andExpect(status().isOk()).andExpect(model().attributeHasErrors("lote"));
        Producto otro = new Producto();
        otro.setNombre("Ibuprofeno");
        otro = productos.saveAndFlush(otro);
        guardar("L1", "5", "2027-12-31", otro.getIdProducto().toString()).andExpect(status().is3xxRedirection());
        assertThat(lotes.count()).isEqualTo(2);
    }

    @Test
    void conservaVencidosYOrdenaPorVencimiento() throws Exception {
        guardar("FUTURO", "3", LocalDate.now().plusDays(30).toString(), producto.getIdProducto().toString());
        guardar("VENCIDO", "2", LocalDate.now().minusDays(1).toString(), producto.getIdProducto().toString());
        String html = mvc.perform(get("/inventario")).andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Vencido")))
            .andReturn().getResponse().getContentAsString();
        assertThat(html.indexOf("VENCIDO")).isLessThan(html.indexOf("FUTURO"));
    }

    @Test
    void manejaLoteInexistenteYFormularioSinProductos() throws Exception {
        mvc.perform(get("/inventario/999999/editar")).andExpect(redirectedUrl("/inventario"));
        mvc.perform(post("/inventario/guardar").param("idLote", "999999")
            .param("producto.idProducto", producto.getIdProducto().toString()).param("codigo", "L1")
            .param("cantidad", "1").param("fechaVencimiento", "2027-12-31"))
            .andExpect(status().isOk()).andExpect(model().attributeHasErrors("lote"));
        productos.deleteAll();
        mvc.perform(get("/inventario/nuevo")).andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("registra un producto")));
    }

    private org.springframework.test.web.servlet.ResultActions guardar(String codigo, String cantidad, String fecha, String productoId) throws Exception {
        return mvc.perform(post("/inventario/guardar").param("codigo", codigo).param("cantidad", cantidad)
            .param("fechaVencimiento", fecha).param("producto.idProducto", productoId));
    }
}
