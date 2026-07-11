CREATE DATABASE IF NOT EXISTS url_shortener
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_bin;

CREATE DATABASE IF NOT EXISTS url_shortener_notify
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_bin;

CREATE DATABASE IF NOT EXISTS url_shortener_test
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_bin;

-- Banco interno do Grafana
CREATE DATABASE IF NOT EXISTS grafana
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_bin;

CREATE USER IF NOT EXISTS 'grafana'@'%' IDENTIFIED BY 'grafana';

GRANT SELECT ON url_shortener.* TO 'grafana'@'%';
GRANT SELECT ON information_schema.* TO 'grafana'@'%';

GRANT ALL PRIVILEGES ON grafana.* TO 'grafana'@'%';

FLUSH PRIVILEGES;
