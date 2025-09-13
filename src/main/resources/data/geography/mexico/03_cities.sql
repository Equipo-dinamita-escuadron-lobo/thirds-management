-- ==============================================================================
-- CIUDADES DE MÉXICO
-- Ciudades principales por estado
-- ==============================================================================

INSERT INTO cities (ci_code, st_code, co_code, ci_name) VALUES
-- Ciudad de México
('CDMX001', 'CDMX', 'MEX', 'Ciudad de México'),

-- Jalisco
('JAL001', 'JAL', 'MEX', 'Guadalajara'),

-- Nuevo León
('NL001', 'NL', 'MEX', 'Monterrey')
ON CONFLICT (ci_code, st_code, co_code) DO NOTHING;
