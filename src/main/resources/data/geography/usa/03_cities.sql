-- ==============================================================================
-- CIUDADES DE ESTADOS UNIDOS
-- Ciudades principales por estado
-- ==============================================================================

INSERT INTO cities (ci_code, st_code, co_code, ci_name) VALUES
-- California
('CA001', 'CA', 'USA', 'Los Angeles'),
('SF', 'CA', 'USA', 'San Francisco'),

-- New York
('NY001', 'NY', 'USA', 'New York City'),

-- Texas
('TX001', 'TX', 'USA', 'Houston')
ON CONFLICT (ci_code, st_code, co_code) DO NOTHING;
