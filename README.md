# 🧠 Mentora - Backend REST API

**Una piattaforma intelligente per il matching tra clienti e psicologi**

---

## 📋 Panoramica del Progetto

**Mentora** è un sistema backend completo per una piattaforma di **benessere psicologico** che connette in modo intelligente i clienti con i professionisti più adatti alle loro esigenze. 

Il sistema utilizza un **questionario psicologico avanzato** per analizzare la situazione del cliente e suggerire automaticamente i terapeuti più compatibili, gestendo l'intero percorso dalla ricerca iniziale fino alla comunicazione continua.

### 🎯 Obiettivi del Sistema
- **Matching Intelligente**: Algoritmo di raccomandazione basato su questionario psicologico
- **Gestione Completa**: Dalla ricerca del terapeuta alla comunicazione post-terapia
- **Sicurezza**: Autenticazione JWT e protezione dati sensibili
- **Scalabilità**: Architettura modulare pronta per crescere

---

## 🏗️ Architettura Tecnica

### **Stack Tecnologico**
- **Framework**: Spring Boot 3.2.0 + Java 17
- **Database**: H2 (sviluppo) / MySQL (produzione)
- **Sicurezza**: Spring Security + JWT
- **ORM**: Hibernate/JPA con query custom
- **Validation**: Bean Validation (Jakarta)
- **Documentation**: OpenAPI/Swagger

### **Pattern Architetturali**
- **MVC**: Model-View-Controller per separazione responsabilità
- **DTO Pattern**: Data Transfer Objects per API pulite
- **Repository Pattern**: Accesso dati astratto e testabile
- **Service Layer**: Business logic centralizzata e riutilizzabile

---

## 👥 Tipi di Utente

### **🧑‍💼 CLIENT (Cliente)**
**Funzionalità principali:**
- Registrazione e gestione profilo personale
- Compilazione questionario psicologico avanzato
- Ricerca psicologi con filtri intelligenti
- Invio richieste di terapia personalizzate
- Gestione appuntamenti e comunicazione

### **👨‍⚕️ PSYCHOLOGIST (Psicologo)**
**Funzionalità principali:**
- Registrazione professionale con specializzazioni
- Gestione profilo e aree operative
- Ricezione e valutazione richieste clienti
- Gestione calendario appuntamenti
- Comunicazione sicura con i clienti

---

## 🧩 Moduli Principali

### 🔐 **Sistema di Autenticazione**
```java
POST /api/auth/login          // Login con JWT token
POST /api/auth/register/client    // Registrazione cliente
POST /api/auth/register/psychologist // Registrazione psicologo
GET  /api/auth/me            // Profilo utente corrente
```

**Caratteristiche:**
- **JWT Stateless**: Token sicuri con scadenza configurabile
- **Role-based Access**: Permessi differenziati per ruolo
- **Password Hashing**: BCrypt per sicurezza password
- **Validazione Input**: Controlli stringenti su email e password

### 📝 **Questionario Psicologico Intelligente**
```java
POST /api/questionnaire      // Compilazione questionario
GET  /api/questionnaire      // Recupero questionario compilato
```

**Sistema di Valutazione Automatica:**
- **Calcolo Priorità**: Algoritmo che determina l'urgenza (NORMAL/MODERATE/HIGH)
- **Specializzazione Richiesta**: Mapping automatico età → specializzazione
- **Fattori di Rischio**: Detection automatica di situazioni critiche
- **Raccomandazioni**: Lista psicologi compatibili con motivazioni

**Logica di Prioritizzazione:**
```java
HIGH Priority:    Dipendenze, traumi recenti, violenza frequente
MODERATE Priority: Sintomi persistenti >1 mese, impatto grave
NORMAL Priority:   Altri casi
```

### 🔍 **Sistema di Matching Avanzato**
```java
GET  /api/psychologists/search    // Ricerca con filtri
GET  /api/psychologists/{id}      // Profilo dettagliato
POST /api/psychologists/{id}/request // Richiesta terapia
```

**Algoritmo di Raccomandazione:**
1. **Analisi Questionario** → Determina specializzazione richiesta
2. **Filtri Geografici** → Psicologi nella città del cliente
3. **Compatibilità Modalità** → Online/Presenza/Misto
4. **Esclusioni** → Rimuove psicologi già collegati
5. **Ranking** → Ordina per rilevanza e disponibilità

### 🤝 **Gestione Relazioni Terapeutiche**
```java
GET  /api/psychologists/requests     // Richieste ricevute (Psicologo)
PUT  /api/psychologists/requests/{id} // Accettazione/Rifiuto
```

