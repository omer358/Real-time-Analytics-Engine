#!/bin/bash
set -e

curl -X POST http://debezium:8083/connectors \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"rta-postgres-cdc\",
    \"config\": {
      \"connector.class\": \"io.debezium.connector.postgresql.PostgresConnector\",
      \"database.hostname\": \"${RTA_DB_HOST}\",
      \"database.port\": \"${RTA_DB_PORT}\",
      \"database.user\": \"${RTA_DB_USER}\",
      \"database.password\": \"${RTA_DB_PASSWORD}\",
      \"database.dbname\": \"${RTA_DB_NAME}\",
      \"topic.prefix\": \"rta\",
      \"plugin.name\": \"pgoutput\",
      \"decimal.handling.mode\": \"double\",
      \"slot.name\": \"rta_debezium_slot\",
      \"publication.autocreate.mode\": \"filtered\",
      \"table.include.list\": \"public.product\",
      \"tombstones.on.delete\": \"false\",
      \"include.schema.changes\": false
    }
  }"
