package com.curso.ecommerce.controller;

import com.curso.ecommerce.model.Producto;
import com.curso.ecommerce.service.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/administrador")
public class AdministradorController {

    @Autowired
    private IProductoService productoService;
    @GetMapping("")
    public String home(Model model){
        List<Producto> productos = this.productoService.findAll();
        //DE ESTA MANERA SE PASA VARIABLES A LA VISTA
        model.addAttribute("productos", productos);
        return "administrador/home";
    }
}
