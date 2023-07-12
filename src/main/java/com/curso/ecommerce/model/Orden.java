package com.curso.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orden {
    private Integer id;
    private String numero;
    private Date fechaCreacion;
    private Date fechaRecibida;
    private double total;
}
