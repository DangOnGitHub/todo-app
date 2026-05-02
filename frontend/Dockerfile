FROM node:24-alpine AS build
RUN apk upgrade
WORKDIR /workspace/frontend

COPY frontend/package*.json ./
RUN npm ci

COPY openapi.yaml /workspace/openapi.yaml
COPY frontend/ .
ARG VITE_API_URL=http://localhost:8080
ENV VITE_API_URL=${VITE_API_URL}
RUN npm run generate && npm run build

FROM nginx:alpine
RUN apk upgrade
COPY --from=build /workspace/frontend/dist /usr/share/nginx/html
COPY frontend/nginx.conf /etc/nginx/conf.d/default.conf
