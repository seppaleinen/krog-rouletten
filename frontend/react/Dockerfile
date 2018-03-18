FROM node:8.4.0-alpine

ADD package.json src public scripts config /application/

EXPOSE 5000
WORKDIR /application

RUN npm install && npm run build && yarn global add serve && rm -rf node_modules/ src/scripts/

ENTRYPOINT ["serve","-s","build"]
