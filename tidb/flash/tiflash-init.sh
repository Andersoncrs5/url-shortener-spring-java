#!/bin/sh

set -e

# Função utilitária para executar queries no TiDB de forma segura
run_query() {
    mysql -h tidb-server -P4000 -uroot -N -s -e "$1" 2>/dev/null || echo "0"
}

echo "Waiting for TiDB Server to accept connections..."
until mysql -h tidb-server -P4000 -uroot -e "SELECT 1;" >/dev/null 2>&1; do
    sleep 3
done

echo "Waiting for Flyway..."
until [ "$(run_query "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='url_shortener' AND table_name='flyway_schema_history';")" = "1" ]; do
    sleep 5
done

echo "Waiting for application tables..."
until [ "$(run_query "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='url_shortener';")" -gt 1 ]; do
    sleep 5
done

echo "Waiting for TiFlash engine to register in the cluster..."
until [ "$(run_query "SELECT COUNT(*) FROM information_schema.CLUSTER_INFO WHERE TYPE = 'tiflash';")" -gt 0 ]; do
    echo "TiFlash server count is still 0. Waiting..."
    sleep 5
done

echo "Enabling TiFlash replicas for database url_shortener..."
mysql -h tidb-server -P4000 -uroot -e "ALTER DATABASE url_shortener SET TIFLASH REPLICA 1;"

echo "TiFlash enabled and replication started!"
