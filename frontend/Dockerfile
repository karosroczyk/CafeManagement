FROM node:22 AS build-stage
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
COPY src/environments/environment.docker.ts src/environments/environment.ts
RUN npm run build

FROM nginx:alpine
COPY nginx.conf /etc/nginx/nginx.conf
# 👇 change this line:
COPY --from=build-stage /app/dist/frontend/browser /usr/share/nginx/html
EXPOSE 80
