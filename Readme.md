# Projet Décentralisé

## Dépendances

Temp de visite: loi normale
main: gère les personnes
il sorte par une porte aléatoire
le main génère un csv pour graph la taille de la liste d'attente / temps

Java: `sudo apt install openjdk-19-jdk`

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
- Si une personne quitte par une porte, alors elle augmente de 1 le nombre de places disponibles (en mémoire dans le controleur de la porte)
  > On ajoute (nb personnes qui sortent - nb personnes qui entrent) au nombre de places disponibles

On utilisera Java RMI pour la communication entre les controleurs et avec le musée.

## Liste des classes (à accompagner du diagramme de classe)

- Musée

  - Attributs:

    - nbPlaces: int
    - nbControleurs: int

  - Méthode d'initialisation des controleurs
    > `public void initControleurs(int nbControleurs, int nbPlaces)`

- Controleur

  - Attributs

    - next: Controleur (prochain controleur dans l'anneau)

    - token: Token
      > `private Token token`

  - Méthode de réception du message
    > `public void receiveMessage(Message msg)`
  - Méthode de mise à jour du nombre de places disponibles
    > `public void updateNbPlaces(int nbPlaces)`
  - Méthode d'envoi du message
    > `public void sendMessage(Message msg)`

- Token

  - Attributs

    - Valeur de disponibilité : int (0 ou 1)
      > `private int available`
    - Nombre de places restantes
      > `private int nbPlacesRestantes`

  - Méthode de modification de la valeur
    > `public void setValue(int value)`

Sujet : On veut réaliser un système de contrôle d'accès à un musée.  
Le but est de permettre à un nombre limité de personnes de rentrer dans le musée par p portes. Il faut donc gérer un problème de synchronisation entre les p contrôleurs des portes pour éviter de laisser entrer plus de personnes que le nombre de places disponibles (dans le cas où plusieurs personnes arrivent en même temps).  
Notre approche :  
On va utiliser un système avec un ring token. Le token utilisé contiendra également le nombre de places restantes.  
Le musée va initialiser les contrôleurs en leur donnant le nombre de places disponibles.  
Chaque contrôleur va recevoir un message contenant le token et le nombre de places restantes. Il va vérifier que le token est disponible (avoir reçu le message et le token à 0) avant de tester si le nombre de places restantes est supérieur à 0. Si c'est le cas, il peut laisser passer le visiteur et décrémenter le nombre de places restantes. Il doit ensuite envoyer le message au contrôleur suivant en mettant le token à 1.  
Si une personne quitte par une porte, alors elle augmente de 1 le nombre de places disponibles (en mémoire dans le controleur de la porte) et le controleur mettra à jour le nombre de places disponibles lorsqu'il reçoit le token.
