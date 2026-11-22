package farmastock.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "lote", uniqueConstraints = @UniqueConstraint(columnNames = {"id_producto", "codigo"}))
public class Lote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLote;

    @NotNull(message = "Selecciona un producto")
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @NotBlank(message = "Ingresa el código del lote")
    @Size(max = 60, message = "El código admite hasta 60 caracteres")
    @Column(nullable = false, length = 60)
    private String codigo;

    @NotNull(message = "Ingresa la fecha de vencimiento")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(nullable = false)
    private LocalDate fechaVencimiento;

    @NotNull(message = "Ingresa la cantidad")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    @Column(nullable = false)
    private Integer cantidad;

    public Long getIdLote() { return idLote; }
    public void setIdLote(Long idLote) { this.idLote = idLote; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public boolean isVencido() {
        return fechaVencimiento != null && fechaVencimiento.isBefore(LocalDate.now());
    }
}
