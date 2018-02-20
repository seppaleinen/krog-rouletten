# API


Framework:	Flask

Language:	Javascript/Python

Dependencies: [Python](https://www.python.org/downloads)

### Endpoints are:
```
GET http://localhost:8000/error/<message>
POST http://localhost:8000/krog/random
POST http://localhost:8000/details/<place_id>
POST http://localhost:8000/details/<place_id>/<location>
```

### Commands to test
```bash
# To check that server is up
curl 'http://localhost:8002/error/hello'
# To add entry
curl -X POST 'http://localhost:8002/krog/random' -d '{"searchtype": "gps", "distance": 200, "latitude": "59.33228889999999", "longitude": "18.0734164"}'
# To get entry
curl -X POST 'http://localhost:8002/details/ChIJkYdudFudX0YRlbnhiqyWAuE' 
# To list all entries
curl -X POST 'http://localhost:8002/details/ChIJkYdudFudX0YRlbnhiqyWAuE/59.33228889999999,18.0734164'
```

### Commands

```bash
# To install libs
pip3 install -r requirements.txt

# To run tests
behave

# To start server
gunicorn --config=gunicorn.config.py wsgi:app
```
