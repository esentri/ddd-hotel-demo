# Ubiquitous Language

Domain terms of this project, one section per bounded context.
Status: `candidate` = proposed, not agreed yet (added by developers or the agent) -
`confirmed` = agreed with the team, only changed by agreement - `deprecated` = no
longer used, see "Avoid".

## Rezeption (`com.esentri.hotel.rezeption`)

Ablaeufe vor Ort am Tresen des Hotels. Die Reservierung im Vorfeld ist ein eigener Kontext (noch nicht modelliert).

| Term | Definition | Code | Status | Avoid | Open question |
|---|---|---|---|---|---|
| Buchung | Zentrales Konzept: steht immer in Verbindung zu einem Gast, hat einen Buchungszeitraum und eine gewuenschte Zimmerkategorie. Wird hoechstens einmal eingecheckt; eingecheckt nicht mehr stornierbar, sonst schon. | `Buchung`, `Buchung.BuchungId`, `BuchungRepository` | candidate | | |
| Gast | Person, fuer die gebucht ist; Teil der Buchung. Buchungen kommen ueber verschiedene Kanaele mit nicht immer vollstaendigen Gastdaten, die beim Check-in vervollstaendigt bzw. korrigiert werden koennen. Pflicht ist nur der Nachname; zum Check-in muessen Adresse und Geburtsdatum bekannt sein. | `Gast`, `Gast.GastId` | candidate | | |
| Adresse | Anschrift des Gastes aus Strasse, Postleitzahl, Ort und Land; Pflicht zum Check-in. | `Adresse`, `Gast.adresse` | candidate | | |
| Geburtsdatum | Geburtstag des Gastes; darf nicht in der Zukunft liegen und ist fuer die Alterspruefung beim Check-in noetig. | `Gast.geburtsdatum`, `Gast.alterAm` | candidate | | |
| Mindestalter | Ein Gast muss am Tag des Check-ins mindestens 16 Jahre alt sein. | `Buchung.MINDESTALTER_CHECKIN` | candidate | | |
| Buchungszeitraum | Zeitraum von der Anreise bis zur Abreise; die Abreise liegt nach der Anreise. | `Buchungszeitraum` | candidate | | |
| Anreise | Erster Tag des Buchungszeitraums. | `Buchungszeitraum.anreise` | candidate | | |
| Abreise | Letzter Tag des Buchungszeitraums. | `Buchungszeitraum.abreise` | candidate | | |
| Zimmerkategorie | Kategorie des gewuenschten Zimmers: Einzelzimmer, Doppelzimmer, Suite. | `Zimmerkategorie`, `Buchung.gewuenschteZimmerkategorie` | candidate | | |
| Buchungsstatus | Stand einer Buchung: reserviert, eingecheckt, storniert. | `Buchungsstatus` | candidate | | |
| Reserviert | Status einer Buchung, die noch nicht eingecheckt (und nicht storniert) ist. | `Buchungsstatus.RESERVIERT` | candidate | angelegt | |
| Einchecken | Der Gast einer Buchung wird vor Ort aufgenommen; nur einmal pro Buchung, nur mit bekannter Adresse und nur ab dem Mindestalter moeglich. | `Buchung.checkeEin` | candidate | | |
| Stornieren | Eine Buchung wird zurueckgenommen; nicht moeglich, wenn sie eingecheckt ist. | `Buchung.storniere` | candidate | | |
| Walk-In | Spontaner Check-in: ein Gast kommt ohne Buchung an den Tresen, die Buchung entsteht vor Ort (Anreise heute) und wird sofort eingecheckt. | `Buchung.walkIn`, `CheckeWalkInEin`, `WalkInService` | candidate | | |
