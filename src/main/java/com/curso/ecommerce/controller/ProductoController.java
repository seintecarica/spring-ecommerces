package com.curso.ecommerce.controller;

import com.curso.ecommerce.model.Producto;
import com.curso.ecommerce.model.Usuario;
import com.curso.ecommerce.service.ProductoService;
import com.curso.ecommerce.service.UploadFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final Logger LOGGER = LoggerFactory.getLogger(ProductoController.class);
    @Autowired //Para que spring lo instancie
    private ProductoService productoService; //Interfaz donde definimos los métodos

    @Autowired
    private UploadFileService upload;

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
    public String save(Producto producto, @RequestParam("img") MultipartFile file) throws IOException {
        LOGGER.info("Este es el objeto producto: {}", producto);

        Usuario u = new Usuario(1, "", "", "", "", "", "", "");
        producto.setUsuario(u);

        //Imagen
        if(producto.getId() == null){ //Cuando se crea un producto
            String nombreImagen = this.upload.saveImage(file);
            producto.setImagen(nombreImagen); //Guardo en el campo imagen del objeto
        }
        else{

        }

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
    public String update(Producto producto, @RequestParam("img") MultipartFile file) throws IOException {
        Producto p = new Producto();
        p = this.productoService.get(producto.getId()).get();

        if(file.isEmpty()){ //Cuando editamos producto pero no se cambia imagen
            producto.setImagen(p.getImagen());
        }
        else{ //Cuando se edita también la imagen
            //Eliminar cuando no sea la imagen por defecto
            if(!p.getImagen().equals("default.jpg")){
                this.upload.deleteImage(p.getImagen());
            }
            String nombreImagen = this.upload.saveImage(file);
            producto.setImagen(nombreImagen); //Guardo en el campo imagen del objeto Producto
        }
        producto.setUsuario(p.getUsuario());
        this.productoService.update(producto);
        return "redirect:/productos";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id){
        Producto p = new Producto();
        p = this.productoService.get(id).get();

        //Eliminar cuando no sea la imagen por defecto
        if(!p.getImagen().equals("default.jpg")){
            this.upload.deleteImage(p.getImagen());
        }

        this.productoService.delete(id);
        return "redirect:/productos";
    }
}
