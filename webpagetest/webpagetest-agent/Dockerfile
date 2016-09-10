FROM ubuntu

RUN apt-get update -y

RUN apt-get install nodejs imagemagick curl unzip -y && \
    curl -L -o google-chrome-stable_current_amd64.deb 'https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb' && \
    apt-get install -y gconf-service libasound2 libgconf-2-4 libnspr4 libnss3 libpango1.0-0 fonts-liberation libcurl3 xdg-utils libxss1 libappindicator1 libindicator7 wget && \
    dpkg -i google-chrome-stable_current_amd64.deb && \
    ln -s /usr/bin/nodejs /usr/sbin/node && \
    ln -s /usr/bin/google-chrome /usr/bin/chrome

RUN apt-get update -y && \
    apt-get install -y xvfb fluxbox x11vnc dbus libasound2 libqt4-dbus libqt4-network libqtcore4 libqtgui4 libxss1 libpython2.7 libqt4-xml libaudio2 fontconfig lib32stdc++6  lib32ncurses5 libc6-i386 lib32gcc1 nano && \
    apt-get install -y python-gobject-2 && \
    #apt-get install -y libmng1 liblcms1 lib32asound2
    apt-get install -y curl git

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

#https://github.com/WPO-Foundation/webpagetest/issues/94