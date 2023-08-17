package com.curso.ecommerce.controller;

import com.curso.ecommerce.model.Producto;
import com.curso.ecommerce.model.Usuario;
import com.curso.ecommerce.service.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final Logger LOGGER = LoggerFactory.getLogger(ProductoController.class);
    @Autowired //Para que spring lo instancie
    private ProductoService productoService; //Interfaz donde definimos los métodos

    @GetMapping("")
    public String show(Model model){ //model va traer información desde el backend hacia la vista
        model.addAttribute("productos", productoService.findAll());
        return "productos/show";
    }

    @GetMapping("/create")
    public String create(){
        return "productos/create";
    }

    @PostMapping("/save")
    public String save(Producto producto){
        LOGGER.info("Este es el objeto producto: {}", producto);

        Usuario u = new Usuario(1, "", "", "", "", "", "", "");
        producto.setUsuario(u);
        this.productoService.save(producto);
        return "redirect:/productos"; //Va a cargar la vista show
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model){
        Producto producto = new Producto();
        LOGGER.info("Producto id: {}", id.toString());

        Optional<Producto> optionalProducto = this.productoService.get(id);
        producto = optionalProducto.get();
        //LOGGER.info("Producto buscado: {}", producto);

        model.addAttribute("producto", producto);
        return "productos/edit";
    }

    @PostMapping("/update")
    public String update(Producto producto){
        this.productoService.update(producto);
        return "redirect:/productos";
    }
}
