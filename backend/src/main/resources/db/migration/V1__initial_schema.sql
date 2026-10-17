-- SupplyChainOS V1 — initial schema (Phase 1).
-- All 16 core tables from the spec, with PKs, FKs, unique + check constraints, indexes.
-- Monetary values use NUMERIC(12,2). Timestamps are TIMESTAMPTZ (UTC).
-- Entities for users/warehouses/products/inventory exist in Phase 1;
-- entities for the remaining tables land with their modules (Phases 2-5).
-- No business data is seeded here except reference warehouses (V2).

CREATE TABLE IF NOT EXISTS users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash TEXT NOT NULL,
  full_name VARCHAR(200) NOT NULL,
  role VARCHAR(30) NOT NULL CHECK (role IN ('ADMIN','OPERATIONS_MANAGER','ANALYST')),
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

CREATE TABLE IF NOT EXISTS warehouses (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  code VARCHAR(20) NOT NULL UNIQUE,
  name VARCHAR(200) NOT NULL,
  city VARCHAR(100) NOT NULL,
  state VARCHAR(10) NOT NULL,
  country VARCHAR(100) NOT NULL DEFAULT 'US',
  timezone VARCHAR(50) NOT NULL,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS products (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  sku VARCHAR(50) NOT NULL UNIQUE,
  name VARCHAR(300) NOT NULL,
  category VARCHAR(100) NOT NULL,
  unit_price NUMERIC(12,2) NOT NULL CHECK (unit_price >= 0),
  lead_time_days INT NOT NULL CHECK (lead_time_days >= 0),
  safety_stock INT NOT NULL CHECK (safety_stock >= 0),
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);
CREATE INDEX IF NOT EXISTS idx_products_sku ON products(sku);

CREATE TABLE IF NOT EXISTS inventory (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  warehouse_id UUID NOT NULL REFERENCES warehouses(id),
  product_id UUID NOT NULL REFERENCES products(id),
  quantity_on_hand INT NOT NULL CHECK (quantity_on_hand >= 0),
  reserved_quantity INT NOT NULL CHECK (reserved_quantity >= 0),
  reorder_threshold INT NOT NULL CHECK (reorder_threshold >= 0),
  version BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT uq_inventory_warehouse_product UNIQUE (warehouse_id, product_id),
  CONSTRAINT chk_inventory_reserved_le_on_hand CHECK (reserved_quantity <= quantity_on_hand)
);
CREATE INDEX IF NOT EXISTS idx_inventory_warehouse ON inventory(warehouse_id);
CREATE INDEX IF NOT EXISTS idx_inventory_product ON inventory(product_id);
CREATE INDEX IF NOT EXISTS idx_inventory_low_stock ON inventory(warehouse_id, product_id)
  WHERE quantity_on_hand - reserved_quantity <= reorder_threshold;

CREATE TABLE IF NOT EXISTS orders (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  order_number VARCHAR(50) NOT NULL UNIQUE,
  warehouse_id UUID NOT NULL REFERENCES warehouses(id),
  status VARCHAR(30) NOT NULL
    CHECK (status IN ('CREATED','PROCESSING','FULFILLED','SHIPPED','DELIVERED','CANCELLED')),
  customer_name VARCHAR(200),
  total_amount NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (total_amount >= 0),
  source_system VARCHAR(100),
  source_order_id VARCHAR(100),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT uq_orders_source UNIQUE (source_system, source_order_id)
);
CREATE INDEX IF NOT EXISTS idx_orders_warehouse_status ON orders(warehouse_id, status);
CREATE INDEX IF NOT EXISTS idx_orders_created ON orders(created_at);

CREATE TABLE IF NOT EXISTS order_items (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  product_id UUID NOT NULL REFERENCES products(id),
  quantity INT NOT NULL CHECK (quantity > 0),
  unit_price NUMERIC(12,2) NOT NULL CHECK (unit_price >= 0),
  line_total NUMERIC(12,2) NOT NULL CHECK (line_total >= 0)
);
CREATE INDEX IF NOT EXISTS idx_order_items_order ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_product ON order_items(product_id);

CREATE TABLE IF NOT EXISTS shipments (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  shipment_number VARCHAR(50) NOT NULL UNIQUE,
  order_id UUID NOT NULL REFERENCES orders(id),
  origin_warehouse_id UUID NOT NULL REFERENCES warehouses(id),
  destination_region VARCHAR(100) NOT NULL,
  carrier VARCHAR(100) NOT NULL,
  tracking_reference VARCHAR(100),
  status VARCHAR(30) NOT NULL
    CHECK (status IN ('PENDING','IN_TRANSIT','DELIVERED','DELAYED','EXCEPTION')),
  expected_delivery_date DATE,
  actual_delivery_date DATE,
  source_system VARCHAR(100),
  source_shipment_id VARCHAR(100),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT uq_shipments_source UNIQUE (source_system, source_shipment_id)
);
CREATE INDEX IF NOT EXISTS idx_shipments_order ON shipments(order_id);
CREATE INDEX IF NOT EXISTS idx_shipments_status ON shipments(status);
CREATE INDEX IF NOT EXISTS idx_shipments_expected ON shipments(expected_delivery_date);

CREATE TABLE IF NOT EXISTS inventory_movements (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  warehouse_id UUID NOT NULL REFERENCES warehouses(id),
  product_id UUID NOT NULL REFERENCES products(id),
  movement_type VARCHAR(30) NOT NULL
    CHECK (movement_type IN ('INBOUND','OUTBOUND','RESERVATION','RELEASE','TRANSFER_IN','TRANSFER_OUT','ADJUSTMENT')),
  quantity_change INT NOT NULL CHECK (quantity_change <> 0),
  quantity_before INT NOT NULL CHECK (quantity_before >= 0),
  quantity_after INT NOT NULL CHECK (quantity_after >= 0),
  reference_type VARCHAR(50),
  reference_id UUID,
  created_by UUID REFERENCES users(id),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_movements_wh_prod ON inventory_movements(warehouse_id, product_id, created_at DESC);

CREATE TABLE IF NOT EXISTS warehouse_routes (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  source_warehouse_id UUID NOT NULL REFERENCES warehouses(id),
  destination_warehouse_id UUID NOT NULL REFERENCES warehouses(id),
  distance_km NUMERIC(10,2) NOT NULL CHECK (distance_km >= 0),
  cost_per_unit NUMERIC(10,2) NOT NULL CHECK (cost_per_unit >= 0),
  transit_days INT NOT NULL CHECK (transit_days >= 0),
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT uq_route_pair UNIQUE (source_warehouse_id, destination_warehouse_id),
  CONSTRAINT chk_route_not_self CHECK (source_warehouse_id <> destination_warehouse_id)
);

CREATE TABLE IF NOT EXISTS data_import_jobs (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  import_type VARCHAR(30) NOT NULL CHECK (import_type IN ('ORDERS','INVENTORY','SHIPMENTS')),
  filename VARCHAR(500) NOT NULL,
  status VARCHAR(30) NOT NULL
    CHECK (status IN ('PENDING','PROCESSING','COMPLETED','FAILED','PARTIAL')),
  total_records INT NOT NULL DEFAULT 0 CHECK (total_records >= 0),
  success_count INT NOT NULL DEFAULT 0 CHECK (success_count >= 0),
  failed_count INT NOT NULL DEFAULT 0 CHECK (failed_count >= 0),
  source_hash VARCHAR(64),
  import_policy VARCHAR(30) NOT NULL DEFAULT 'UPSERT'
    CHECK (import_policy IN ('UPSERT','INITIALIZE','RECONCILE')),
  created_by UUID REFERENCES users(id),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  completed_at TIMESTAMPTZ
);
CREATE INDEX IF NOT EXISTS idx_import_jobs_type_status ON data_import_jobs(import_type, status);
CREATE INDEX IF NOT EXISTS idx_import_jobs_source_hash ON data_import_jobs(source_hash);

CREATE TABLE IF NOT EXISTS data_import_errors (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  job_id UUID NOT NULL REFERENCES data_import_jobs(id) ON DELETE CASCADE,
  row_number INT NOT NULL CHECK (row_number >= 1),
  raw_row TEXT,
  error_code VARCHAR(50) NOT NULL,
  error_message TEXT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_import_errors_job ON data_import_errors(job_id, row_number);

CREATE TABLE IF NOT EXISTS alerts (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  alert_type VARCHAR(50) NOT NULL,
  severity VARCHAR(20) NOT NULL CHECK (severity IN ('INFO','WARNING','CRITICAL')),
  entity_type VARCHAR(50),
  entity_id UUID,
  message TEXT NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN','ACKNOWLEDGED','RESOLVED')),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  resolved_at TIMESTAMPTZ
);
CREATE INDEX IF NOT EXISTS idx_alerts_status_severity ON alerts(status, severity, created_at DESC);

CREATE TABLE IF NOT EXISTS transfer_recommendations (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  product_id UUID NOT NULL REFERENCES products(id),
  source_warehouse_id UUID NOT NULL REFERENCES warehouses(id),
  destination_warehouse_id UUID NOT NULL REFERENCES warehouses(id),
  recommended_quantity INT NOT NULL CHECK (recommended_quantity > 0),
  estimated_cost NUMERIC(12,2) NOT NULL CHECK (estimated_cost >= 0),
  estimated_delivery_days INT NOT NULL CHECK (estimated_delivery_days >= 0),
  reason TEXT NOT NULL,
  explanation JSONB,
  status VARCHAR(20) NOT NULL DEFAULT 'DRAFT'
    CHECK (status IN ('DRAFT','APPROVED','REJECTED','EXPIRED')),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  decided_by UUID REFERENCES users(id),
  decided_at TIMESTAMPTZ,
  CONSTRAINT chk_recommendation_not_self CHECK (source_warehouse_id <> destination_warehouse_id)
);
CREATE INDEX IF NOT EXISTS idx_recommendations_status ON transfer_recommendations(status, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_recommendations_dest_product ON transfer_recommendations(destination_warehouse_id, product_id);

CREATE TABLE IF NOT EXISTS inventory_transfers (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  transfer_number VARCHAR(50) NOT NULL UNIQUE,
  recommendation_id UUID REFERENCES transfer_recommendations(id),
  source_warehouse_id UUID NOT NULL REFERENCES warehouses(id),
  destination_warehouse_id UUID NOT NULL REFERENCES warehouses(id),
  status VARCHAR(20) NOT NULL DEFAULT 'DRAFT'
    CHECK (status IN ('DRAFT','APPROVED','IN_TRANSIT','COMPLETED','CANCELLED')),
  version BIGINT NOT NULL DEFAULT 0,
  created_by UUID REFERENCES users(id),
  approved_by UUID REFERENCES users(id),
  approved_at TIMESTAMPTZ,
  shipped_at TIMESTAMPTZ,
  received_at TIMESTAMPTZ,
  cancelled_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT chk_transfer_not_self CHECK (source_warehouse_id <> destination_warehouse_id)
);
CREATE INDEX IF NOT EXISTS idx_transfers_status ON inventory_transfers(status, created_at DESC);

CREATE TABLE IF NOT EXISTS inventory_transfer_items (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  transfer_id UUID NOT NULL REFERENCES inventory_transfers(id) ON DELETE CASCADE,
  product_id UUID NOT NULL REFERENCES products(id),
  quantity INT NOT NULL CHECK (quantity > 0),
  CONSTRAINT uq_transfer_item UNIQUE (transfer_id, product_id)
);
CREATE INDEX IF NOT EXISTS idx_transfer_items_transfer ON inventory_transfer_items(transfer_id);

CREATE TABLE IF NOT EXISTS audit_logs (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID REFERENCES users(id),
  action VARCHAR(100) NOT NULL,
  entity_type VARCHAR(50) NOT NULL,
  entity_id UUID,
  changes JSONB,
  ip_address VARCHAR(45),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_audit_entity ON audit_logs(entity_type, entity_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_audit_user ON audit_logs(user_id, created_at DESC);
