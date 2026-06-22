#!/bin/sh

echo "⏳ Waiting TiDB..."

until mysql -h tidb-server -P 4000 -u root -e "SELECT 1" 2>/dev/null; do
  sleep 3
done

echo "🚀 Creating databases..."

mysql -h tidb-server -P 4000 -u root < /scripts/init-db.sql

echo "✅ Databases created"
