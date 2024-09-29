FROM mysql/mysql-server:latest
COPY ./my.cnf /etc/
VOLUME ./mysql-lib /var/lib/mysql
COPY ./setup/db-script-v3.1.sql /docker-entrypoint-initdb.d/
ENV  MYSQL_ROOT_PASSWORD=mysql@sit
ENV LANG=C.UTF-8
EXPOSE 3306




