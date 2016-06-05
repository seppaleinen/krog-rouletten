# krog-rouletten
Krogrouletten

[Installera git](htts://git-scm.com/download/win)
[Installera maven](http://www.mkyong.com/maven/how-to-install-maven-in-windows/)
[Installera databasen mongo](https://www.mongodb.org/downloads)
[Installera docker](https://www.docker.com/)
[Installera docker-compose](https://docs.docker.com/compose/)

För att ta hem projektet
```
git clone ssh://git@github.com:22/seppaleinen/krog-rouletten
```

För att köra igång projektet i helhet
## Docker commands
```
docker-compose pull (för att ladda ner alla uppladdade image-filer
docker-compose build
docker-compose up (för att starta image-filerna)
docker-compose kill (för att stänga av docker instanserna
```

För att köra projektet i utvecklingsmode
```
mvn clean install (Kompilerar projektet)
java -jar backend/target/krogrouletten-backend.jar (Startar backend servern)
cd frontend/webapp/src/main/webapp
python start_tornado (Starta frontend server)
```

## Git commands
```
git add -A (för att lägga till alla filer du har ändrat)
git commit -m "text" (För att sätta ett namn på din incheckning)
git push (för att checka in dina förändringar)
git pull (för att ta hem mina förändringar)
```
