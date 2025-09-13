-- ==============================================================================
-- PAÍSES - Datos base
-- ==============================================================================

INSERT INTO countries (co_code, co_name) VALUES
('COL', 'Colombia'),
('USA', 'Estados Unidos'),
('MEX', 'México')
ON CONFLICT (co_code) DO NOTHING;
