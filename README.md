# 🥊 Ring Timer

**Timer de boxe et d'entraînement, gratuit et en code ouvert.**
**Free, source-available boxing & workout timer. Not for sale.**

![Ring Timer](screenshots/preview.png)

[Français](#français) · [English](#english)

---

## Français

Un timer ultra simple pour la boxe, la muscu et le cardio.

- **4 modes** en bas de l'écran : Échauffement (orange), Entraînement (rouge), Repos (jaune), Étirements (bleu)
- **Rounds** réglables avec − / +, repos automatique entre les rounds
- **Presets** de temps en un tap, plus un bouton **Perso** pour régler le temps au choix
- **Style jeu de combat** : 2 polices (Street, Iron) et 5 couleurs de texte
- **5 ambiances sonores** : Boxe, Muscu, Sifflet, Arcade, Gong
- **Français / English**
- Tout est **sauvegardé** sur l'appareil. Aucune pub, aucun compte, aucune donnée collectée.

### Utiliser la version web (iPhone, Android, ordinateur)
Ouvre le lien GitHub Pages du projet dans Safari, puis **Partager → Sur l'écran d'accueil**.
L'app s'ouvre en plein écran et marche sans internet.

### Installer l'app iPhone (Xcode)
Il faut un Mac avec Xcode 16 ou plus récent.
1. Ouvre `ios/RingTimer.xcodeproj`.
2. **Signing & Capabilities → Team** : choisis ton identifiant Apple. Si besoin, change le *Bundle Identifier*.
3. Branche ton iPhone, active le **Mode développeur** (Réglages → Confidentialité et sécurité).
4. Choisis ton iPhone en haut d'Xcode et clique sur ▶︎.

Avec un compte Apple gratuit, l'app doit être réinstallée tous les 7 jours.

### Contribuer
Les idées, bugs et améliorations sont les bienvenus : ouvre une *issue* ou une *pull request*.

---

## English

A dead-simple timer for boxing, gym and cardio.

- **4 modes** at the bottom: Warm up (orange), Training (red), Rest (yellow), Stretching (blue)
- Adjustable **rounds** with − / +, automatic rest between rounds
- One-tap time **presets**, plus a **Custom** button for any time
- **Fighting-game style**: 2 fonts (Street, Iron) and 5 text colors
- **5 sound themes**: Boxing, Gym, Whistle, Arcade, Gong
- **English / Français**
- Everything is **saved** on the device. No ads, no account, no data collected.

### Web version (iPhone, Android, desktop)
Open the project's GitHub Pages link in Safari, then **Share → Add to Home Screen**.
It runs full screen and works offline.

### iPhone app (Xcode)
Requires a Mac with Xcode 16 or newer.
1. Open `ios/RingTimer.xcodeproj`.
2. **Signing & Capabilities → Team**: pick your Apple ID. Change the *Bundle Identifier* if needed.
3. Plug in your iPhone and turn on **Developer Mode** (Settings → Privacy & Security).
4. Select your iPhone at the top of Xcode and press ▶︎.

With a free Apple account the app must be reinstalled every 7 days.

### Contributing
Ideas, bug reports and improvements are welcome: open an issue or a pull request.

---

## Structure

```
docs/   Web app (installable, works offline) — served by GitHub Pages
ios/    Native SwiftUI app (Xcode project)
```

## Licence / License

**FR** : le code est sous licence [PolyForm Noncommercial 1.0.0](LICENSE.md).
Tu peux l'utiliser, l'installer sur ton iPhone, le modifier et le partager **gratuitement**.
**Il est interdit de le vendre** ou de l'utiliser dans un but commercial.
Si tu partages une version modifiée, utilise un autre nom et une autre icône.

**EN**: the code is licensed under [PolyForm Noncommercial 1.0.0](LICENSE.md).
You may use it, install it on your iPhone, modify it and share it **for free**.
**Selling it or any commercial use is not allowed.**
If you share a modified version, please use a different name and icon.

Copyright (c) 2026 jpx2882. Fonts: SIL Open Font License 1.1.
