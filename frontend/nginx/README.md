# NGINX server

To make it easier to integrate a real ssl-certificate on a server, I've
installed a self-signed certificate in the docker-image, which I exchange
later for the real certificate.

TODO:
A way to automatically update letsencrypt ssl certificate.
```
letsencrypt/letsencrypt-auto certonly -t --keep --authenticator webroot -w /root/workspace/krog-rouletten/frontend/webapp/src/main/webapp/application -d krogrouletten.se -d www.krogrouletten.se
```

[Local CDN in nGinx](https://jesus.perezpaz.es/2014/02/configure-subdomain-as-cdn-in-nginx-wordpress-w3-total-cache-configurations/)


letsencrypt/letsencrypt-auto certonly --server https://acme-v01.api.letsencrypt.org/directory -a webroot --webroot-path=/tmp/letsencrypt-auto --agree-dev-preview -d krogrouletten.se -d www.krogrouletten.se
```
git clone https://github.com/letsencrypt/letsencrypt
sudo dd if=/dev/zero of=/swapfile bs=1024 count=524288
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
sudo letsencrypt/letsencrypt-auto certonly --standalone --agree-tos --redirect --duplicate --text --email davidbaeriksson@gmail.com -d krogrouletten.se -d www.krogrouletten.se
sudo swapoff /swapfile
```

```
sudo dd if=/dev/zero of=/swapfile bs=1024 count=524288
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile

docker-compose stop
/opt/letsencrypt/letsencrypt-auto renew
sudo swapoff /swapfile
docker-compose up -d
```