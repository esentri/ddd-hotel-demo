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
| Zimmerkategorie | Kategorie eines Zimmers bzw. die in der Buchung gewuenschte: Einzelzimmer < Doppelzimmer < Suite (aufsteigend). | `Zimmerkategorie`, `Buchung.gewuenschteZimmerkategorie`, `Zimmer.zimmerkategorie` | candidate | | |
| Buchungsstatus | Stand einer Buchung: reserviert, eingecheckt, ausgecheckt, storniert. | `Buchungsstatus` | candidate | | |
| Reserviert | Status einer Buchung, die noch nicht eingecheckt (und nicht storniert) ist. | `Buchungsstatus.RESERVIERT` | candidate | angelegt | |
| Einchecken | Der Gast einer Buchung wird vor Ort aufgenommen und bekommt ein Zimmer zugewiesen; nur einmal pro Buchung, nur mit bekannter Adresse und nur ab dem Mindestalter moeglich. | `Buchung.checkeEin`, `CheckinService` | candidate | | |
| Stornieren | Eine Buchung wird zurueckgenommen; nur moeglich, solange sie reserviert ist (nicht eingecheckt oder ausgecheckt). | `Buchung.storniere` | candidate | | |
| Walk-In | Spontaner Check-in: ein Gast kommt ohne Buchung an den Tresen, die Buchung entsteht vor Ort (Anreise heute) und wird sofort eingecheckt, mit dem gemeinsam ausgewaehlten Zimmer. | `Buchung.walkIn`, `CheckeWalkInEin`, `CheckinService.checkeWalkInEin`, `WalkInService` | candidate | | |
| Zimmer | Zimmer des Hotels mit Zimmernummer, Zimmerkategorie und Merkmalen (Etage, Blickrichtung, Balkon, barrierefrei, Bettart, Raucherzimmer). | `Zimmer`, `Zimmer.ZimmerId`, `ZimmerRepository` | candidate | | |
| Zimmernummer | Nummer eines Zimmers, im Hotel eindeutig. | `Zimmernummer` | candidate | | |
| Zimmer anlegen | Ein neues Zimmer wird erfasst; es ist danach bezugsfertig. | `LegeZimmerAn`, `ZimmerFactory.legeAn` | candidate | | Ist ein neu angelegtes Zimmer sofort bezugsfertig? |
| Etage | Stockwerk, in dem das Zimmer liegt. | `Zimmer.etage` | candidate | | |
| Blickrichtung | Wohin das Zimmer ausgerichtet ist: Strasse, Innenhof, Park. | `Blickrichtung` | candidate | | |
| Bettart | Einzelbett, Doppelbett oder zwei Einzelbetten. | `Bettart` | candidate | | |
| Balkon / barrierefrei / Raucherzimmer | Ja/Nein-Merkmale eines Zimmers. | `Zimmer.balkon`, `Zimmer.barrierefrei`, `Zimmer.raucherzimmer` | candidate | | |
| Zimmerstatus | Zustand eines Zimmers: bezugsfertig, belegt, zu reinigen, ausser Betrieb. | `Zimmerstatus` | candidate | | Wer setzt "bezugsfertig" nach der Reinigung und "ausser Betrieb" (Housekeeping)? |
| Bezugsfertig | Zimmerstatus: das Zimmer kann einem Gast zugewiesen werden. | `Zimmerstatus.BEZUGSFERTIG` | candidate | | |
| Belegt | Zimmerstatus: das Zimmer ist einer eingecheckten Buchung zugewiesen. | `Zimmerstatus.BELEGT`, `Zimmer.belege`, `Zimmer.belegtDurch` | candidate | | |
| Zimmerzuweisung | Beim Check-in wird der Buchung ein bezugsfertiges Zimmer der gewuenschten oder einer hoeheren Zimmerkategorie zugewiesen; der Gast kann mitentscheiden. | `Buchung.zugewiesenesZimmer`, `Zimmer.belege` | candidate | | |
| Upgrade | Zuweisung eines Zimmers einer hoeheren als der gewuenschten Zimmerkategorie. | `VerfuegbaresZimmer.upgrade`, `Zimmerkategorie.istGleichOderHoeherAls` | candidate | | Ist ein Upgrade kostenfrei? |
| Verfuegbares Zimmer | Bezugsfertiges Zimmer der gewuenschten oder einer hoeheren Zimmerkategorie; Uebersicht fuer die Zimmerauswahl an der Rezeption. | `VerfuegbaresZimmer`, `VerfuegbareZimmerQueryHandler` | candidate | | |
| Auschecken | Der Gast reist ab: eine eingecheckte Buchung wird ausgecheckt, ihr Zimmer wird geraeumt und ist danach zu reinigen. Das zugewiesene Zimmer bleibt an der Buchung vermerkt. | `Buchung.checkeAus`, `CheckeAus`, `CheckoutService` | candidate | | |
| Ausgecheckt | Buchungsstatus nach dem Check-out. | `Buchungsstatus.AUSGECHECKT` | candidate | | |
| Zimmer raeumen | Beim Check-out wird das belegte Zimmer von der belegenden Buchung freigegeben; danach ist es zu reinigen. | `Zimmer.raeume` | candidate | | |
| Zu reinigen | Zimmerstatus nach dem Check-out; das Zimmer ist nicht verfuegbar. | `Zimmerstatus.ZU_REINIGEN` | candidate | | |
