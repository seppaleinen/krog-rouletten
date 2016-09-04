FROM ubuntu

RUN apt-get update -y && \
    apt-get install nodejs imagemagick curl unzip -y && \
    ln -s /usr/bin/nodejs /usr/sbin/node

# RUN git clone http://github.com/WPO-Foundation/webpagetest.git wpt

RUN mkdir wpt && \
    curl -L -o wpt/webpagetest.zip 'https://github.com/WPO-Foundation/webpagetest/releases/download/WebPageTest-2.19/webpagetest_2.19.zip' && \
    cd wpt && \
    unzip webpagetest.zip && \
    chmod +x mobile/wptdriver.sh

