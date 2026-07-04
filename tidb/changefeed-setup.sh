#!/bin/sh
set -eu

until nc -z redpanda 9092; do
  echo "Aguardando Broker de Mensagens (redpanda:9092)..."
  sleep 3
done

until nc -z pd 2379; do
  echo "Aguardando TiDB PD (pd:2379)..."
  sleep 3
done

echo "Esperando a criacao dos topicos via Spring Boot...."
sleep 45

DB_NAME="url_shortener"
TABELAS="api_key_permissions api_keys permissions roles url_access_rule url_redirect_rules url_tag_links url_tags urls user_roles users"

echo "Iniciando a verificação e criação dos changefeeds dedicados..."

for TABELA in $TABELAS; do
  echo "--------------------------------------------------------"

  CF_ID=$(echo "sync-$DB_NAME-$TABELA" | tr '_' '-')

  if /cdc cli changefeed list --pd=http://pd:2379 | grep -q "\"$CF_ID\""; then
    echo "Changefeed já existe: $CF_ID. Pulando a criação."
  else
    echo "Criando changefeed para: $DB_NAME.$TABELA -> Topico: $TABELA"

    cat <<EOF > /tmp/filter_$TABELA.toml

[filter]
rules = ["$DB_NAME.$TABELA"]

[[filter.event-filters]]
matcher = ["$DB_NAME.$TABELA"]
ignore-event = ["create table", "drop table", "truncate table", "rename table", "alter table"]

[sink]
protocol = "canal-json"
EOF

    /cdc cli changefeed create \
      --pd=http://pd:2379 \
      --changefeed-id="$CF_ID" \
      --sink-uri="kafka://redpanda:9092/$TABELA?message-max-bytes=10485760" \
      --config=/tmp/filter_$TABELA.toml

    echo "Changefeed $CF_ID criado com sucesso!"
  fi

done

echo "--------------------------------------------------------"
echo "Processo de inicialização dos changefeeds concluído!"
