INSERT INTO `third_type` (`tt_id`, `tt_name`) VALUES
(1, 'Cliente'),
(2, 'Proveedor'),
(3, 'Empleado'),
(4, 'Otro');

INSERT INTO `type_id` (`ti_id`, `ti_name`, `ti_created_at`, `ti_updated_at`) VALUES
('CC', 'Cédula de Ciudadanía', NULL, NULL),
('CE', 'Cédula de Extranjería', NULL, NULL),
('NIT', 'Número de Identificación Tributaria', NULL, NULL),
('Pasaporte', 'Pasaporte', NULL, NULL),
('TI', 'Tarjeta de Identidad', NULL, NULL);