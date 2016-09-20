# Sentry

Sentry is a tool for analyzing stacktraces in running container.

* Backend sends errors through logback
* Frontend sends errors through raven

For now it's defaulted to sending logs to sentry.io,
but start this service, and change dsn to

http://3ed6475242b24270b6c3ab96b692cd38:99f9c9a5a601403fb5c84e993c3caad5@192.168.99.100:9000/2


#### Commands
```
docker-compose down;
docker-compose build && docker-compose up
```