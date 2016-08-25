# NGINX server

To make it easier to integrate a real ssl-certificate on a server, I've
installed a self-signed certificate in the docker-image, which I exchange
later for the real certificate.


[Local CDN in nGinx](https://jesus.perezpaz.es/2014/02/configure-subdomain-as-cdn-in-nginx-wordpress-w3-total-cache-configurations/)


```
git clone https://github.com/letsencrypt/letsencrypt
sudo dd if=/dev/zero of=/swapfile bs=1024 count=524288
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
letsencrypt/letsencrypt-auto certonly --server https://acme-v01.api.letsencrypt.org/directory -a webroot --webroot-path=/tmp/letsencrypt-auto --agree-dev-preview -d krogrouletten.se -d www.krogrouletten.se
cd krog-rouletten
sudo swapoff /swapfile
```

```
sudo dd if=/dev/zero of=/swapfile bs=1024 count=524288
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile

docker-compose stop
letsencrypt/letsencrypt-auto --renew-by-default certonly --server https://acme-v01.api.letsencrypt.org/directory -a webroot --webroot-path=/tmp/letsencrypt-auto --agree-dev-preview -d krogrouletten.se -d www.krogrouletten.se
sudo swapoff /swapfile
cd krog-rouletten
docker-compose up -d
```