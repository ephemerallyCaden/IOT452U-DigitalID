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

For workers, it means a single digital identity that follows them across employers and regions. For organisations, it means fast verification without chasing paper certificates. For the central authority, it means audit trails and compliance reporting that's actually reliable.

---

## Architecture Overview

Layered architecture with ports and adapters. Four layers (presentation, application, domain, infrastructure) with port interfaces at the application-infrastructure boundary for testability. Full details in `doc-technical.md`.

### Storage: SQLite Database

SQLite made sense here — it's a single file (`digitalid.db`), needs no server configuration, and data persists between restarts. Assessors don't need to install anything to run the project. It's production-capable for this scale.

---

## Organisation Types

### 1. Central Authority: Food Service Certification Board

**Role:** Issues and manages digital worker IDs and certifications

Does everything — creates worker IDs, records certifications (internationally), manages status changes, tracks expirations, generates reports, maintains the audit trail. Essentially the only entity that can write to the system.


### 2. Financial Services: Payroll Financial

**Role:** Verifies worker IDs for payroll and tax compliance

Real-world context: employers must verify right-to-work before payroll (UK requires this before employment starts, US allows 3 business days after hire). This org just needs to confirm a worker is valid and authorised.

**Tools Available:** 2 tools (core: view + basic verification)

---

### 3. Fast Food Chain: Mega Slice Pizza

**Role:** Quick-service restaurant chain

High turnover, rapid hiring, simple requirements. They just need to know "is this person's ID valid?" before putting them on shift.

**Tools Available:** 2 tools (core: view + basic verification)

---

### 4. Fine Dining: Le Gourmet Restaurant Group

**Role:** Upscale restaurant requiring detailed verification

These places care about certification levels — they want to see ServSafe Manager, allergen training, full history. Hiring decisions depend on it.

**Tools Available:** 4 tools (core + certification history + attributes verification)

**Example Verification:**
```
Position: Head Chef
Worker ID: WK-US-2024-001

Result:
 Status: ACTIVE
 Food Handler (California): Valid until 2027-03-15
 ServSafe Manager: Valid until 2029-01-10
 Allergen Training: Completed
 Employment History: 3 positions, no violations

Decision: APPROVED
```

---

### 5. Delivery Service: QuickBite Delivery Network

**Role:** Food delivery platform with driver-specific requirements

Needs to check conditions beyond just "is the ID active" — background clearance, food handler permit, driver authorisation, active restrictions.

**Tools Available:** 3 tools (core + conditions)

---

### 6. Coffee Shop Chain: Daily Grind Coffee Co.

**Role:** Coffee shop with mostly part-time staff

Same as fast food — simple verification needs.

**Tools Available:** 2 tools (core: view + basic verification)

---

### 7. Street Vendor Association: Urban Eats

**Role:** Mobile food vendor credential management

Street vendors have specific requirements that other orgs don't deal with — mobile food unit permits, annual health inspections, commissary agreements, fire safety for propane equipment.

**Tools Available:** 3 tools (core + permits)

---

### Tool Set (Operations)

The system provides functions organised into categories. Each organisation type has access to a specific subset based on their role.

### CORE Tools (All Organisations)

**VIEW_WORKER_ID**
- View basic worker information, current status, primary operating region
- Available to: All organisation types

**VERIFY_BASIC**
- Simple yes/no: is this worker ID valid and active?
- Available to: All organisation types

### IDENTITY MANAGEMENT Tools (Central Authority Only)

**CREATE_WORKER_ID**
- Register new food service worker in system
- Assigns unique identifier, sets initial status and region
- Records work authorisation documentation (right-to-work check)

**UPDATE_WORKER_ID**
- Modify contact info (email, phone), mailing address, operating region
- Cannot modify immutable fields (name, date of birth)

**CHANGE_STATUS**
- Activate new or suspended worker
- Suspend worker (temporary, pending investigation etc.)
- Revoke worker (permanent removal from active service)

**DELETE_WORKER_ID**
- Permanently remove worker from system, cascades to certifications
- Audit log entries maintained for compliance
- Potentially requires reason attached (?)

