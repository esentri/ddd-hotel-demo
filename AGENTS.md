# Generelle Regeln für die Abbildung der Fachlichkeit
- Du bist ein Experte für taktisches DDD. 
- Du verwendest Java und Das Open Source Framework DomainLifecycles, insbesondere die dort bereitgestellten Marker Interfaces um DDD Building Blocks zu implementieren.

Die folgenden Regeln sind bei der Implementierung von DDD Building Blocks zu beachten:

## Regeln für Aggregates
- Eindeutige Identität: Jede Entity und somit auch jedes Aggregate Root muss über eine unveränderliche, typisierte ID verfügen. Der Objektvergleich (Equals/HashCode) erfolgt ausschließlich über diese ID.
- Konsistenz: Aggregates schützen Invarianten zwingend durch Guard-Clauses.
- Kapselung: Keine öffentlichen Setter. Keine direkte Herausgabe veränderbarer interner Listen (nur defensive Kopien/Unmodifiable).
- Validität: Keine "leeren" Objekte. Konstruktoren müssen die initiale Gültigkeit erzwingen.
- Reinheit: Keine Infrastruktur-Abhängigkeiten oder Repository-Aufrufe innerhalb des Aggregates.
- Keep Aggregates Small: Ein Aggregate ist keine fachliche Sammelmappe, sondern eine minimale Konsistenzgrenze. Es umfasst nur die Entities und Value Objects, die zwingend atomar und synchron konsistent sein müssen.
- Referenzierung: Andere Aggregates werden niemals als Objekt-Referenz eingebettet, sondern ausschließlich über ihre typisierte ID referenziert. Dies erzwingt die Entkopplung und verhindert riesige Objektgraphen.
- Tell, Don't Ask-Prinzip: Trage nach Möglichkeit keine Daten aus dem Aggregate heraus, um extern Entscheidungen zu treffen. Sende stattdessen einen Befehl (Command/Methode) an das Aggregate, damit es die Entscheidung intern basierend auf seinem eigenen Zustand trifft.

## Regeln für ValueObjects
- Unveränderlichkeit (Immutability): Value Objects sind nach der Erzeugung nicht mehr veränderbar. Jede Änderung erzeugt eine neue Instanz des Objekts.
- Gleichheit durch Wert: Zwei Value Objects sind identisch, wenn alle ihre Attribute übereinstimmen (implementiere equals und hashCode basierend auf allen Feldern).
- Selbst-Validierung: Ein Value Object darf niemals in einem ungültigen Zustand existieren. Prüfe alle fachlichen Regeln direkt im Konstruktor.
- Keine Primitive Obsession: Nutze keine einfachen Datentypen (String, Integer) für fachliche Konzepte. Erstelle stattdessen Value Objects, die ihre eigene Logik mitbringen.
- Nebenwirkungsfreie Methoden: Methoden in Value Objects dürfen niemals den Zustand verändern, sondern geben immer ein neues Value Object zurück.

## Regeln für Entities
- Global eindeutige Identität: Jede Entity besitzt eine eigene, global eindeutige Identität (typisierte ID). Der Vergleich von Entities erfolgt ausschließlich über diese ID.
- Kapselung durch das Root: Interne Entities sind nach außen hin "unsichtbar". Kein externer Dienst darf eine Referenz auf eine interne Entity halten oder diese direkt manipulieren. Jede Interaktion erfolgt über das Aggregate Root.
- Lebenszyklus-Abhängigkeit: Die Existenz einer internen Entity ist an das Aggregate Root gebunden. Wird das Root gelöscht, verschwinden auch seine Entities.
- Fachliche Methoden: Entities sind keine reinen Datenhüllen. Sie enthalten Logik, die ihren eigenen Zustand betrifft, werden aber vom Root orchestriert.
- Validierung bei Zustandsübergängen: Auch interne Entities müssen ihre eigenen Invarianten schützen. Eine Entity darf durch einen Methodenaufruf niemals in einen fachlich ungültigen Zustand geraten.

## Regeln für Repositories
- Schnittstellen-Prinzip: Repositories werden im Domänen-Layer als Interfaces definiert. Die technische Implementierung erfolgt strikt getrennt im Infrastruktur-Layer.
- Sammlungs-Metapher: Ein Repository verhält sich wie eine fachliche Sammlung (z. B. ein Set).
- Aggregate-Fokus: Repositories verwalten ausschließlich Aggregate Roots. Erstelle niemals Repositories für interne Entities oder Value Objects.
- Ganzheitlichkeit (Integrität): Ein Repository gibt immer das vollständige Aggregate zurück. Gib niemals Fragmente oder Teil-Daten (Projektionen) heraus.
- Keine Infrastruktur-Lecks: Die Schnittstelle darf keine technischen Details wie JPA-Criteria, SQL-Fragmente enthalten.
- Selektion vs. Präsentation:
  - Erlaubt: Fachliche Filter, um Aggregates für Operationen zu laden (z. B. findeÜberfälligeStornierungen()).
  - Vermeiden: Abfragen für Dashboards oder komplexe UI-Tabellen, welche Informationen aus mehreren Aggregates umfassen. Nutze hierfür Read Models.

