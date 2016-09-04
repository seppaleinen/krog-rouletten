FROM ubuntu

RUN apt-get update -y

RUN apt-get install nodejs imagemagick curl unzip -y && \
#    wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb && \
#    apt-get install -y gconf-service libasound2 libgconf-2-4 libnspr4 libnss3 libpango1.0-0 fonts-liberation libcurl3 xdg-utils && \
#    dpkg -i google-chrome-stable_current_amd64.deb && \
    ln -s /usr/bin/nodejs /usr/sbin/node

RUN mkdir wpt && \
    curl -L -o wpt/webpagetest.zip 'https://github.com/WPO-Foundation/webpagetest/releases/download/WebPageTest-2.19/webpagetest_2.19.zip' && \
    cd wpt && \
    unzip webpagetest.zip && \
    rm -f webpagetest.zip && \
    chmod +x mobile/wptdriver.sh

RUN apt-get autoclean -y && \
    apt-get clean -y && \
    apt-get autoremove && \
    apt-get autoremove -y curl unzip