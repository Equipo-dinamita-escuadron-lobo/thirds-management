-- ==============================================================================
-- ESTADOS DE MÉXICO
-- Estados principales de México
-- ==============================================================================

INSERT INTO states (st_code, co_code, st_name) VALUES
('CDMX', 'MEX', 'Ciudad de México'),
('JAL', 'MEX', 'Jalisco'),
('NL', 'MEX', 'Nuevo León'),
('BC', 'MEX', 'Baja California')
ON CONFLICT (st_code, co_code) DO NOTHING;
