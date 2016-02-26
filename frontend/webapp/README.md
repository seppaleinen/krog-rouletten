# Frontend

Framework:	Flask
Language:	Javascript/Python
Dependencies:	krog-rouletten-backend, Gunicorn http://docs.gunicorn.org/en/stable/install.html


To build and start server on port 8000
```
cd src/main/webapp;
gunicorn --config=gunicorn.config.py wsgi:app;
```

To build docker image
```
mvn clean install -Pdocker
```
