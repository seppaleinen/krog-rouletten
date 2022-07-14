#FROM python:3.4-onbuild
FROM python:alpine3.16

ENV PYTHONUNBUFFERED 1

ADD requirements.txt /webapp/
WORKDIR /webapp
RUN pip install -r requirements.txt

ADD . /webapp