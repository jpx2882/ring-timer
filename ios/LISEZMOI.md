# Ring Timer : installer sur ton iPhone

Il faut un Mac avec **Xcode 16 ou plus récent**, ton iPhone et un câble.

1. Dézippe le dossier, puis double-clique sur **RingTimer.xcodeproj**.
2. Dans Xcode, clique sur **RingTimer** tout en haut de la colonne de gauche, puis sur l'onglet **Signing & Capabilities**.
3. Dans **Team**, choisis **Add an Account…**, connecte-toi avec ton identifiant Apple, puis sélectionne « (Personal Team) ».
   - Si Xcode dit que le **Bundle Identifier** est déjà pris, change la fin (par exemple `com.ringtimer.boxing` suivi de ton prénom).
4. Branche ton iPhone au Mac et déverrouille-le. Accepte « Faire confiance à cet ordinateur ».
5. Sur l'iPhone, active le **Mode développeur** : Réglages, Confidentialité et sécurité, Mode développeur. L'iPhone redémarre.
6. En haut d'Xcode, choisis ton iPhone dans la liste des appareils, puis clique sur **▶︎** (ou Cmd+R).
7. Au premier lancement, l'iPhone peut bloquer l'app. Va dans Réglages, Général, VPN et gestion de l'appareil, touche ton identifiant Apple, puis **Faire confiance**.

Avec un compte Apple gratuit, l'app fonctionne **7 jours**. Ensuite, rebranche l'iPhone et clique à nouveau sur ▶︎.
Avec le compte Apple Developer (99 € par an), elle dure 1 an et tu peux la publier sur l'App Store.

Tu peux aussi tester sans iPhone : choisis un simulateur (par exemple « iPhone 16 ») à l'étape 6.

## Contenu
- Code Swift (SwiftUI) dans `RingTimer/`
- Polices Bangers, Russo One et Barlow Condensed (licence libre SIL OFL, fichiers dans `RingTimer/Fonts`)
- 5 ambiances sonores (Boxe, Muscu, Sifflet, Arcade, Gong) dans `RingTimer/Sounds`
- Icône dans `RingTimer/Assets.xcassets`
