FROM node:22 AS builder
WORKDIR /app
COPY . .
RUN npm install
RUN npm run build --prod

FROM nginx:alpine
COPY --from=builder /app/dist/angular-fleets/browser /usr/share/nginx/html
EXPOSE 80