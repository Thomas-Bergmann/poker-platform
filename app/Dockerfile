ARG VERSION_APP=latest

FROM node:20-alpine as builder
RUN npm install -g @angular/cli

# dependencies of app-project
COPY package.json /workspace/app-project/
WORKDIR /workspace/app-project
RUN npm install

## build of CEC
COPY src/ /workspace/app-project/src/
COPY *.json /workspace/app-project/
WORKDIR /workspace/app-project
RUN npm run build:production

# EXPOSE 4200/tcp

# ENTRYPOINT ["ng", "serve","--disableHostCheck", "--host", "0.0.0.0"]

### STAGE 2: nginx environment ###
FROM nginx:1.25.4-alpine

## Remove default nginx website
RUN rm -rf /usr/share/nginx/html/*

## From ‘builder’ stage copy over the artifacts in dist folder to default nginx public folder
COPY --from=builder /workspace/app-project/dist/poker-app /usr/share/nginx/html
COPY nginx/default.conf /etc/nginx/conf.d

EXPOSE 80
VOLUME [/usr/share/nginx/html/assets/config]

HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=3 CMD [ "curl http://localhost:80" ]

ENTRYPOINT ["nginx", "-g", "daemon off;"]
