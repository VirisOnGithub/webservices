// Configuration de l'URL racine de ton API (laisse vide si le front est déployé sur le même Tomcat)
const API_BASE_URL = window.location.origin + window.location.pathname.replace("index.html", "");

let currentChannelId = null;

// Éléments du DOM
const channelList = document.getElementById('channel-list');
const chatHeader = document.getElementById('chat-header');
const messageArea = document.getElementById('message-area');
const messageForm = document.getElementById('message-form');
const messageInput = document.getElementById('message-input');
const sendBtn = document.getElementById('send-btn');

// --- 1. CHARGEMENT DES CANAUX (GET /api/channels) ---
async function loadChannels() {
    try {
        const response = await fetch(`${API_BASE_URL}api/channels`);
        if (!response.ok) throw new Error("Impossible de récupérer les canaux");

        const channels = await response.json();
        channelList.innerHTML = ''; // Nettoyer le loader

        if (channels.length === 0) {
            channelList.innerHTML = '<p style="padding:10px; color:#80848e;">Aucun salon disponible</p>';
            return;
        }

        channels.forEach(channel => {
            const div = document.createElement('div');
            div.classList.add('channel-item');
            div.innerText = `# ${channel.name}`;
            div.dataset.idc = channel.idc;

            // Événement au clic : changer de salon
            div.addEventListener('click', () => selectChannel(channel.idc, channel.name));
            channelList.appendChild(div);
        });

    } catch (error) {
        console.error(error);
        channelList.innerHTML = '<p style="padding:10px; color:#f23f43;">Erreur de chargement</p>';
    }
}

// --- 2. SÉLECTION D'UN SALON ---
function selectChannel(idc, name) {
    currentChannelId = idc;

    // Mettre à jour l'état visuel actif dans la barre latérale
    document.querySelectorAll('.channel-item').forEach(item => {
        item.classList.toggle('active', item.dataset.idc === idc);
    });

    // Activer le champ de saisie
    chatHeader.innerText = `# ${name}`;
    messageInput.disabled = false;
    sendBtn.disabled = false;
    messageInput.placeholder = `Envoyer un message dans #${name}`;

    // Charger l'historique
    loadMessages(idc);
}

// Variable globale en haut de app.js pour mettre en cache les URLs des Avatars créées
const avatarCache = new Map();

// --- 3. CHARGEMENT DES MESSAGES AVEC SYSTÈME DE CACHE D'AVATARS ---
async function loadMessages(idc) {
    try {
        const response = await fetch(`${API_BASE_URL}api/messages?idc=${idc}`);
        if (!response.ok) throw new Error("Erreur de récupération des messages");

        const messages = await response.json();
        messageArea.innerHTML = '';

        if (messages.length === 0) {
            messageArea.innerHTML = '<p style="color:#80848e; text-align:center; margin-top:20px;">C\'est le début de l\'histoire de ce salon.</p>';
            return;
        }

        // On boucle sur chaque message
        for (const msg of messages) {
            const msgDiv = document.createElement('div');
            msgDiv.classList.add('message');

            const date = new Date(msg.sendDate);
            const formattedTime = date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

            // Identifiant unique ou nom de fichier de l'avatar (ex: 'alice.jpg')
            const avatarFilename = msg.author.avatar;
            let resolvedAvatarUrl = "";

            if (!avatarFilename) {
                // Secours si pas d'avatar spécifié
                resolvedAvatarUrl = `https://ui-avatars.com/api/?name=${encodeURIComponent(msg.author.pseudo)}`;
            } else if (avatarCache.has(avatarFilename)) {
                // STRATÉGIE CACHE : Si l'avatar a déjà été récupéré par l'API une fois, on réutilise l'URL locale en mémoire
                resolvedAvatarUrl = avatarCache.get(avatarFilename);
            } else {
                // PREMIER APPEL API pour cet avatar spécifique
                try {
                    const avatarResponse = await fetch(`${API_BASE_URL}api/avatars?filename=${avatarFilename}`);
                    if (avatarResponse.ok) {
                        const imageBlob = await avatarResponse.blob();
                        // Crée une URL binaire éphémère utilisable directement dans la balise <img>
                        resolvedAvatarUrl = URL.createObjectURL(imageBlob);
                        // On stocke dans le cache pour les prochains messages/salons
                        avatarCache.set(avatarFilename, resolvedAvatarUrl);
                    } else {
                        throw new Error();
                    }
                } catch (e) {
                    // Secours en cas d'erreur de la servlet d'avatar
                    resolvedAvatarUrl = `https://ui-avatars.com/api/?name=${encodeURIComponent(msg.author.pseudo)}`;
                }
            }

            msgDiv.innerHTML = `
                <div class="avatar-container">
                    <img src="${resolvedAvatarUrl}" alt="Avatar de ${msg.author.pseudo}">
                </div>
                <div class="message-details">
                    <div class="message-meta">
                        <span class="message-author">${msg.author.pseudo}</span>
                        <span class="message-date">Aujourd'hui à ${formattedTime}</span>
                    </div>
                    <div class="message-content">${msg.content}</div>
                </div>
            `;
            messageArea.appendChild(msgDiv);
        }

        messageArea.scrollTop = messageArea.scrollHeight;

    } catch (error) {
        console.error(error);
        messageArea.innerHTML = '<p style="color:#f23f43; text-align:center;">Erreur d\'actualisation des messages.</p>';
    }
}

// --- 4. ENVOI D'UN MESSAGE (POST /api/messages) ---
messageForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const content = messageInput.value.trim();

    if (!content || !currentChannelId) return;

    const payload = {
        content: content,
        channel: {
            idc: currentChannelId
        }
    };

    try {
        const response = await fetch(`${API_BASE_URL}api/messages`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) throw new Error("Échec de l'envoi");

        messageInput.value = ''; // Effacer le champ texte
        loadMessages(currentChannelId); // Recharger la vue du chat

    } catch (error) {
        alert("Impossible d'envoyer le message : " + error.message);
    }
});

// Lancement au chargement de la page
window.addEventListener('DOMContentLoaded', loadChannels);