package com.curso.ecommerce.controller;

import com.curso.ecommerce.model.DetalleOrden;
import com.curso.ecommerce.model.Orden;
import com.curso.ecommerce.model.Producto;
import com.curso.ecommerce.service.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class HomeController {

    private final Logger log = LoggerFactory.getLogger(HomeController.class);
    @Autowired
    private ProductoService productoService;

    //Para almacenar los detalles de la orden
    private List<DetalleOrden> detalles = new ArrayList<DetalleOrden>();
    //Datos de la orden
    private Orden orden = new Orden();
    @GetMapping("")
    public String home(Model model){
        model.addAttribute("productos", this.productoService.findAll());
        return "usuario/home";
    }

    @GetMapping("/productohome/{id}")
    public String productoHome(@PathVariable Integer id, Model model){
        this.log.info("Id producto enviado como parámetro {}", id);
        Producto producto = new Producto();
        Optional<Producto> productoOptional = this.productoService.get(id);
        producto = productoOptional.get();
        model.addAttribute("producto", producto);
        return "/usuario/productohome";
    }

    @PostMapping("/cart")
    public String addCart(@RequestParam Integer id, @RequestParam Integer cantidad, Model model){
        DetalleOrden detalleOrden = new DetalleOrden();
        Producto producto = new Producto();
        double sumaTotal = 0;
        Optional<Producto> optionalProducto = this.productoService.get(id);
        //this.log.info("Producto añadido: {}", optionalProducto.get());
        this.log.info("Cantidad: {}", cantidad);

        producto = optionalProducto.get();
        detalleOrden.setCantidad(cantidad);
        detalleOrden.setPrecio(producto.getPrecio());
        detalleOrden.setNombre(producto.getNombre());
        detalleOrden.setTotal(producto.getPrecio()*cantidad);
        detalleOrden.setProducto(producto);

        this.detalles.add(detalleOrden);

        sumaTotal = this.detalles.stream().mapToDouble(dt->dt.getTotal()).sum();
        this.orden.setTotal(sumaTotal);

        model.addAttribute("cart", this.detalles);
        model.addAttribute("orden", this.orden);
        return "usuario/carrito";
    }

    //Quitar un producto del carrito
    @GetMapping("/delete/cart/{id}")
    public String deleteProductoCart(@PathVariable Integer id, Model model){
        //Lista nueva de productos
        List<DetalleOrden> ordenesNueva = new ArrayList<DetalleOrden>();

        for(DetalleOrden detalleOrden: this.detalles) {
            if(detalleOrden.getProducto().getId() != id){
                ordenesNueva.add(detalleOrden);
            }
        }
        //Pone la nueva lsta con los productos restantes
        this.detalles = ordenesNueva;

        double sumaTotal = 0;

        sumaTotal = this.detalles.stream().mapToDouble(dt->dt.getTotal()).sum();
        this.orden.setTotal(sumaTotal);

        model.addAttribute("cart", this.detalles);
        model.addAttribute("orden", this.orden);

        return "usuario/carrito";
    }
}
