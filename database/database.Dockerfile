FROM mysql/mysql-server:latest
COPY ./my.cnf /etc/
VOLUME ./mysql-lib /var/lib/mysql
COPY ./setup/db-script-v2.sql /docker-entrypoint-initdb.d/
ENV  MYSQL_ROOT_PASSWORD=mysql@sit
EXPOSE 3306