## Regeln für ReadModels und QueryHandlers
- Immutability: Read Models müssen strikt unveränderlich sein. Es gibt keine öffentlichen Setter.
- Client-Zentrierung: Die Struktur eines Read Models richtet sich ausschließlich nach den Bedürfnissen des Konsumenten (z. B. UI oder API-Client). Sie darf flach sein, kann aber auch hierarchisch oder verschachtelt aufgebaut sein, wenn dies die Verarbeitung im Client vereinfacht.
- Grenzüberschreitung: Im Gegensatz zu Aggregates dürfen Read Models Daten aus verschiedenen Aggregates kombinieren (Denormalisierung), um Joins auf Client-Seite zu vermeiden.
- Bereitstellung via QueryHandler: Read Models werden niemals über Repositories bezogen, sondern über dedizierte QueryHandler.
- Wiederverwendung von Value Objects oder typisierte Ids: Es ist ausdrücklich erlaubt und erwünscht, Value Objects oder typisierte Ids aus der Domäne im Read Model wiederzuverwenden. Dies stellt sicher, dass fachliche Formatierungen und einfache Berechnungen (z. B. Geldwert.toString() oder Zeitraum.dauerInTagen()) konsistent bleiben.
- Keine neue Domänenlogik: Ein Read Model darf existierende Logik von Value Objects nutzen, aber keine eigene neue Geschäftslogik oder Validierungen einführen. Es bleibt ein reiner Informationsträger.
- Performance-Fokus: QueryHandler dürfen den "schweren" Weg über das Domänenmodell umgehen und optimierte Abfragen (z. B. natives SQL oder spezialisierte Views) nutzen.

## Regeln für Application Services
- Koordination statt Logik: Ein Application Service delegiert die Fachlogik an Aggregates oder Domain Services. Er enthält nur „Wenn-Dann“-Abläufe für den Prozess (z. B. „Lade Buchung, führe Check-In aus, speichere Buchung“).
- Transaktionsgrenze: Hier beginnt und endet die Datenbank-Transaktion. Der Application Service stellt sicher, dass eine Operation atomar ist.
- Infrastruktur-Brücke: Er koordiniert technische Belange: Versand von Bestätigungs-E-Mails, Aufruf von externen APIs oder das Veröffentlichen von Events.
- Eingabe/Ausgabe: Er nimmt Commands oder einfache Datentypen (DTOs) entgegen und gibt idealerweise ReadModels oder einfache Bestätigungen zurück. Er schirmt die Domäne vor der Außenwelt ab.
- Sicherheit & Zugriff: Authentifizierung und Autorisierung („Darf dieser Nutzer diesen Command ausführen?“) werden typischerweise hier oder in vorgeschalteten Interceptoren geprüft.

## Regeln für Domain Services
- Zustandslosigkeit: Doman Services sind zustandslos. Sie halten keine Daten, sondern führen Berechnungen oder Operationen auf übergebenen Aggregates aus.
- Fachliche Sprache: Die Methodennamen spiegeln die Ubiquitous Language wider (z. B. ZimmerZuweisungsService.findeBestesZimmer()).
- Keine Infrastruktur: Ein Domain Service darf keine technischen Details (E-Mail-Versand, Datenbank-Transaktionen) kennen. Er bleibt im Domain Layer.
- Logik-Fokus: Nutze Domain Services nur, wenn die Logik wirklich nicht in ein Aggregate passt. Das Ziel bleibt ein Rich Domain Model, kein prozeduraler Code.

## Regeln für Domain Commands
- Strikte Logik-Trennung: Ein Domain Command darf keinerlei Businesslogik enthalten. Seine Verantwortung beschränkt sich ausschließlich auf die formale (syntaktische) Validierung.
- Erlaubt (Validierung): Prüfung auf null, leere Strings, korrekte Formate (Regex), Wertebereiche (z. B. „Alter muss eine positive Zahl sein“).
- Verboten (Businesslogik): Fachliche Entscheidungen, Berechnungen oder Prüfungen gegen den Systemzustand (z. B. „Ist das Zimmer frei?“ oder „Darf dieser Gast einchecken?“). Diese Logik gehört zwingend in Aggregates oder Domain Services.
- Fachliche Absicht (Intent): Domain Commands sind nach der fachlichen Handlung benannt (WeiseZimmerZu), nicht nach technischen Datenänderungen (UpdateZimmerRequest).
- Unveränderlichkeit (Immutability): Domain Commands müssen strikt unveränderlich sein.
- Eindeutigkeit: Ein Domain Command repräsentiert genau eine fachliche Absicht und führt zu genau einer Transaktion.
- Teil der Domäne: Domain Commands sind Teil Domäne, sie können außerhalb bspw. aus einem Application Service erstellt werden, aber da sie Teil der Domäne sind, problemlos in den Core hereingereicht werden.

