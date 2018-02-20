#FROM python:3.4-onbuild
FROM jfloff/alpine-python:3.4
ADD . /webapp

ENV PYTHONUNBUFFERED 0

WORKDIR /webapp
RUN pip install --upgrade pip && \
    pip install --upgrade setuptools && \
    pip install -r requirements.txt