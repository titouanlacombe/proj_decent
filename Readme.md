# Projet Décentralisé

## Dépendances

Java: `sudo apt install default-jre default-jdk`

## Conception / Idees

- Musée
  > Registre général qui contient le nombre de personnes et les controleurs
- Controleur
  > Client/Serveur qui peut laisser passer une personne si elle possède le token et que la condition de passage est respectée
- Communication entre les controleurs
  > Chaque controleur communique avec les autres en passant un "message" au suivant (Token + nb de places restantes)
- Visiteur
  > Nombre (entier) transmis via le message
- Comment assurer l'objectif ?
  > Il n'y a qu'un seul message qui circule dans l'anneau, un contrôleur doit donc vérifier que le token est disponible (avoir reçu le message et le token à 0) avant de tester si le nombre de places restantes est supérieur à 0. Si c'est le cas, il peut laisser passer le visiteur et décrémenter le nombre de places restantes. Il doit ensuite envoyer le message au contrôleur suivant en mettant le token à 1.
- Comment gérer la sortie des personnes à l'interieur du musée
  > Le controleur laisse sortir les personnes et enregistre le nombre de personnes sorties. Il mettra à jour le nombre de places dispnibles lorsqu'il reçoit le token.

On utilisera Java RMI pour la communication entre les controleurs et avec le musée.

## Liste des classes (à accompagner du diagramme de classe)

- Musée

  - Méthode d'initialisation des controleurs
    > `public void initControleurs(int nbControleurs, int nbPlaces)`

- Controleur

  - Attributs

    - Token
      > `private Token token`

  - Méthode de réception du message
    > `public void receiveMessage(Message msg)`
  - Méthode de mise à jour du nombre de places disponibles
    > `public void updateNbPlaces(int nbPlaces)`
  - Méthode d'envoi du message
    > `public void sendMessage(Message msg)`

- Token

  - Attributs

    - Valeur de disponibilité
      > `private int available`
    - Nombre de places restantes
      > `private int nbPlacesRestantes`

  - Méthode de modification de la valeur
    > `public void setValue(int value)`
