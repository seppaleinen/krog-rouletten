FROM nginx:stable-alpine

RUN adduser -D -H -u 1000 -s /bin/bash www-data -G www-data
ADD sites-enabled/ /etc/nginx/sites-enabled
ADD nginx.conf /etc/nginx/nginx.conf
ADD certs/ /etc/nginx/certs

CMD ["/usr/sbin/nginx"]