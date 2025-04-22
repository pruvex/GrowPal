# GrowPal

**Wichtige Hinweise (Stand: 2025-04-22)**

- Komplettes Projekt-Backup: `GrowPal_Backup_2025-04-22.tar.gz`
- Crash beim App-Start (BottomNavItem.Home.route) wurde robust gefixt (Singleton-Zugriff + ProGuard-Regel)
- ProGuard-Konfiguration schützt jetzt die sealed class `BottomNavItem`
- Englische Übersetzung für `stay_logged_in` ergänzt (Lint-Fehler beseitigt)


GrowPal ist eine modulare Android-App für Pflanzenliebhaber, entwickelt mit Kotlin, Jetpack Compose und MVVM-Architektur. 

**Features:**
- Moderne Compose-UI
- MVVM-Architektur
- Navigation Compose
- Firebase-Vorbereitung (Auth/Firestore)
- Modular erweiterbar (Raumverwaltung, Tagebuch, Community etc.)

**Build:**
- Min SDK: 24
- Target SDK: 34
- Compose aktiviert
- Für Firebase: `google-services.json` in `app/` ablegen

**Struktur:**
- `data/` – Repositorys, Modelle
- `viewmodel/` – ViewModels
- `ui/` – Composables, Screens
- `navigation/` – NavGraph
- `util/` – Hilfsklassen

**Start:**
- Startscreen zeigt: „GrowPal ist bereit.“

**Hinweis:**
Weitere Infos und Anleitung folgen mit den nächsten Modulen.
