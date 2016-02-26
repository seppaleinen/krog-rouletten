# Frontend

Framework:	Flask
Language:	Javascript/Python
Dependencies: [Python](https://www.python.org/downloads)

To install dependencies
```
cd src/main/webapp;
pip install -r requirements.txt
```

To build and start server on port 8000
```
cd src/main/webapp;
gunicorn --config=gunicorn.config.py wsgi:app
```

To build docker image
```
mvn clean install -Pdocker
```


## TODO
frameworks för att skapa web/ios/android appar
http://ionicframework.com/getting-started/ 
http://foundation.zurb.com/ 
