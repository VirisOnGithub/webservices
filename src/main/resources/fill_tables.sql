-- 1. Insertion des Utilisateurs (Mots de passe fictifs pour le moment)
INSERT INTO USERS (pseudo, email, password, avatar)
VALUES ('Alice', 'alice@polytech.fr', 'passAlice123', 'avatar_alice.png'),
       ('Bob', 'bob@polytech.fr', 'passBob456', 'avatar_bob.png'),
       ('Charlie', 'charlie@polytech.fr', 'passCharlie789', 'avatar_charlie.png');

-- 2. Insertion des Canaux (Alice crée le général, Bob crée le projet secret)
INSERT INTO CHANNEL (name, description, is_public, idu)
VALUES ('Général', 'Salon de discussion principal ouvert à tous.', TRUE,
        (SELECT idu FROM USERS WHERE email = 'alice@polytech.fr')),
       ('Projet Secret', 'Espace privé pour l''équipe du TP4.', FALSE,
        (SELECT idu FROM USERS WHERE email = 'bob@polytech.fr'));

-- 3. Gestion des membres pour le canal privé (Seuls Alice et Bob y ont accès)
INSERT INTO belong (idu, idc)
VALUES ((SELECT idu FROM USERS WHERE email = 'alice@polytech.fr'),
        (SELECT idc FROM CHANNEL WHERE name = 'Projet Secret')),
       ((SELECT idu FROM USERS WHERE email = 'bob@polytech.fr'),
        (SELECT idc FROM CHANNEL WHERE name = 'Projet Secret'));
-- Bob est dans le projet secret

-- 4. Insertion d'une dizaine de messages
-- Dans le canal Général (c1)
INSERT INTO MESSAGE (content, idu, idc)
VALUES ('Hello tout le monde ! Bienvenue sur le serveur.',
        (SELECT idu FROM USERS WHERE email = 'alice@polytech.fr'),
        (SELECT idc FROM CHANNEL WHERE name = 'Général')),
       ('Salut Alice ! Content d''être là.',
        (SELECT idu FROM USERS WHERE email = 'bob@polytech.fr'),
        (SELECT idc FROM CHANNEL WHERE name = 'Général')),
       ('Hey ! Est-ce que quelqu''un a compris la phase 2 du projet ?',
        (SELECT idu FROM USERS WHERE email = 'charlie@polytech.fr'),
        (SELECT idc FROM CHANNEL WHERE name = 'Général')),
       ('Oui Charlie, il faut exposer des servlets qui renvoient du JSON.',
        (SELECT idu FROM USERS WHERE email = 'alice@polytech.fr'),
        (SELECT idc FROM CHANNEL WHERE name = 'Général')),
       ('Ça marche, merci !',
        (SELECT idu FROM USERS WHERE email = 'charlie@polytech.fr'),
        (SELECT idc FROM CHANNEL WHERE name = 'Général'));

-- Dans le canal Privé (c2)
INSERT INTO MESSAGE (content, idu, idc)
VALUES ('Bienvenue dans le salon secret du projet.',
        (SELECT idu FROM USERS WHERE email = 'bob@polytech.fr'),
        (SELECT idc FROM CHANNEL WHERE name = 'Projet Secret')),
       ('Top, on va pouvoir partager les réponses ici.',
        (SELECT idu FROM USERS WHERE email = 'alice@polytech.fr'),
        (SELECT idc FROM CHANNEL WHERE name = 'Projet Secret')),
       ('Tu as avancé sur le script SQL ?',
        (SELECT idu FROM USERS WHERE email = 'bob@polytech.fr'),
        (SELECT idc FROM CHANNEL WHERE name = 'Projet Secret')),
       ('Oui, j''ai corrigé les clés étrangères pour éviter les blocages.',
        (SELECT idu FROM USERS WHERE email = 'alice@polytech.fr'),
        (SELECT idc FROM CHANNEL WHERE name = 'Projet Secret'));

-- Exemple d'un message qui est une réponse (Thread) à m9 dans c2
INSERT INTO MESSAGE (content, idu, idc, idm_2)
VALUES ('Génial, tu gères !',
        (SELECT idu FROM USERS WHERE email = 'bob@polytech.fr'),
        (SELECT idc FROM CHANNEL WHERE name = 'Projet Secret'),
        (SELECT m.idm FROM MESSAGE m
         JOIN CHANNEL c ON c.idc = m.idc
         WHERE c.name = 'Projet Secret'
           AND m.content = 'Oui, j''ai corrigé les clés étrangères pour éviter les blocages.'));
