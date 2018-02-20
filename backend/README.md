# API


[https://golang.org/doc/install](https://golang.org/doc/install)
or with homebrew on mac
```bash
brew install go
```

### Endpoints are:
```
GET http://localhost:8080/
GET http://localhost:8080/todos
POST http://localhost:8080/todos
GET http://localhost:8080/todos/{id}
```

### JSON Object
{"id":2,"name":"Name","completed":false,"due":"0001-01-01T00:00:00Z"}

```bash
# To check that server is up
curl 'http://localhost:8080/'
# To add entry
curl -X POST 'http://localhost:8080/todos' -d '{"name":"Name","completed":false,"due":"0001-01-01T00:00:00Z"}'
# To get entry
curl 'http://localhost:8080/todos/1' 
# To list all entries
curl 'http://localhost:8080/todos'
```

### Commands

```bash
make

make release

go build -o api src/*.go 

go test src/*.go
```
