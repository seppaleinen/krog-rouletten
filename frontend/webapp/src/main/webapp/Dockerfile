#FROM python:3.4-onbuild
FROM python:alpine3.16
ADD . /webapp

ENV PYTHONUNBUFFERED 0

WORKDIR /webapp
RUN pip install -r requirements.txt