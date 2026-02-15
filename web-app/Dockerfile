# 1단계: Vue 빌드 (Node.js 환경)
FROM node:lts-alpine as build-stage
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# 2단계: 배포 (Nginx 환경)
FROM nginx:stable-alpine as production-stage
# Vue 빌드 결과물만 복사
COPY --from=build-stage /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]