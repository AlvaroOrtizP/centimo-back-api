package com.centimo.api.it;

class GastoIT {
    /**
     * Hacer un select count de todas las entidades y comprobar que no existen datos en ninguna salvo Cuentas, Plataformas y flyway
     *
     * Llamada a upsertSnapshot
     *
     * Comprobar que existe un registro nuevo (ese nuevo registro nos valdra para crear los gastos) el gasto de ese registro debe de ser 0
     *
     * Llamada a createExpense creamos un gasto a esa instantanea mensual con el valor 20 para el dia 2 de ese mes
     *
     * Llamada a createExpense creamos un gasto a esa instantanea mensual con el valor 5 para el dia 13 de ese mes
     *
     * Comprobar que sigue existiendo el registro de instantanea y que su valor de gasto es 25 para ese mes
     *
     * Comprobamos tosas las tablas y debe salir lo mismo que al principio pero con 2 registros en gastos y 1 en instantanea
     *
     * TODO Probar borrado....
     */
}
