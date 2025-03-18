FROM node:22.13.0-alpine AS builder

WORKDIR /app

#Installing dependencies separately
COPY package*.json ./
RUN npm install

#Copying app source
COPY . .
RUN npm run build -- --configuration production

#Serving with NGINX
FROM nginx:alpine

#Removing default nginx config
ARG CACHEBUSTER=1
RUN echo "Busting cache: $CACHEBUSTER" && rm /etc/nginx/conf.d/default.conf

#Copy custom nginx config
COPY ./nginx/default.conf /etc/nginx/conf.d/default.conf

#copy angular build output
COPY --from=builder /app/dist/angular-fleets/browser /usr/share/nginx/html

#caching-busting layer to force fresh layers on rebuilds
ARG CACHE_BUSTER=dev
RUN  echo "Using cache buster: $CACHE_BUSTER"

#exposing port 80 for HTTP traffic
EXPOSE 80