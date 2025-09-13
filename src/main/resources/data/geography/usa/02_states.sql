-- ==============================================================================
-- ESTADOS DE ESTADOS UNIDOS
-- Estados principales de USA
-- ==============================================================================

INSERT INTO states (st_code, co_code, st_name) VALUES
('CA', 'USA', 'California'),
('NY', 'USA', 'New York'),
('TX', 'USA', 'Texas'),
('FL', 'USA', 'Florida'),
('IL', 'USA', 'Illinois')
ON CONFLICT (st_code, co_code) DO NOTHING;
