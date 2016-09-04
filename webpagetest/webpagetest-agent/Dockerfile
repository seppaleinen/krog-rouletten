FROM ubuntu

RUN apt-get update -y && \
    apt-get install nodejs imagemagick git -y && \
    ln -s /usr/bin/nodejs /usr/sbin/node

RUN git clone http://github.com/WPO-Foundation/webpagetest.git wpt
