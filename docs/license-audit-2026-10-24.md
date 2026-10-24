# Lizenz-Audit: Umstellung zukünftiger Releases auf MIT (Stand: 2026-10-24)

> Technische Bestandsaufnahme, keine Rechtsberatung.

## 1. Ergebnis

Der Repository-Eigentümer hat bestätigt, dass alle vier in der Git-Historie auftauchenden
Autor-Einträge ihm zuzuordnen sind und keine Rechte Dritter an den Beiträgen bestehen.
Auf dieser Grundlage sind die Repository-Lizenzangaben für **neue Releases ab dem
24.10.2026** auf MIT umgestellt worden. Frühere Releases bleiben unter GNU GPLv3.

Die Bestätigung ist eine Erklärung des Eigentümers, keine unabhängige Prüfung der
Urheberschaft oder Rechtslage. Es wurde kein Release erstellt oder veröffentlicht.

Vor einem MIT-Release müssen die Verteilungslizenzen der gebündelten und transitiven
Abhängigkeiten sowie die erforderlichen Lizenzhinweise im tatsächlich gebauten Artefakt
noch verifiziert werden. Dies betrifft die Release-Compliance, nicht die Lizenzierung
des eigenen Projektcodes.

## 2. Aktueller Lizenzstatus (Beleglage)

- `LICENSE`: MIT License, Copyright (c) 2026 Daniel Rosemann.
- `README.md` und `pom.xml`: MIT für neue Releases ab dem 24.10.2026; frühere Releases
  bleiben GPLv3.
- Im Repository sind keine Tags vorhanden. Die frühere GPL-Lizenzdatei bleibt in den
  entsprechenden historischen Git-Ständen erhalten.
