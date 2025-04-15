# POC - Communication Server Sent Events (SSE)

Ce projet est une démonstration technique (POC) mettant en œuvre la technologie Server Sent Events pour établir une communication temps réel unidirectionnelle du serveur vers le client.

## Qu'est-ce que Server Sent Events?

Les Server Sent Events (SSE) constituent un standard web permettant à un serveur d'envoyer des mises à jour automatiques vers un client à travers une connexion HTTP. 
Contrairement aux WebSockets qui sont bidirectionnels, SSE est optimisé pour une communication unidirectionnelle (serveur → client).

## Composants principaux

### Composant Serveur

Le serveur SSE est responsable de :
- Générer et envoyer des événements
- Gérer les connexions souscrites

**Technologies utilisées :**
- Java avec Spring Boot pour le serveur HTTP
- SseEmitter de Spring Framework pour la gestion des événements SSE
- Spring Modulith pour simuler un traitement asynchrone par message

### Composant Client

Le client SSE s'appuie sur l'API EventSource native du navigateur pour :
- Établir une connexion avec le serveur
- Recevoir et traiter les événements
- Gérer les déconnexions et reconnexions automatiques

## Installation et démarrage rapide

### Backend (Spring Boot)

```bash
# Installation des dépendances et compilation
mvn clean install

# Lancement du serveur
mvn spring-boot:run
```

### Frontend (Angular)

```bash
# Installation des dépendances
npm install

# Lancement du serveur de développement
npm start
```

## Avantages de SSE

- **Simplicité** : API web standard, facile à implémenter
- **Léger** : Utilise HTTP standard, sans protocole supplémentaire
- **Reconnexion automatique** : Gérée nativement par le navigateur
- **Compatibilité** : Fonctionne avec les proxy et pare-feu HTTP

## Limites

- Communication unidirectionnelle uniquement
- Nombre limité de connexions simultanées
- Support navigateur moins universel que pour XHR