### CERTIFICATION MANAGEMENT Tools (Central Authority Only)

**ADD_CERTIFICATION**
- Record new food safety certification
- Select type based on region, or manual override for a different certificate
- Validates against regional requirements automatically

**RENEW_CERTIFICATION**
- Update expiring certification with new dates
- Links to previous certification record, maintains history

**UPDATE_CERTIFICATION_STATUS**
- Mark as suspended, mark as expired (or automatic at expiration date), reactivate

**(optional) BULK_IMPORT_CERTIFICATIONS**
- Import multiple certifications from file — might not implement this, depends on time

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

**VIEW_AUDIT_LOG** — full audit trail, filterable by worker/org/date range

**GENERATE_COMPLIANCE_REPORT** — workers by status across regions, certification compliance %, export to PDF/CSV

**CHECK_EXPIRING_CERTS** — certifications expiring within 30/60/90 days, filterable by region or cert type

**GENERATE_REGIONAL_REPORT** — compliance stats, worker distribution, cert types by region

**VIEW_ORGANISATION_ACTIVITY** — verification requests by org, usage patterns

### SEARCH & QUERY Tools (Central Authority Only)

**SEARCH_WORKERS** — by name, email, or worker ID. Filter by region, status, cert type.

**SEARCH_BY_CERTIFICATION** — find all workers with a specific cert type, filter by validity

**SEARCH_BY_EXPIRATION** — workers with expiring certs, group by type or date range

### BATCH OPERATIONS Tools (Central Authority Only)

**BULK_STATUS_UPDATE** — update multiple workers at once (regulatory actions), requires justification

**BULK_CERTIFICATION_CHECK** — validity check across multiple workers, summary report

**EXPORT_WORKER_DATA** — backup/reporting export, CSV or JSON, filterable

### NOTIFICATION Tools (Central Authority Only)

**SEND_RENEWAL_REMINDER** — email/SMS for expiring certifications, customisable schedule (30, 14, 7 days). System can run these autonomously.

**SEND_STATUS_NOTIFICATION** — notify on status changes, include reasons. Can trigger automatically on any status change. Potentially include appeal info (?)


---

## Worker Credentials & Certifications

### What the System Tracks

The system supports food safety certifications across 12 regions. Here's what's relevant for each:

#### United States
- **Food Handler Certificate** — mandatory in 42+ states. Validity varies: California 3 years, Texas 2 years, Illinois 3 years. Generally 2-5 years.
- **ServSafe Manager (CFPM)** — at least one required per establishment. 5-year validity. ANSI-accredited exam.
- **Mobile Vendor Requirements** — varies wildly. NYC needs a licence + unit permit + food protection cert. California is an annual health permit. Texas needs a food handler card + CFPM on-site.

#### United Kingdom
- **Level 2 Food Safety and Hygiene** — required for all food handlers. 3-year recommended renewal. Issued by CIEH or Highfield, overseen by FSA.
- **Level 3 Food Safety (Supervising)** — for supervisors/managers. Also 3 years.

#### European Union
- **HACCP Training (EU Regulation 852/2004)** — required across all EU states. No official expiration but refresher recommended every 2-3 years.
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
| Germany | Hygiene Schulung | Same | Lifetime |
| Singapore | Level 1 | WSQ Course | 5 yrs |
| Japan | Sanitation Mgr | Same | Lifetime |
| Hong Kong | Basic Hygiene | Same | Lifetime |
| S. Korea | Hygiene Ed | Same | 1 year |

### Work Authorisation by Region

Separate from certifications, the system also tracks whether a worker has the legal right to work in their operating region. This is a verification that all consuming organisations can check.

#### United Kingdom
- **Right to Work check** — employer must verify before employment starts (no grace period)
- Documents: UK/Irish passport, Biometric Residence Permit (BRP), or Home Office share code
- Indefinite for UK/Irish citizens and those with settled status
- Time-limited for visa holders — system tracks expiry and flags when reverification needed

