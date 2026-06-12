# Étape 1 : Build de l'application (en supposant que tu utilises Maven)
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
# On copie le descripteur de projet et le code source
COPY pom.xml .
COPY src ./src
# On compile et on package l'application en fichier .war (en ignorant les tests pour aller plus vite)
RUN mvn clean package -DskipTests

# Étape 2 : Déploiement sur Tomcat
# Tomcat 10+ est requis pour Jakarta EE (Tomcat 9 est pour l'ancien Java EE)
FROM tomcat:10.1-jdk17
WORKDIR /usr/local/tomcat

# On supprime les applications par défaut de Tomcat pour faire place nette
RUN rm -rf webapps/*

# On récupère le .war généré à l'étape 1 et on le renomme ROOT.war 
# pour qu'il soit accessible à la racine (http://localhost:8080/)
COPY --from=builder /app/target/*.war webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]