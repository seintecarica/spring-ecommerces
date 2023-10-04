package com.curso.ecommerce.controller;

import com.curso.ecommerce.model.Usuario;
import com.curso.ecommerce.service.IUsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {
    private final Logger logger = LoggerFactory.getLogger(UsuarioController.class);
    @Autowired
    private IUsuarioService usuarioService; //para acceder a operaciones CRUD
    @GetMapping("/registro")
    public String create(){
        return "usuario/registro";
    }

    @PostMapping("/save")
    public String save(Usuario usuario){
        this.logger.info("Usuario registro: {}", usuario);
        usuario.setTipo("USER");
        this.usuarioService.save(usuario);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(){
        return "usuario/login";
    }

    @PostMapping("/acceder")
    public String acceder(Usuario usuario, HttpSession session){
        this.logger.info("Accesos: {}", usuario);
        Optional<Usuario> user = this.usuarioService.findByEmail(usuario.getEmail());
        //this.logger.info("Usuario obtenido de db:", user.get());
        if(user.isPresent()){
            session.setAttribute("idUsuario", user.get().getId());
            if(user.get().getTipo().equals("ADMIN")){
                return "redirect:/administrador";
            }
            else{
                return "redirect:/";
            }
        }
        else{
            this.logger.info("Usuario no existe");
        }
        return "redirect:/";
    }
}