**Workflow Completo:**
1. Cliente invia richiesta con messaggio personalizzato
2. Psicologo riceve notifica con priorità e dettagli
3. Valutazione e risposta (ACCEPTED/REJECTED) con note
4. Attivazione comunicazione se accettata

### 📅 **Sistema Appuntamenti**
```java
POST /api/appointments           // Creazione appuntamento
GET  /api/appointments           // Lista appuntamenti utente
PUT  /api/appointments/{id}/status // Gestione stati
```

**Gestione Stati:**
- `REQUESTED` → `CONFIRMED` → `COMPLETED`
- `REQUESTED` → `CANCELLED` / `REJECTED`

**Supporto Modalità:**
- **Online**: Con URL meeting automatico
- **In Presenza**: Con location specifica
- **Misto**: Flessibilità di scelta

### 💬 **Sistema Messaggistica**
```java
POST /api/messages                    // Invio messaggio
GET  /api/messages/conversation/{id}  // Cronologia chat
PUT  /api/messages/{id}/read         // Tracking lettura
```

**Caratteristiche:**
- **Comunicazione Sicura**: Solo tra utenti con relazione attiva
- **Tracking Lettura**: Conferme di lettura automatiche
- **Paginazione**: Gestione efficiiente cronologie lunghe
- **Soft Delete**: Messaggi non eliminati fisicamente

---

## 📊 Modello Dati

### **Entità Principali**

**User Hierarchy:**
```
User (abstract)
├── Client
│   ├── age, location, preferredSessionMode
│   └── questionnaireResponse (1:1)
└── Psychologist
    ├── licenseNumber, biography, specializations
    └── operatingLocations (N:N)
```

**Relazioni Chiave:**
- `ClientPsychologistRelation`: Gestisce richieste e stato connessione
- `QuestionnaireResponse`: Analisi psicologica con priorità calcolata
- `Appointment`: Appuntamenti con supporto multi-modalità
- `Message`: Sistema chat con tracking lettura

### **Enums Strategici**
```java
UserRole: CLIENT, PSYCHOLOGIST
PsychologistSpecialization: CHILD_PSYCHOLOGY, ADOLESCENT_PSYCHOLOGY, 
                          ADULT_PSYCHOLOGY, GERIATRIC_PSYCHOLOGY
Priority: NORMAL, MODERATE, HIGH
SessionMode: IN_PERSON, ONLINE, MIXED, INDIFFERENT
```

---

## 🚀 Funzionalità Avanzate

### **Algoritmo di Specializzazione Automatica**
```java
// Mapping automatico età → specializzazione
Age 0-11   → CHILD_PSYCHOLOGY
Age 12-16  → ADOLESCENT_PSYCHOLOGY  
Age 17-64  → ADULT_PSYCHOLOGY
Age 65+    → GERIATRIC_PSYCHOLOGY
```

### **Sistema di Priorità Intelligente**
Il questionario valuta automaticamente:
- **Durata sintomi**: Persistenza problematiche
- **Intensità impatto**: Livello di compromissione vita quotidiana
- **Fattori di rischio**: Comportamenti autolesivi, violenza, dipendenze
- **Supporto sociale**: Presenza rete di sostegno
- **Sintomi depressivi**: Indicatori clinici rilevanti

### **Ricerca Multi-Criterio**
```java
// Filtri combinabili
?city=Milano&specialization=ADULT_PSYCHOLOGY&sessionMode=ONLINE
```

### **Gestione Transazionale**
- **@Transactional**: Operazioni atomiche su relazioni complesse
- **Upsert Pattern**: Update intelligente questionari esistenti
- **Soft Delete**: Preservazione dati per audit
- **Optimistic Locking**: Gestione concorrenza

---

## 🔒 Sicurezza e Privacy

### **Autenticazione JWT**
- **Token Expiration**: 24 ore configurabile
- **Stateless**: Nessun session server-side
- **Role-Based**: Controllo accessi granulare

### **Protezione Dati Sensibili**
- **Questionario Privato**: Visibile solo al terapeuta scelto
- **Comunicazioni Protette**: Chat solo tra utenti connessi
- **Validazione Input**: Sanitizzazione automatica
- **CORS Configurato**: Accesso controllato da frontend

### **Compliance**
- **GDPR Ready**: Soft delete e data minimization
- **Professional Standards**: Rispetto privacy terapeutica
- **Audit Trail**: Tracciamento operazioni sensibili

---

## 📈 Scalabilità e Performance

### **Database Design**
- **Indicizzazione Strategica**: Su campi di ricerca frequenti
- **Query Ottimizzate**: JPQL con join efficienti
- **Lazy Loading**: Caricamento dati on-demand
- **Paginazione**: Gestione grandi dataset

