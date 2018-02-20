PKGS := $(sh go list ./... | grep -v /vendor)

.PHONY: test
#test: lint
test:
	go test $(PKGS) test/*.go

BIN_DIR := $(GOPATH)/bin
GOMETALINTER := $(BIN_DIR)/gometalinter

$(GOMETALINTER):
	go get -u github.com/alecthomas/gometalinter
	gometalinter --install

#.PHONY: lint
#lint: $(GOMETALINTER)
#	gometalinter src/* --vendor


BINARY := api

.PHONY: windows
windows:
	mkdir -p release
	GOOS=windows GOARCH=amd64 go build -i -o release/$(BINARY)-v1.0.0-windows-amd64 src/*.go

.PHONY: linux
linux:
	mkdir -p release
	GOOS=linux GOARCH=amd64 go build -i -o release/$(BINARY)-v1.0.0-linux-amd64 src/*.go

.PHONY: darwin
darwin:
	mkdir -p release
	GOOS=darwin GOARCH=amd64 go build -i -o release/$(BINARY)-v1.0.0-darwin-amd64 src/*.go

.PHONY: release
release: test windows linux darwin