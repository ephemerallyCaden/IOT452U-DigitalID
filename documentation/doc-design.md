# Food Service Digital ID Management System
## Design Documentation

## Contents

1. [System Context](#system-context)
2. [Architecture Overview](#architecture-overview)
3. [Organisation Types](#organisation-types)
4. [Worker Credentials & Certifications](#worker-credentials--certifications)
5. [System Capabilities](#system-capabilities)
6. [Technology Stack](#technology-stack)
7. [Business Rules & Enforcement](#business-rules--enforcement)
8. [Video Demo Plan](#video-demo-plan)
9. [References](#references)

For technical implementation details, architecture patterns, and database design, see `doc-technical.md`

---

## System Context

### Industry Background

The food truck and food service industry requires extensive documentation and certification for workers to operate legally. This is especially true internationally, where many countries use different certification and there is currently no system for overlap between them.

This industry choice allows for an extensive project scope, due to many factors. The first are the countless discrepancies in certification required between different regions. This is a great reason to having a centralising system. The legal importance of food safety providing genuine limits and safeguards for me to implement, and thirdly, the very accessible concept of an entity-based system of workers, is a simple idea that I can build on. I also found this to be a relatively unique endeavour, with no existing projects in the area of centralising food service documents, so I hope you find it just as interesting.

A quick overview of what different regions require:
- UK: Level 2/3 Food Safety (CIEH)
- EU: HACCP Training (EU Regulation 852/2004)
- Singapore: WSQ Food Safety Course
- Japan: Food Sanitation Manager Certificate
- US: Employment Eligibility Verification (+Food Handler Certificate in most states, 42+)

The food service industry also has extremely high turnover (75-150% annually), which means credential verification happens constantly. Most places currently manage this with disconnected spreadsheets or manual processes, which slows hiring and creates compliance gaps during inspections.

### What This System Does

This platform centralises all of that into one place:
- Worker identity and work authorisation
- Food safety certifications across 12 regions
- Health permits and business documentation
- Verification services for hiring and compliance

This is great for workers, as it means a single digital identity that follows them across employers and regions. For organisations looking to hire, pay employees, check certification renewals etc., it provides fast verification without chasing certificates. This is also beneficial to the central authorities data visibility, as it provides centralised access to comprehensive audit trails and compliance reporting.

---

## Architecture Overview

Layered architecture with ports and adapters. Four layers (presentation, application, domain, infrastructure) with port interfaces at the application-infrastructure boundary for both scalability and testability. Full details in `doc-technical.md`.

### Storage: JSON Files

Data is persisted across separate JSON files (`workers.json`, `certifications.json`, `work_authorisations.json`, `audit_log.json`, `sequence.json`) in a `data/` directory. I initially considered SQLite but decided it was overkill for the scale of this project. There's no need for SQL queries or relational joins when the dataset is small enough to load into memory. JSON keeps things simple: the files are human-readable, easy to inspect during development, and don't require any additional dependencies beyond Gson.

---

## Organisation Types

### 1. Central Authority Service

**Role:** Issues and manages digital worker IDs and certifications

Does everything, the only entity that can write to the system and therefore handling all forms of data in the system: worker IDs, certification, non-food-safety documents, audit and compliance logs.

**Tools Available:** All


### 2. Financial Service

**Role:** Verifies worker IDs for payroll and tax compliance

Employers must verify right-to-work before payroll (UK requires this before employment starts, US allows 3 business days after hire). This org just needs to confirm a worker is valid and authorised. This is done based on the region.

**Tools Available:** 3 tools (core: view + basic verification + work authorisation)

---

### 3. Fast Food Service

**Role:** Quick-service restaurant chain

High turnover in workers and therefore requiring only simple verification and hiring requirements. They just need to know the validity of a worker's ID
**Tools Available:** 3 tools (core: view + basic verification + work authorisation)

---

### 4. Fine Dining Service

**Role:** For fancier restaurants requiring more rigorous validation for workers they hire

Fine dining restaurants care about certification levels. They want to see ServSafe Manager, allergen training, and full history as the role is much more specialised and requires more skill.

**Tools Available:** 5 tools (core + certification history + attributes verification)

**Example Verification:**
```
Position: Head Chef
Worker ID: WK-UK-1

Result:
 Status: ACTIVE
 Level 2 Food Safety (CIEH): Valid until 2027-03-15
 Level 3 Food Safety - Supervising (Highfield): Valid until 2029-01-10
 Allergen Awareness Training: Completed

Decision: APPROVED
```

---

### 5. Delivery Service

**Role:** Food delivery platform with driver-specific requirements

Needs to check ID, background clearance, food handler permit, driver authorisation, and active restrictions.

**Tools Available:** 4 tools (core + conditions)

---

### 6. Coffee Shop Service

**Role:** Coffee shop with mostly part-time staff

Just simple verification required

**Tools Available:** 3 tools (core: view + basic verification + work authorisation)

---

### 7. Street Vendor Service

**Role:** Mobile food vendor credential management

Street vendors have specific requirements unique to their orgs: mobile food unit permits, annual health inspections, commissary agreements, and fire safety for propane equipment.

**Tools Available:** 4 tools (core + permits)

---

### Tool Set (Operations)

The system provides functions organised into categories. Each organisation type has access to a specific subset based on their role.

### CORE Tools (All Organisations)

**VIEW_WORKER**
- View basic worker information, current status, primary operating region
- Available to: All organisation types

**VERIFY_BASIC**
- Simple yes/no: is this worker ID valid and active?
- Available to: All organisation types

**VERIFY_WORK_AUTHORISATION**
- Check right-to-work documentation is valid and not expired
- Available to: All organisation types

### IDENTITY MANAGEMENT Tools (Central Authority Only)

**CREATE_WORKER**
- Register new food service worker in system
- Assigns unique identifier, sets initial status and region

**UPDATE_WORKER**
- Modify contact info (email), operating region
- Cannot modify immutable fields (name, date of birth)

**CHANGE_STATUS**
- Activate new or suspended worker
- Suspend worker (temporary, pending investigation etc.)
- Revoke worker (permanent removal from active service)

**DELETE_WORKER**
- Permanently remove worker from system
- Audit log entries maintained for compliance

### CERTIFICATION MANAGEMENT Tools (Central Authority Only)

**ADD_CERTIFICATION**
- Record new food safety certification for any worker
- Central Authority can attach certs from any region (workers may hold cross-regional certs)
- Validates certification dates and data integrity

**RENEW_CERTIFICATION**
- Update expiring certification with new dates
- Links to previous certification record

**UPDATE_CERTIFICATION_STATUS**
- Mark as suspended, mark as expired, or reactivate

### SPECIAL VERIFICATION Tools

**VERIFY_WITH_CERT_HISTORY**
- Complete certification history (current and expired), renewal history, previous employment if available
- Available to: Fine Dining Restaurants

**VERIFY_WITH_CONDITIONS**
- Check background clearance, food handler permit validity, driver authorisation, active restrictions
- Available to: Delivery Services

**VERIFY_WITH_PERMITS**
- Mobile food vendor licences, health permit validity, commissary agreements, fire safety, location permits, inspection results
- Available to: Street Vendor Associations, Food Truck Networks

**VERIFY_WITH_ATTRIBUTES**
- Check allergen training, specialised certs (HACCP, Sous Vide), health and safety training
- Available to: Fine Dining Restaurants

### REPORTING & ANALYTICS Tools (Central Authority Only)

**VIEW_AUDIT_LOG**: full audit trail, filterable by worker or organisation

**GENERATE_COMPLIANCE_REPORT**: total and active worker counts, certifications expiring within 30 days

**CHECK_EXPIRING_CERTS**: certifications expiring within a configurable number of days

**CHECK_REGIONAL_COMPLIANCE**: total and active worker count for a given region, with worker listing

**VIEW_ORGANISATION_ACTIVITY**: audit log entries for a given organisation

### SEARCH & QUERY Tools (Central Authority Only)

**SEARCH_WORKERS**: by name, region, or status. Supports combined filters.

### BATCH OPERATIONS Tools (Central Authority Only)

**BULK_STATUS_UPDATE**: update multiple workers at once (regulatory actions)

**EXPORT_WORKER_DATA**: backup/reporting export, CSV or JSON, filterable by region

### NOTIFICATION Tools (Central Authority Only)

**SEND_RENEWAL_REMINDER**: notification for expiring certifications, delivered via a NotificationPort (console adapter currently; swappable to email/SMS)

**SEND_STATUS_NOTIFICATION**: notify worker on status changes, delivered via NotificationPort


---

## Worker Credentials & Certifications

### What the System Tracks

The system supports food safety certifications across 12 regions. Here's what's relevant for each:

#### United States
- **Food Handler Certificate**: mandatory in 42+ states. Validity varies: California 3 years, Texas 2 years, Illinois 3 years. Generally 2-5 years.
- **ServSafe Manager (CFPM)**: at least one required per establishment. 5-year validity. ANSI-accredited exam.
- **Mobile Vendor Requirements**: varies wildly. NYC needs a licence + unit permit + food protection cert. California is an annual health permit. Texas needs a food handler card + CFPM on-site.

#### United Kingdom
- **Level 2 Food Safety and Hygiene**: required for all food handlers. 3-year recommended renewal. Issued by CIEH or Highfield, overseen by FSA.
- **Level 3 Food Safety (Supervising)**: for supervisors/managers. Also 3 years.

#### European Union
- **HACCP Training (EU Regulation 852/2004)**: required across all EU states. No official expiration but refresher recommended every 2-3 years.
- Country-specific variations: Germany has Gesundheitszeugnis (lifetime), France has Formation HACCP (3 years), Italy has Attestato HACCP (2-3 years), Spain has Certificado Manipulador (4 years).

#### Asia
- **Singapore:** WSQ Food Safety Course, 5-year validity, issued by SFA
- **Japan:** Food Sanitation Manager (食品衛生責任者), lifetime validity
- **Hong Kong:** Basic Food Hygiene Certificate, lifetime
- **South Korea:** Food Hygiene Education (식품위생교육), 1-year renewal
- **China:** Food Safety Training Certificate, 1-2 years (province-dependent)

### Regional Summary

| Region | Basic Cert | Manager Cert | Validity |
|--------|-----------|-------------|----------|
| US | Food Handler | ServSafe Manager | 2-5 yrs / 5 yrs |
| UK | Level 2 | Level 3 | 3 yrs / 3 yrs |
| Germany | Hygiene Schulung | -- | Lifetime |
| Singapore | Level 1 | WSQ Course | 5 yrs |
| Japan | Sanitation Mgr | -- | Lifetime |
| Hong Kong | Basic Hygiene | -- | Lifetime |
| S. Korea | Hygiene Ed | -- | 1 year |

### Work Authorisation by Region

Separate from certifications, the system also tracks whether a worker has the legal right to work in their operating region. This is a verification that all consuming organisations can check.

#### United Kingdom
- **Right to Work check**: employer must verify before employment starts (no grace period)
- Documents: UK/Irish passport, Biometric Residence Permit (BRP), or Home Office share code
- Indefinite for UK/Irish citizens and those with settled status
- Time-limited for visa holders: system tracks expiry and flags when reverification needed

#### European Union
- **Work permit verification**: varies by country, but non-EU nationals need a valid residence/work permit
- EU/EEA citizens have free movement rights (indefinite)
- Third-country nationals need permits with expiry dates tracked by the system

#### United States
- **Employment eligibility verification**: must be completed within 3 business days of hire
- Documents: passport, permanent resident card, employment authorisation document, etc.
- Permanent residents have indefinite authorisation
- Temporary work visas require reverification before expiry

#### Singapore
- **Work pass verification**: Employment Pass, S Pass, or Work Permit required for non-citizens
- Citizens and permanent residents have indefinite right to work

#### General
- The system stores: documents presented, verification date, expiry date (null = indefinite), and the verifier
- Region-specific timing rules are enforced automatically
- All organisations can check work authorisation validity as part of the basic verification flow

---

## System Capabilities

### Example Flow: Create UK Worker with Certifications

```
1. User: Central Authority Portal
2. Select: Create Worker ID
3. Choose Region: United Kingdom
4. Enter Details:
   - Name: Finn Mertens
   - DOB: 1995-06-15
   - Email: finn@adventure.time
5. System shows required UK certifications
6. Add Level 2 Food Safety and Hygiene:
   - Type: Level 2 Food Safety (CIEH)
   - Cert Number: CIEH-2024-1234
   - Issue: 2024-01-15
   - Expires: 2027-01-15
7. Add Level 3 Food Safety - Supervising:
   - Cert Number: HF-2024-5678
   - Issue: 2024-01-20
   - Expires: 2027-01-20
8. System validates and creates worker
9. Result: Worker ID WK-UK-1 created
10. Audit log entry recorded
```

### Example Flow: Fine Dining Verifies Chef

```
1. User: Fine Dining Service Portal
2. Select: Verify with Certification History
3. Enter Worker ID: WK-UK-1
4. System retrieves:
   - Worker status: ACTIVE
   - Level 2 Food Safety (CIEH): Valid until 2027-01-15
   - Level 3 Food Safety - Supervising (Highfield): Valid until 2027-01-20
5. Display certification history
6. Result: APPROVED for head chef position
```

### Example Flow: Region-Aware Verification

```
Worker: Joanne Binith
Worker ID: WK-UK-2

All certifications on record:
  - UK Level 2 Food Safety: Valid until 2028-01-10
  - UK Allergen Training: Valid until 2027-03-15
  - US Food Handler: Valid until 2027-09-01

Verification by UK Fine Dining (operating in UK):
  Certifications considered:
  - Level 2 Food Safety and Hygiene [VALID] (expires: 2028-01-10)
  - Allergen Awareness Training [VALID] (expires: 2027-03-15)
  (US Food Handler filtered out — not relevant to UK operations)

Result: VALID — 2 certification(s) on record
```

---

## Tech Stack

- **Language:** Java
- **Presentation:** JLine
- **Persistence:** JSON file (via Gson)
- **Build:** Maven
- **Testing:** JUnit 5, Github for CI
- **Version Control:** Github

---

## Business Rules & Enforcement

### What the System Enforces

**Status rules:**
- Revoked workers can't be updated or reactivated: it's permanent
- Suspended workers can be reactivated by central authority
- All status changes get an audit log entry

**Certification rules:**
- Certifications expire based on their regional validity periods
- Central Authority can attach any certification to any worker (workers may work across regions)
- Verification is region-aware: consuming organisations only see certs relevant to their operating region (e.g., a UK restaurant sees UK and EU-wide certs, not US certs)
- Right-to-work verification must meet regional timing requirements (UK: before start, US: within 3 days)
- Temporary work authorisation requires reverification before expiry

**Access control:**
- Organisations can only use tools listed in their profile
- Unauthorised tool access throws an exception
- Central authority has full management access; everyone else is read-only (verification)

**Data integrity:**
- Worker and certification identifiers are unique
- Deleting a worker cascades to remove orphaned certifications
- Timestamps track all changes
- IOException in persistence propagates visibly rather than silently returning empty data

---

## References
