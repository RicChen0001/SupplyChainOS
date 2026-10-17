-- Reference data: the three Northstar Commerce warehouses (fictional, synthetic demo).
-- Product / order / shipment demo data is generated in Phase 2, not here.
INSERT INTO warehouses (code, name, city, state, country, timezone, is_active) VALUES
  ('SEA', 'Seattle Fulfillment Center', 'Seattle', 'WA', 'US', 'America/Los_Angeles', TRUE),
  ('CHI', 'Chicago Fulfillment Center', 'Chicago', 'IL', 'US', 'America/Chicago', TRUE),
  ('ATL', 'Atlanta Fulfillment Center', 'Atlanta', 'GA', 'US', 'America/New_York', TRUE)
ON CONFLICT (code) DO NOTHING;

-- Warehouse-to-warehouse routes: road distance approximations used by the
-- Phase 4 recommendation engine. Cost = cost_per_unit * quantity (documented in docs).
-- Sources: approximate driving distances SEA-CHI ~2,800 km, SEA-ATL ~3,500 km, CHI-ATL ~1,100 km.
INSERT INTO warehouse_routes (source_warehouse_id, destination_warehouse_id, distance_km, cost_per_unit, transit_days, is_active)
SELECT s.id, d.id, v.distance_km, v.cost_per_unit, v.transit_days, TRUE
FROM (VALUES
  ('SEA','CHI', 2800, 1.85, 4),
  ('SEA','ATL', 3500, 2.30, 5),
  ('CHI','SEA', 2800, 1.85, 4),
  ('CHI','ATL', 1100, 0.95, 2),
  ('ATL','SEA', 3500, 2.30, 5),
  ('ATL','CHI', 1100, 0.95, 2)
) AS v(src, dst, distance_km, cost_per_unit, transit_days)
JOIN warehouses s ON s.code = v.src
JOIN warehouses d ON d.code = v.dst
ON CONFLICT (source_warehouse_id, destination_warehouse_id) DO NOTHING;
