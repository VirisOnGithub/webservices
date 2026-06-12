FROM node:20-alpine
WORKDIR /app

# On installe d'abord les dépendances (optimisation du cache Docker)
COPY package*.json ./
RUN npm install

# On copie le reste du code et on build
COPY . .
RUN npm run build

EXPOSE 3000

# Commande de démarrage par défaut pour Nuxt 3
CMD ["node", ".output/server/index.mjs"]

# 💡 Note : Si tu utilises Nuxt 2, remplace la ligne CMD par :
# CMD ["npm", "run", "start"]