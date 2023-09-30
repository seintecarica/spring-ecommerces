package com.curso.ecommerce.controller;

import com.curso.ecommerce.model.DetalleOrden;
import com.curso.ecommerce.model.Orden;
import com.curso.ecommerce.model.Producto;
import com.curso.ecommerce.model.Usuario;
import com.curso.ecommerce.service.IDetalleOrdenService;
import com.curso.ecommerce.service.IOrdenService;
import com.curso.ecommerce.service.IProductoService;
import com.curso.ecommerce.service.IUsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class HomeController {

    private final Logger log = LoggerFactory.getLogger(HomeController.class);
    @Autowired
    private IProductoService productoService;
    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private IOrdenService ordenService;
    @Autowired
    private IDetalleOrdenService detalleOrdenService;

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

        //Validar que el producto no se repita
        Integer idProducto = producto.getId();
        boolean ingresado = this.detalles.stream().anyMatch(p -> p.getProducto().getId() == idProducto);

        if(!ingresado){
            this.detalles.add(detalleOrden);
        }

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

    @GetMapping("/getCart")
    public String getCart(Model model){
        model.addAttribute("cart", this.detalles);
        model.addAttribute("orden", this.orden);
        return "/usuario/carrito";
    }

    @GetMapping("/order")
    public String order(Model model){
        Usuario usuario = this.usuarioService.findById(1).get();

        model.addAttribute("cart", this.detalles);
        model.addAttribute("orden", this.orden);
        model.addAttribute("usuario", usuario);
        return "usuario/resumenorden";
    }

    //GUARDAR LA ORDEN
    @GetMapping("/saveOrder")
    public String saveOrder(){
        Date fechacreacion = new Date();
        this.orden.setFechaCreacion(fechacreacion);
        this.orden.setNumero(this.ordenService.generarNumeroOrden());
        //usuario
        Usuario usuario = this.usuarioService.findById(1).get();
        this.orden.setUsuario(usuario);

        this.ordenService.save(this.orden);
        //Guardar detalles
        for (DetalleOrden dt:this.detalles){
            dt.setOrden(this.orden);
            this.detalleOrdenService.save(dt);
        }
        //Limpiar los valores del detalle y la orden
        this.orden = new Orden();
        this.detalles.clear();

        return "redirect:/";
    }

    @PostMapping("/search")
    public String searchProduct(@RequestParam String nombre, Model model){
        log.info("Nombre del producto: {}", nombre);
        List<Producto> productos = this.productoService.findAll().stream().filter(p -> p.getNombre().contains(nombre)).collect(Collectors.toList());
        model.addAttribute("productos", productos);
        return "usuario/home";
    }
}
