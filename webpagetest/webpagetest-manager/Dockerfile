FROM php:7.0-apache
MAINTAINER thomas.steinbach@aikq.de

RUN echo deb http://www.deb-multimedia.org jessie main non-free >> /etc/apt/sources.list && \
    apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -q -y --force-yes \
    deb-multimedia-keyring \
    imagemagick \
    libjpeg-progs \
    exiftool \
    unzip \
    wget \
    libfreetype6-dev \
    libjpeg62-turbo-dev \
    libpng12-dev \
    libcurl4-openssl-dev \
    python \
    python-pillow && \
    apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -q -y \
    ffmpeg && \
    apt-get clean && \
    apt-get autoclean

RUN docker-php-ext-configure gd --with-freetype-dir=/usr/include/ --with-jpeg-dir=/usr/include/ && \
    docker-php-ext-install gd && \
    docker-php-ext-install zip && \
    docker-php-ext-install curl && \
    a2enmod expires headers rewrite

RUN cd /tmp && \
    wget https://github.com/WPO-Foundation/webpagetest/releases/download/WebPageTest-2.18/webpagetest_2.18.zip && \
    unzip webpagetest_2.18.zip && \
    cp -a www/* /var/www/html && \
    rm -rf /tmp/* && \
    chown -R www-data:www-data /var/www/html && \
    cd /var/www/html && \
    chmod 0750 tmp && \
    chmod 0750 dat && \
    chmod 0750 results && \
    chmod 0750 work/jobs && \
    chmod 0750 work/video && \
    chmod 0750 logs

COPY php.ini /usr/local/etc/php/php.ini
COPY locations.ini /var/www/html/settings/
COPY connectivity.ini /var/www/html/settings/
COPY settings.ini /var/www/html/settings/