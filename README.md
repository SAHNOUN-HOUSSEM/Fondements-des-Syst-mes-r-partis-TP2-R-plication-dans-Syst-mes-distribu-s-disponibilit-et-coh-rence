Dans ce TP, nous voudrons réaliser un prototype d’application réparties pour la réplication des
données (Base de données, …, ou pour simplifier un Fichier Texte) sur plusieurs machines ou
disques (pour simplifier nous allons travailler sur une seule machine et définir à la place un
répertoire par réplication ou disque). Nous allons choisir deux stratégies pour la gestion des
réplications. La première qui va favoriser la cohérence (consistancy) des données sur les
réplications. La deuxième qui va faire un certain compromis entre la disponibilité et la cohérence.
Pour ce TP, nous allons utiliser RabbitMQ comme middleware MoM pour l’échanges entre les
différentes entités (ou processus) de l’application répartie. On suppose également 3 programmes
ou processus : Un processus ClientWriter (un processus client qui lance des transactions de
lignes à ajouter dans les fichiers Textes), un autre processus ClientReader (un processus client
qui lit les lignes des fichiers Textes) et un processus Replica (un processus qui permet de
répondre aux requêtes des clients que se soit pour la lecture ou l’écriture). Dans l’exemple
suivant, on définit les deux traitements à savoir l’écriture du client puis la lecture du client.
