# Datenbank

## Beschreibung
Die Datenbank besteht aus zwei Tabellen: "Accounts" und "Tasks".

### Tabellen
Die "***Accounts***"-Tabelle enthält Informationen über Benutzerkonten und hat die folgenden Spalten:
- **account_id (bigint):** Eindeutige Kennung für jedes Benutzerkonto.
- **firstname (character varying):** Der Vorname des Benutzers.
- **lastname (character varying):** Der Nachname des Benutzers.
- **password (character varying):** Das Passwort für das Benutzerkonto.

Die "***Tasks***"-Tabelle enthält Informationen über Aufgaben, die Benutzern zugewiesen sind. Die Spalten dieser Tabelle sind:
- **task_id (bigint):** Eindeutige Kennung für jede Aufgabe.
- **task_title (character varying):** Der Titel oder die Bezeichnung der Aufgabe.
- **task_description (character varying):** Eine *optionale* Beschreibung der Aufgabe.
- **assignee_id (bigint):** Die Zuordnung zu einem Benutzerkonto über die "account_id" in der "Accounts"-Tabelle.
- **task_deadline (timestamp with time zone):** Das Datum und die Uhrzeit, bis zu denen die Aufgabe abgeschlossen sein soll.
- **task_created_time (timestamp with time zone):** Das Datum und die Uhrzeit, zu der die Aufgabe erstellt wurde.
- **task_priority (priorities):** Die Priorität der Aufgabe
- **task_status (status):** Der Status der Aufgabe

### Aufzählungstypen
Es gibt auch zwei benutzerdefinierte Aufzählungstypen ("Enum") in der Datenbank:
- **priorities:** Definiert die Prioritäten von Aufgaben mit den Werten '*High*', '*Medium*' oder '*Low*'.
- **status:** Definiert die Statusmöglichkeiten von Aufgaben mit den Werten '*Not Started*', '*In Progress*' oder '*Done*'.

### Constraints
Die Datenbank enthält auch Fremdschlüssel-Constraints, die sicherstellen, dass die "Tasks"-Tabelle auf die "Accounts"-Tabelle verweist. Der Fremdschlüssel "Tasks_assignee_id_fkey" verbindet die "assignee_id" in der "Tasks"-Tabelle mit der "account_id" in der "Accounts"-Tabelle.

## Zugriff
- **Database**: taskmanager
- **Hostname**: psql.f4.htw-berlin.de
- **Port**: 5432
- **Engine**: PostgreSQL

### Login-Daten
- **Admin**:
    - *Username*: taskmanager_admin
    - *Password*: iLUqbipQL

- **Manager**:
    - *Username*: taskmanager_manager
    - *Password*: cC314bj4d

- **Workerbee**:
    - *Username*: taskmanager_workerbee
    - *Password*: s2lIoSvMZ

## SQL Code:
```sql
-- Table: Accounts

DROP TABLE IF EXISTS "Accounts" CASCADE;

CREATE TABLE IF NOT EXISTS "Accounts"
(
    account_id bigint NOT NULL,
    firstname character varying NOT NULL,
    lastname character varying NOT NULL,
    password character varying NOT NULL,
    CONSTRAINT "Accounts_pkey" PRIMARY KEY (account_id)
);

-- Type: priorities

DROP TYPE IF EXISTS priorities CASCADE;

CREATE TYPE priorities AS ENUM
    ('High', 'Medium', 'Low');

-- Type: status

DROP TYPE IF EXISTS status CASCADE;

CREATE TYPE status AS ENUM
    ('Not Started', 'In Progress', 'Done');
   
-- Table: Tasks

DROP TABLE IF EXISTS "Tasks" CASCADE;

CREATE TABLE IF NOT EXISTS "Tasks"
(
    task_id bigint NOT NULL,
    task_title character varying NOT NULL,
    task_description character varying,
    assignee_id bigint NOT NULL,
    task_deadline timestamp with time zone,
    task_created_time timestamp with time zone NOT NULL,
    task_priority priorities NOT NULL,
    task_status status NOT NULL,
    CONSTRAINT "Tasks_pkey" PRIMARY KEY (task_id),
    CONSTRAINT "Tasks_assignee_id_fkey" FOREIGN KEY (assignee_id)
        REFERENCES "Accounts" (account_id)
);

```
