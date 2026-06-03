#!/bin/bash

# Dossier de destination sur la machine
TARGET_DIR="/tmp/journal_de_bord/avatars"

echo "⏳ Création du dossier d'accueil : $TARGET_DIR"
mkdir -p "$TARGET_DIR"

echo "📥 Téléchargement des avatars depuis Pravatar..."

# Téléchargement des images fixes pour chaque utilisateur
curl -s -L "https://i.pravatar.cc/150?img=47" -o "$TARGET_DIR/avatar_alice.png"
curl -s -L "https://i.pravatar.cc/150?img=33" -o "$TARGET_DIR/avatar_bob.png"
curl -s -L "https://i.pravatar.cc/150?img=12" -o "$TARGET_DIR/avatar_charlie.png"

echo "✅ Fin du téléchargement !"
echo "📁 Les fichiers suivants ont été créés :"
ls -l "$TARGET_DIR"