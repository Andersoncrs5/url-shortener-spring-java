#!/bin/sh
set -eu

# Aguarda as portas estarem prontas na rede interna
until nc -z redpanda 9092; do
  echo "Aguardando Broker de Mensagens (redpanda:9092)..."
  sleep 3
done

until nc -z pd 2379; do
  echo "Aguardando TiDB PD (pd:2379)..."
  sleep 3
done

# Aguarda o Spring Boot subir e criar os tópicos com 5 partições
echo "Esperando a criacao dos topicos via Spring Boot...."
sleep 45

DB_NAME="url_shortener"
TABELAS="api_key_permissions api_keys permissions roles url_access_rule url_redirect_rules url_tag_links url_tags urls user_roles users"

echo "Iniciando a criação dos changefeeds dedicados (1 tabela = 1 tópico)..."

for TABELA in $TABELAS; do
  echo "--------------------------------------------------------"
  echo "Criating changefeed para: $DB_NAME.$TABELA -> Topico: $TABELA"

  # Monta a configuração corrigida respeitando a hierarquia do TOML e o TiCDC
  cat <<EOF > /tmp/filter_$TABELA.toml
[filter]
rules = ["$DB_NAME.$TABELA"]

[[filter.event-filters]]
matcher = ["$DB_NAME.$TABELA"]
ignore-event = ["create table", "drop table", "truncate table", "rename table", "alter table"]

[sink]
protocol = "canal-json"
EOF

  # Remove underline do ID para evitar rejeição de caractere do TiCDC
  CF_ID=$(echo "sync-$DB_NAME-$TABELA" | tr '_' '-')

  # Executa a criação apontando diretamente para o PD
  /cdc cli changefeed create \
    --pd=http://pd:2379 \
    --changefeed-id="$CF_ID" \
    --sink-uri="kafka://redpanda:9092/$TABELA?message-max-bytes=10485760" \
    --config=/tmp/filter_$TABELA.toml

done

echo "--------------------------------------------------------"
echo "Todos os changefeeds individuais foram inicializados com sucesso!"