#### European Union
- **Work permit verification** — varies by country, but non-EU nationals need a valid residence/work permit
- EU/EEA citizens have free movement rights (indefinite)
- Third-country nationals need permits with expiry dates tracked by the system

#### United States
- **Employment eligibility verification** — must be completed within 3 business days of hire
- Documents: passport, permanent resident card, employment authorisation document, etc.
- Permanent residents have indefinite authorisation
- Temporary work visas require reverification before expiry

#### Singapore
- **Work pass verification** — Employment Pass, S Pass, or Work Permit required for non-citizens
- Citizens and permanent residents have indefinite right to work

#### General
- The system stores: documents presented, verification date, expiry date (null = indefinite), and the verifier
- Region-specific timing rules are enforced automatically
- All organisations can check work authorisation validity as part of the basic verification flow

---

## System Capabilities

### Workflow 1: Create US Worker with Certifications

```
1. User: Central Authority Portal
2. Select: Create Worker ID
3. Choose Region: United States
4. Enter Details:
   - Name: Maria Rodriguez
   - DOB: 1995-06-15
   - Email: maria@email.com
5. System shows required US certifications
6. Add Food Handler Certificate:
   - Type: Texas Food Handler
   - Cert Number: TFH-2024-1234
   - Issue: 2024-01-15
   - Expires: 2026-01-15
7. Add ServSafe Manager:
   - Cert Number: SM-2024-5678
   - Issue: 2024-01-20
   - Expires: 2029-01-20
8. System validates and creates worker
9. Result: Worker ID WK-US-2024-001 created
10. Audit log entry recorded
```

### Workflow 2: Fine Dining Verifies Chef

```
1. User: Le Gourmet Restaurant Portal
2. Select: Verify with Certification History
3. Enter Worker ID: WK-US-2024-001
4. System retrieves:
   - Worker status: ACTIVE
   - Food Handler: Valid until 2026-01-15
   - ServSafe Manager: Valid until 2029-01-20
5. Display certification history
6. Result: APPROVED for head chef position
```

### Workflow 3: Multi-Region Worker

```
Worker: Sofia Martinez
Worker ID: WK-INTL-2024-099

Certifications:
  United States:
    ServSafe Manager: Valid until 2028-12-01
  United Kingdom:
    Level 3 Food Safety: Valid until 2027-09-20
  Singapore:
    WSQ Food Safety: Valid until 2029-03-15

System checks:
  - Can work in US?         YES (has ServSafe)
  - Can work in UK?         YES (has Level 3)
  - Can work in Singapore?  YES (has WSQ)
  - Can work in France?     NO (no Formation HACCP)
```

---

## Technology Stack

- **Language:** Java 17
- **Database:** SQLite (via xerial sqlite-jdbc)
- **Build:** Maven
- **Testing:** JUnit 5 + Mockito

---

## Business Rules & Enforcement

### What the System Enforces

**Status rules:**
- Revoked workers can't be updated or reactivated — it's permanent
- Suspended workers can be reactivated by central authority
- All status changes get an audit log entry

**Certification rules:**
- Certifications expire based on their regional validity periods
- Workers should have appropriate certs for their operating region
- Right-to-work verification must meet regional timing requirements (UK: before start, US: within 3 days)
- Temporary work authorisation requires reverification before expiry

**Access control:**
- Organisations can only use tools listed in their profile
- Unauthorised tool access throws an exception
- Central authority has full management access; everyone else is read-only (verification)

**Data integrity:**
- Worker and certification identifiers are unique
- Foreign key relationships enforced in the database
- Timestamps track all changes

---

## Video demo plan

When demonstrating the system, I'll focus on these key tools:

**Essential (Must Show):**
1. CREATE_WORKER_ID
2. VERIFY_BASIC
3. ADD_CERTIFICATION 
4. VERIFY_WITH_CERT_HISTORY

**If Time Allows:**
5. CHECK_EXPIRING_CERTS
6. GENERATE_COMPLIANCE_REPORT
7. Multi-region worker creation

**For Questions:**
- Tool access control — how organisations get different tools
- Regional requirements — how certification types vary by region

---

## References
