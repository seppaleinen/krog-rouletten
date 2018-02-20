FROM jfloff/alpine-python:3.4

ADD . /webapp

ENV PYTHONUNBUFFERED 0

EXPOSE 8002

WORKDIR /webapp
RUN pip install --upgrade pip && \
    pip install --upgrade setuptools && \
    pip install -r requirements.txt

ENTRYPOINT ["gunicorn", "--config=gunicorn.config.py", "wsgi:app"]