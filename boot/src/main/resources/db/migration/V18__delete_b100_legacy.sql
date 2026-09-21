-- Eliminación del modelo antiguo de B100 (plataforma b100 + cuentas b100-* + sus instantáneas).
-- B100 pasa a vivir únicamente en b100_balances (creada en V17).
-- Las cascadas de V2/V3/V4 limpian: cuentas b100-corriente/save/heal, sus instantáneas mensuales
-- y los gastos, fuentes de ingreso y tareas asociadas a esas instantáneas.
DELETE FROM plataformas WHERE id = 'b100';