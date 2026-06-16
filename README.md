# 🫀 Kalbim – Herzgesundheit-Tracker

**Kalbim** (türkisch für „Mein Herz") ist eine native Android-Anwendung für Patienten mit 
Herzinsuffizienz oder erhöhtem Herzrisiko. Die App ermöglicht die systematische Erfassung, 
Visualisierung und den Export von medizinischen Messwerten – vollständig offline und datenschutzkonform.

> ⚠️ **Wichtiger Hinweis:** Diese App dient ausschließlich zur persönlichen Aufzeichnung von 
> Gesundheitsdaten. Sie bietet **keine medizinische Beratung, Diagnose oder Behandlung**. 
> Bitte konsultieren Sie stets Ihren Arzt.

---

## 📱 Screenshots

![1](assets/1.png)

---

## ✨ Funktionen

### 📊 Messwert-Erfassung
- **Blutdruck** (systolisch / diastolisch in mmHg)
- **Puls** (bpm)
- **Körpergewicht** (kg)
- **NT-proBNP / BNP** (pg/mL)
- **Troponin T/I** (ng/L)

### 📈 Visualisierung
- Interaktive Liniendiagramme für alle Messwerte
- Filterung nach Zeitraum (letzte 7 / 30 Tage oder benutzerdefiniert)
- Übersichtskarten mit aktuellen Werten auf dem Dashboard

### 📄 PDF-Berichte
- Professionelle Berichte mit Patientendaten, Krankengeschichte und Medikamentenliste
- Diagramme werden direkt in das PDF eingebettet
- Datumsbereichsfilter für gezielte Auswertungen
- Direkt teilbar (E-Mail, WhatsApp, Drucken etc.)

### 💊 Medikamentenverwaltung
- Erfassung von Medikamentenname, Dosierung und Einnahmezeiten
- Tagesdosis-Konfiguration (Morgens / Mittags / Abends / Nachts)
- Dosisänderungen mit Notizen protokollierbar
- Aktiv/Passiv-Status für abgesetzte Medikamente

### 🔔 Erinnerungssystem
- Tägliche Erinnerungen für Gewichtsmessung (Morgens)
- Erinnerungen für Blutdruckmessungen (Mittags / Abends)
- Individuelle Erinnerungszeiten pro Medikament und Einnahmezeitpunkt
- Funktioniert auch nach Geräteneustart (WorkManager)

### 💾 Datensicherung
- Export aller Daten als JSON-Datei
- Import / Wiederherstellung aus JSON-Backup
- Vollständig lokal – keine Cloud, kein Server

### 🎨 Benutzeroberfläche
- Modernes Material Design 3
- Dark Mode / Light Mode / Automatisch (Systemeinstellung)
- Mehrsprachig: 🇹🇷 Türkisch, 🇩🇪 Deutsch, 🇬🇧 Englisch
- Optimiert für ältere Nutzer (große Schrift, kontrastreiche Farben)

---

## 🔒 Datenschutz

- ✅ Alle Daten werden **ausschließlich lokal** auf dem Gerät gespeichert
- ✅ **Keine Internetverbindung** erforderlich
- ✅ Keine Datenübertragung an Server oder Dritte
- ✅ Keine Werbung, kein Tracking

---

## 🛠️ Technologie-Stack

| Bereich | Technologie |
|---|---|
| Programmiersprache | **Kotlin** |
| UI-Framework | **Jetpack Compose** (Material Design 3) |
| Lokale Datenbank | **Room Database** (SQLite) |
| Architektur | **MVVM** (ViewModel + Repository) |
| Hintergrundaufgaben | **WorkManager** (Benachrichtigungen) |
| PDF-Generierung | Android native `PdfDocument` |
| Diagramme | Canvas API (Jetpack Compose) |
| Datensicherung | JSON Export / Import |
| Min. Android-Version | **Android 8.0** (API 26) |

---

## 📐 Architektur
