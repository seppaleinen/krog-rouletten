FROM debian:jessie


RUN apt-get update -y && \
    apt-get install -y curl unzip nodejs imagemagick xvfb chromium && \
    ln -s /usr/bin/nodejs /usr/sbin/node

RUN mkdir wpt && \
    curl -L -o wpt/webpagetest.zip 'https://github.com/WPO-Foundation/webpagetest/releases/download/WebPageTest-2.19/webpagetest_2.19.zip' && \
    cd wpt && \
    unzip webpagetest.zip && \
    rm -f webpagetest.zip && \
    chmod +x mobile/wptdriver.sh && \
    chmod +x mobile/lib/webdriver/chromedriver/Linux\ x86_64/chromedriver-2.4 && \
    chmod +x mobile/ipfw_config


ADD xvfb-chromium /usr/bin/xvfb-chromium

RUN chmod +x /usr/bin/xvfb-chromium && \
    ln -s /usr/bin/xvfb-chromium /usr/bin/chrome

RUN apt-get autoclean -y && \
    apt-get clean -y && \
    apt-get autoremove && \
    apt-get autoremove -y curl unzip
