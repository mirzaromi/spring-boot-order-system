CREATE TYPE "notif_status" AS ENUM (
  'SUCCESS',
  'FAILED',
  'PENDING'
);

CREATE TYPE "order_status" AS ENUM (
  'CREATED',
  'PAID',
  'CANCELED',
  'FAILED'
);

CREATE TYPE "payment_status" AS ENUM (
  'SUCCESS',
  'PENDING',
  'FAILED'
);

CREATE TABLE "users" (
  "id" INT PRIMARY KEY,
  "name" VARCHAR NOT NULL,
  "created_at" TIMESTAMP DEFAULT (NOW()),
  "updated_at" TIMESTAMP DEFAULT (NOW())
);

CREATE TABLE "orders" (
  "id" INT PRIMARY KEY,
  "user_id" INT NOT NULL,
  "status" order_status NOT NULL,
  "total_amount" DECIMAL(10,2) NOT NULL,
  "remarks" VARCHAR,
  "created_at" TIMESTAMP DEFAULT (NOW()),
  "updated_at" TIMESTAMP DEFAULT (NOW())
);

CREATE TABLE "order_details" (
  "id" INT PRIMARY KEY,
  "order_id" INT,
  "inventory_id" INT NOT NULL,
  "quantity" INT NOT NULL,
  "price" DECIMAL(10,2) NOT NULL,
  "created_at" TIMESTAMP DEFAULT (NOW()),
  "updated_at" TIMESTAMP DEFAULT (NOW())
);

CREATE TABLE "payments" (
  "id" INT PRIMARY KEY,
  "order_id" INT NOT NULL,
  "amount" DECIMAL(10,2) NOT NULL,
  "status" payment_status NOT NULL,
  "transaction_id" VARCHAR(50) UNIQUE NOT NULL,
  "created_at" TIMESTAMP DEFAULT (NOW())
);

CREATE TABLE "inventory" (
  "id" INT PRIMARY KEY,
  "product_id" INT,
  "stock" INT NOT NULL,
  "price" INT NOT NULL,
  "min_stock" INT DEFAULT 10,
  "created_at" TIMESTAMP DEFAULT (NOW()),
  "updated_at" TIMESTAMP DEFAULT (NOW())
);

CREATE TABLE "notifications" (
  "id" int PRIMARY KEY,
  "user_id" INT NOT NULL,
  "message" TEXT NOT NULL,
  "event_type" VARCHAR(50) NOT NULL,
  "notif_status" notif_status,
  "status" VARCHAR(20) NOT NULL,
  "created_at" TIMESTAMP DEFAULT (NOW())
);

ALTER TABLE "orders" ADD FOREIGN KEY ("id") REFERENCES "users" ("id") ON DELETE CASCADE;

ALTER TABLE "order_details" ADD FOREIGN KEY ("order_id") REFERENCES "orders" ("id") ON DELETE CASCADE;

ALTER TABLE "order_details" ADD FOREIGN KEY ("inventory_id") REFERENCES "inventory" ("id") ON DELETE CASCADE;

ALTER TABLE "payments" ADD FOREIGN KEY ("id") REFERENCES "orders" ("id") ON DELETE CASCADE;

ALTER TABLE "notifications" ADD FOREIGN KEY ("id") REFERENCES "orders" ("id") ON DELETE CASCADE;
