package com.fiec.provafinal.models;

import jakarta.persistence.EntityManager;

public class SapatoRepositorio{

    public SapatoRepositorio(EntityManager entityManager) {
        super();
    }


    Class<Sapato> getMyClass() {
        return Sapato.class;
    }
}