## Regeln für Domain Events
- Benennung: Domain Events dokumentieren fachlich relevante Fakten der Vergangenheit und werden konsequent in der Vergangenheitsform benannt (z. B. BuchungEingecheckt).
- Inhalt: Ein Domain Event sollte nur die Identität (IDs) und die minimal notwendigen Daten enthalten, um den Empfängern die Arbeit zu ermöglichen.
- Unveränderlichkeit (Immutability): Domain Events müssen strikt unveränderlich sein.

# Regeln für die Implementierung von taktischem DDD mit Java und Domainlifecycles

- Du bist Senior Java Engineer für Domain-Driven Design mit DomainLifecycles (DLC, https://domainlifecycles.io).
- Verwende im Java Code nie deutsche Umlaute, stattdessen: ä→ae, ü→ue, ö→oe
- Verwende für alle Application Services das Interface io.domainlifecycles.domain.types.ApplicationService. Wenn du eine Methode zur Verarbeitung eines Befehls schreibst, muss der Eingabeparameter ein Java Record sein, der io.domainlifecycles.domain.types.DomainCommand implementiert.
- Nutze für AggregateRoots die Markierung per Interface io.domainlifecycles.domain.types.AggregateRoot<ID> .
- Nutze für Entities die Markierung per Interface io.domainlifecycles.domain.types.Entity<ID> .
- Nutze für Value Objects die Markierung per Interface io.domainlifecycles.domain.types.ValueObject. Verwende Java Records für die erforderliche Immutability.
- Nutze für typisierte Identities die Markierung per Interface io.domainlifecycles.domain.types.Identity<VALUETYPE>. Verwende Java Records für die erforderliche Immutability.
- Domain Commands müssen io.domainlifecycles.domain.types.DomainCommand implementieren. Verwende Java Records für die erforderliche Immutability.
- Markiere Application Services immer mit dem Interface io.domainlifecycles.domain.types.ApplicationService.
- Markiere Domain Services immer mit dem Interface io.domainlifecycles.domain.types.DomainService.
- Domain Events müssen io.domainlifecycles.domain.types.DomainEvent implementieren. Verwende Java Records für die erforderliche Immutability.
- Markiere Methoden, die DomainEvent veröffentlichen mit der Annotation io.domainlifecycles.domain.types.Publishes(domainEventTypes={EVENT_TYPE_A, EVENT_TYPE_B}). Dies kann aus AggregateRoots, Entities, oder DomainServices heraus erfolgen.
- Veröffentliche Domain Events ausschließlich über die statische API DomainEvents.publish(EVENT) aus io.domainlifecycles.events.api.DomainEvents. Der Aufruf erfolgt innerhalb der Aggregat-Methode oder innerhalb einer DomainService-Methode nach erfolgreicher Zustandsänderung.
- Markiere Methoden, die Events empfangen mit der Annotation io.domainlifecycles.domain.types.DomainEventListener. Dies kann in AggregateRoots, DomainServices oder ApplicationServices erfolgen.
- ReadModels  müssen io.domainlifecycles.domain.types.ReadModel implementieren. Verwende Java Records für die erforderliche Immutability.
- Verwende für alle Query Handler das Interface io.domainlifecycles.domain.types.QueryHandler<READMODEL>.
- Prüfe Invarianten immer in den Konstruktoren und wirf entsprechende Standard Java-Exceptions. 
- Repositories müssen io.domainlifecycles.domain.types.Repository<ID, AGGREGATE_ROOT> implementieren

## Technische Regeln (Lombok & Konstruktoren)
- Vollständige Konstruktoren für Entities und Roots: Jede Entity und jedes Aggregate Root muss über genau einen public Konstruktor verfügen, der ausnahmslos alle Felder als Parameter entgegennimmt. Dieser Konstruktor muss zwingend mit @Builder (Lombok) annotiert sein, um eine saubere und lesbare Instanziierung zu ermöglichen.
- Builder für Value Objects: Jedes als record implementierte Value Object muss ebenfalls mit der @Builder-Annotation von Lombok versehen werden.
- Kombination mit Validierung: Die geforderte Selbst-Validierung und das Schützen von Invarianten (Guard Clauses) müssen innerhalb dieses All-Args-Konstruktors (bzw. beim Record im kompakten/kanonischen Konstruktor) greifen, damit der @Builder niemals ein ungültiges Objekt erzeugen kann.
