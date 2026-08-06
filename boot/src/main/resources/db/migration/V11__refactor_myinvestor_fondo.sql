-- Refactor MyInvestor: Cartera Metal pasa a tratarse como un fondo más.
-- La cuenta agregada de fondos se unifica como myinvestor-fondo.

-- Se elimina el historial de instantaneas de la cuenta de metal.
DELETE FROM instantaneas_mensuales WHERE cuenta_id = 'myinvestor-metal';

-- La cuenta agregada de fondos pasa a llamarse myinvestor-fondo.
INSERT INTO cuentas (id, plataforma_id, nombre, tipo, orden)
SELECT 'myinvestor-fondo', plataforma_id, 'Fondos', tipo, orden
FROM cuentas WHERE id = 'myinvestor-fondos';
UPDATE instantaneas_mensuales SET cuenta_id = 'myinvestor-fondo' WHERE cuenta_id = 'myinvestor-fondos';
DELETE FROM cuentas WHERE id = 'myinvestor-fondos';

-- La cuenta de metal desaparece (su saldo pasa a registrarse como fondo).
DELETE FROM cuentas WHERE id = 'myinvestor-metal';
