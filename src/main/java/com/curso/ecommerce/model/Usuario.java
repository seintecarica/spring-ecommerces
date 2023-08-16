package com.curso.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usuarios") // Para modificar el nombre de la tabla en la BBDD.
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //el id sea autoincrementable
    private Integer id;
    private String nombre;
    private String username;
    private String email;
    private String direccion;
    private String telefono;
    private String tipo;
    private String password;

    @OneToMany(mappedBy = "usuario") //En la tabla Producto existe un atributo llamado usuario;
    private List<Producto> producto;

    @OneToMany(mappedBy = "usuario")
    private List<Orden> ordenes;

    public Usuario(Integer id, String nombre, String username, String email, String direccion, String telefono, String tipo, String password) {
        this.id = id;
        this.nombre = nombre;
        this.username = username;
        this.email = email;
        this.direccion = direccion;
        this.telefono = telefono;
        this.tipo = tipo;
        this.password = password;
    }
}
