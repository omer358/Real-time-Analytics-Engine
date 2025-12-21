curl -X POST http://debezium:8083/connectors \
  -H "Content-Type: application/json" \
  -d '{
    "name": "rta-postgres-cdc",
    "config": {
      "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
      "database.hostname": "postgres",
      "database.port": "5432",
      "database.user": "postgres",
      "database.password": "postgres",
      "database.dbname": "rta_ecommerce",
      "topic.prefix": "rta",
      "plugin.name": "pgoutput",
      "decimal.handling.mode": "double",
      "slot.name": "rta_debezium_slot",
      "publication.autocreate.mode": "filtered",
      "table.include.list": "public.product",
      "tombstones.on.delete": "false",
      "include.schema.changes": false
    }
  }'