- Commit `236fd54` („refactor: improve professionalism and update to GNU GPLv3"): bewusste GPL-3.0-Wahl.
- Keine `COPYING`, kein `NOTICE`, keine `CLA.md`, kein DCO. `CONTRIBUTING.md` enthält **keine** Rechte- oder
  Lizenzübertragungsklausel.
- Keine SPDX-Identifier und keine Copyright-Header in den geprüften Quellen gefunden
  (Stichprobe: `src/main/java/de/streuland/StreulandPlugin.java`, `src/main/resources/plugin.yml` — ohne Treffer).
- `THIRD_PARTY_NOTICES.md` listet Dritthersteller-Komponenten und enthält bisher einen Hinweis auf
  GPL-3.0-Pflichten. Den Hinweis vor einem künftigen MIT-Release auf die konkreten Abhängigkeiten
  und die jeweilige Distribution abstimmen; historische GPL-Distributionen bleiben davon unberührt.
- Kein Fork-Hinweis, keine Git-Submodule (`git submodule status` leer), kein `package.json` (JS-Dateien unter
  `src/` ohne npm-Manifest — nur Maven ist relevant). `plugins/Streuland/` enthält nur generierte
  `messages_*.yml`. Keine mitgelieferten Fonts/Binärdateien gefunden.

## 3. Abhängigkeitsanalyse (aus `pom.xml`, ohne ausgeführte Builds — Angaben unbestätigt, Prüfhinweise)

| Name | Version | Lizenz (Annahme, unbestätigt) | Typ | Verwendung | Quelle | Bewertung | Nächster Schritt |
|---|---|---|---|---|---|---|---|
| paper-api | 1.16.5-R0.1-SNAPSHOT | GPL-3.0 (Upstream-Angabe prüfen) | direkt, `provided` | Laufzeit-Link gegen Paper-Server, nicht geshadet | pom.xml | bedingt | Upstream-Lizenz der exakten Version am Artefakt prüfen |
| VaultAPI | 1.7 (com.github.MilkBowl via JitPack) | ungeklärt (LGPL-Variante vermutet, unbestätigt) | direkt, `provided` | Economy-Integration zur Laufzeit | pom.xml | ungeklärt | Lizenz der Version 1.7 aus Quelle/SPDX bestätigen |
| snakeyaml | 1.33 | Apache-2.0 (unbestätigt) | direkt, compile → Shade-Kandidat | Config-YAML, wird in Jar eingebettet | pom.xml | unproblematisch (zu bestätigen) | Artefakt-Lizenz prüfen |
| gson | 2.10.1 | Apache-2.0 (unbestätigt) | direkt, compile → Shade-Kandidat | JSON, wird in Jar eingebettet | pom.xml | unproblematisch (zu bestätigen) | Artefakt-Lizenz prüfen |
| Java-WebSocket | 1.5.6 | MIT (unbestätigt) | direkt, compile → Shade-Kandidat | Dashboard-Realtime, wird in Jar eingebettet | pom.xml | unproblematisch (zu bestätigen) | Artefakt-Lizenz prüfen |
| sqlite-jdbc | 3.46.0.0 | Apache-2.0 (unbestätigt) | direkt, compile → Shade-Kandidat | SQLite-Persistenz, wird in Jar eingebettet | pom.xml | unproblematisch (zu bestätigen) | Artefakt-Lizenz prüfen, native Libs beachten |
| junit-jupiter | 5.10.2 | EPL-2.0 (unbestätigt) | Test (`test`-Scope) | Tests, nicht distribuiert | pom.xml | unproblematisch | keine Maßnahme, bei Distribution ausschließen |
| mockito-core | 5.11.0 | MIT (unbestätigt) | Test | Tests, nicht distribuiert | pom.xml | unproblematisch | keine Maßnahme |
| MockBukkit-v1.16 | 1.5.2 | ungeklärt (MIT vermutet, unbestätigt) | Test | Tests, nicht distribuiert | pom.xml | ungeklärt | Lizenz bestätigen (folgt Bukkit-GPL-Herkunft?) |
| fastutil | 8.5.12 | Apache-2.0 (unbestätigt) | Test | Tests, nicht distribuiert | pom.xml | unproblematisch | keine Maßnahme |
| WorldGuard | keine Version in pom | ungeklärt | optional/laufzeit (README: Soft-Dependency) | Kompatibilitäts-Hooks | README.md | ungeklärt | klären, ob gebündelt oder nur referenziert |

Hinweise:
- `maven-shade-plugin 3.5.1` ohne sichtbare Relocations: Compile-Dependencies werden ins verteilte Jar
  **eingebettet** — für den Lizenzreport und Copyleft-Pflichten relevant, aber kein eigenständiger
  MIT-Blocker für eigenen Code (keine Abhängigkeit wurde entfernt oder ersetzt).
- Paper-API ist `provided` (nicht eingebettet), das Plugin läuft aber als Derivat auf GPL-lizenzierter
  Plattform: Eine MIT-Lizenzierung eigenen Codes ist rechtlich etwas anderes als die GPL-Pflichten der
  **kombinierten Distribution** — vor einem MIT-Release rechtlich klären.
- Keine GPL/AGPL in Compile-Scope gefunden; alle Copyleft-Hinweise oben sind Verwendungs-, keine
  Beweis-Aussagen.

## 4. Contributor- und Rechteanalyse

Git-Metadaten enthalten vier Autor-Kombinationen, darunter `DaaanielTV`, `Daniel Rosemann`
und `Daniel`. Der Eigentümer hat ausdrücklich bestätigt, dass alle vier Einträge ihm
zuzuordnen sind und keine Rechte von Arbeitgebern, Auftraggebern oder sonstigen Dritten
an den Beiträgen bestehen. Die Commit-Historie allein belegt diese Angaben nicht.

## 5. Geänderte Dateien

- `LICENSE`: MIT-Text mit Copyright-Jahr 2026.
- `README.md`: Geltungsbeginn für neue Releases und Fortgeltung für frühere Releases klargestellt.
- `pom.xml`: Maven-Lizenzmetadaten auf MIT gesetzt.
- `docs/license-audit-2026-10-24.md`: Befund und offene Release-Prüfungen aktualisiert.
- Keine Änderungen an Anwendungslogik oder Abhängigkeiten.

## 6. Noch erforderliche Maßnahmen (Priorität)

1. **Vor dem ersten MIT-Release:** Lizenzen aller exakten Abhängigkeitsversionen und Transitives anhand
   der Quellartefakte/offiziellen Angaben verifizieren; das finale Shade-Artefakt prüfen und nötige
   Drittanbieter-Lizenztexte/-Hinweise beilegen.
2. **WorldGuard/MockBukkit:** weiterhin klären, ob sie gebündelt oder nur referenziert werden; die
   Lizenz der Testabhängigkeit MockBukkit verifizieren.
3. **Frühere Releases:** bei einer konkreten Veröffentlichung sicherstellen, dass deren GPLv3-Lizenz
   und zugehörige Pflichten für das jeweilige Artefakt erhalten bleiben.

## 7. Validierung

- `git status --short`, `git log`, `git remote -v`,
  `git tag -l` (keine Tags), `git submodule status` (leer): durchgeführt, lesend.
- `LICENSE` (vorher GPL-3.0-Volltext, anschließend MIT), `README.md` (Lizenzabschnitt), `CONTRIBUTING.md` (keine
  Rechteklausel), `THIRD_PARTY_NOTICES.md`, `pom.xml` (Dependencies + Shade ohne Relocations),
  `src/main/resources/plugin.yml`, Stichprobe Quellen-Header: durchgeführt, lesend.
`git diff --check` bestanden; die Maven-POM wurde mit dem XML-Parser validiert und die
MIT-Metadaten wurden geprüft. Maven ist in der Umgebung nicht installiert, daher wurde
`mvn validate` nicht ausgeführt. Keine Anwendungslogik geändert. Abhängigkeiten wurden
nicht heruntergeladen und der Shade-Artefakt-Lizenzreport ist noch offen.

## Release-Empfehlung

Die Repository-Metadaten sind auf MIT für neue Releases ab dem 24.10.2026 gesetzt. Vor
einem tatsächlichen MIT-Release bleiben die in §6 genannten Abhängigkeits- und
Artefaktprüfungen erforderlich. Dieses Audit stellt keine Rechtsberatung oder Garantie
der Lizenzkompatibilität des Gesamtartefakts dar.
