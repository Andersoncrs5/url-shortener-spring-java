#!/bin/sh
set -eu

until nc -z redpanda 9092; do
  echo "Aguardando Kafka..."
  sleep 3
done

until nc -z ticdc 8300; do
  echo "Aguardando TiCDC..."
  sleep 3
done

echo "Esperando a criacao dos topics...."
sleep 100


DB_NAME="url_shortener"
TABELAS="api_key_permissions api_keys permissions roles url_access_rule url_redirect_rules url_tag_links url_tags urls user_roles users"

echo "Iniciando a criação dos changefeeds dedicados (1 tabela = 1 tópico)..."

for TABELA in $TABELAS; do
  echo "--------------------------------------------------------"
  echo "Criando replicação para: $DB_NAME.$TABELA -> Tópico: $TABELA"

  cat <<EOF > /tmp/filter_$TABELA.toml
[filter]
rules = ['$DB_NAME.$TABELA']
EOF

  CF_ID=$(echo "sync-$DB_NAME-$TABELA" | tr '_' '-')

  /cdc cli changefeed create \
    --server=http://ticdc:8300 \
    --sink-uri="kafka://redpanda:9092/$TABELA?protocol=open-protocol&replication-factor=1&kafka-version=3.4.0" \
    --changefeed-id="$CF_ID" \
    --sort-engine="unified" \
    --config=/tmp/filter_$TABELA.toml

done

echo "--------------------------------------------------------"
echo "Todos os changefeeds foram inicializados com sucesso!"
