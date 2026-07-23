package com.centimo.api.it;

class NominaPantallaIT {
    /**
     * Hacer un select count de todas las entidades y comprobar que no existen datos en ninguna salvo Cuentas, Plataformas y flyway
     *
     * Llamada a getNominaAndDate que debo retornar Not found al no encontrar ninguna nomina antes
     * Llamada a createNomina que debe retornar los valores de la nomina creada
     *
     * Hacer un select count de todas las tablas que debe retornar lo mismo que antes pero con un regitro nuevo (comprobar sus datos)
     *
     * Llamada a X para modificar la nomina existente
     * Hacer un select count de todas las tablas que debe retornar lo mismo que antes pero con un regitro actualizado (comprobar sus datos)
     *
     * Llamada a X para delete de la nomina existente
     * Hacer un select count de todas las tablas que debe retornar lo mismo que al inicio
     */
}
