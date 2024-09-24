FROM nginx:alpine
COPY ./https/fullchain.pem /ssl/fullchain.pem
COPY ./https/privkey.pem /ssl/privkey.pem
COPY ./https/dhparam-2048.pem /ssl/dhparam-2048.pem
COPY ./nginx.conf /etc/nginx/conf.d/default.conf