### **Architettura Modulare**
```java
// Separazione responsabilità chiara
Controller → Service → Repository → Entity
```

### **Configurazione Flessibile**
- **Profiles**: dev/test/prod con configurazioni specifiche
- **Properties Externalized**: Database, JWT, mail configurabili
- **Docker Ready**: Containerizzazione semplice

---

## 🧪 Testing e Quality Assurance

### **Endpoints di Debug**
```java
GET /api/test/hello      // Health check
GET /api/test/status     // System status + DB counters
GET /api/test/users      // User list (debug)
```

### **Validation Completa**
- **Bean Validation**: Controlli automatici input
- **Custom Validators**: Business rules specifiche
- **Error Handling**: Messaggi user-friendly
- **HTTP Status Codes**: Semantica REST corretta

---

## 📋 Esempi di Utilizzo

### **Workflow Tipico Cliente**
```bash
1. POST /api/auth/register/client     # Registrazione
2. POST /api/auth/login              # Login → JWT token
3. POST /api/questionnaire           # Compila questionario
4. GET  /api/psychologists/search    # Vede psicologi raccomandati
5. POST /api/psychologists/1/request # Invia richiesta
6. POST /api/appointments           # Prenota appuntamento
7. POST /api/messages               # Comunica con terapeuta
```

### **Workflow Tipico Psicologo**
```bash
1. POST /api/auth/register/psychologist # Registrazione professionale
2. POST /api/auth/login                # Login
3. GET  /api/psychologists/requests    # Vede richieste clienti
4. PUT  /api/psychologists/requests/1  # Accetta richiesta
5. GET  /api/appointments             # Gestisce calendario
6. POST /api/messages                 # Comunica con clienti
```

---

## 🎯 Valore Aggiunto

### **Per i Clienti**
- **Matching Intelligente**: Non più ricerca casuale, ma raccomandazioni basate su analisi scientifica
- **Trasparenza**: Informazioni complete su specializzazioni e approcci terapeutici
- **Flessibilità**: Supporto modalità online, presenza e miste
- **Privacy**: Comunicazione sicura e dati protetti

### **Per i Professionisti**
- **Clienti Qualificati**: Richieste pre-filtrate con informazioni sulla priorità
- **Gestione Efficiente**: Calendario e comunicazioni integrate
- **Specializzazione Target**: Matching basato su competenze specifiche
- **Business Intelligence**: Insights su richieste e trend

### **Per il Sistema Sanitario**
- **Accesso Facilitato**: Riduzione barriere all'accesso alle cure psicologiche
- **Ottimizzazione Risorse**: Matching efficiente riduce tempi morti
- **Prevenzione**: Identificazione precoce situazioni critiche
- **Quality of Care**: Abbinamenti più accurati migliorano outcomes

---

## 🚀 Deployment e Configurazione

### **Requirements**
- Java 17+
- Maven 3.6+
- MySQL 8.0+ (produzione)

### **Quick Start**
```bash
git clone <repository>
cd mentora-backend
mvn spring-boot:run
```

### **Database Setup**
```sql
CREATE DATABASE mentora_prod;
CREATE USER 'mentora_user'@'localhost' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON mentora_prod.* TO 'mentora_user'@'localhost';
```

### **Environment Variables**
```properties
DATABASE_URL=jdbc:mysql://localhost:3306/mentora_prod
DATABASE_USERNAME=mentora_user  
DATABASE_PASSWORD=secure_password
JWT_SECRET=your_secure_jwt_secret_key_here
```

---

## 📚 API Documentation

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **H2 Console** (dev): `http://localhost:8080/h2-console`
- **Health Check**: `http://localhost:8080/api/test/status`

### **Credenziali Test**
```
Psicologi:
- dr.rossi@mentora.com / password123
- dr.bianchi@mentora.com / password123

Clienti:  
- anna.neri@email.com / password123
- luca.blu@email.com / password123
```

---

## 🎖️ Conclusioni

**Mentora Backend** rappresenta una soluzione completa e moderna per il settore del benessere psicologico, combinando:

✅ **Tecnologie Avanzate**: Spring Boot, JWT, algoritmi di matching  
✅ **User Experience**: API intuitive e workflow ottimizzati  
✅ **Sicurezza**: Protezione dati e compliance privacy  
✅ **Scalabilità**: Architettura modulare e performance ottimizzate  
✅ **Impatto Sociale**: Facilitazione accesso cure psicologiche  

Il sistema non è solo un'applicazione tecnica, ma uno strumento concreto per **migliorare l'accesso alle cure psicologiche** attraverso tecnologia intelligente e design user-centrico.

---

*Sviluppato con ❤️ per il benessere psicologico digitale*
