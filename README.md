# DevOps-Enabled Telemedicine Queue Management System (TQMS)

[![Java 17](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Build-Maven%203.9-blue.svg)](https://maven.apache.org/)
[![Packaging](https://img.shields.io/badge/Packaging-WAR-blueviolet.svg)](https://tomcat.apache.org/)
[![License](https://img.shields.io/badge/License-Academic-lightgrey.svg)]()

> **Course:** DevOps Lab &bull; **Class:** B.E. Semester VII (Computer Engineering, Division A)  
> **Student:** Sheshank Singh (Roll No: `23102A0017` | GitHub: `@Sheshank-singh`)  
> **Subject In-charge:** Prof. Snehal Andhare  

---

## 1. Project Overview
The **Telemedicine Queue Management System (TQMS)** is a lightweight, responsive web application engineered to solve real-time queue congestion and visibility challenges in remote telemedicine clinics. 

While the functional application manages patient token generation, role-based queue transitions, and status lookup, its primary curriculum objective is to demonstrate a **comprehensive DevOps lifecycle**:
1. Version control via **Git & GitHub** (branching, PRs, release tags).
2. Continuous Integration via **Jenkins** (commit hooks, Maven build, artifact archiving).
3. Pipeline-as-Code via **Jenkinsfile** and deployment to **Tomcat 10.1**.
4. Automated browser regression testing with **Selenium WebDriver**.
5. Containerization and lifecycle management with **Docker & Docker Hub**.
6. Automated node provisioning, idempotency, and rollback with **Ansible/Puppet**.

---

## 2. Minimum Viable Product (MVP) Scope
- **FR-01: Patient Registration & Token Generation:** Front desk registers patients; automatic token assignment (`TQ-001`, `TQ-002`, ...).
- **FR-02: Live Queue Visibility:** Dynamic queue display ordered by priority (Urgent first) and arrival time.
- **FR-03: Patient Record Update:** Update patient phone, symptoms, or department.
- **FR-04: Search Records:** Real-time search across tokens, names, and statuses.
- **FR-05: Role-Based Workflow:** Strictly enforced status state machine:  
  `WAITING` &rarr; `CALLED` &rarr; `IN_CONSULTATION` &rarr; `COMPLETED` (or `CANCELLED` / `NO_SHOW`).
- **FR-06: Role-Based Access:** Distinct privileges for Receptionist, Doctor, Admin, and Public Patients.
- **FR-07: Public Patient Lookup:** Public endpoint where patients track queue position without login.
- **FR-08: Admin Summary Dashboard:** Real-time counters for all queue states.
- **FR-09: Health Check Monitoring:** Spring Boot Actuator endpoint (`/actuator/health`) for pipeline validation.

---

## 3. Technology Stack

| Layer | Component | Specification |
| :--- | :--- | :--- |
| **Language & SDK** | Java | OpenJDK 17 LTS / 21 LTS |
| **Backend Framework** | Spring Boot | 3.2.5 (Web, Data JPA, Validation, Actuator) |
| **Build & Dependency Tool** | Apache Maven | 3.9.x |
| **Web UI & Styling** | Thymeleaf, HTML5, CSS3, JS | Modern Responsive Vanilla CSS & Inter typography |
| **Database** | H2 (Dev/CI) & MySQL 8 (Prod) | H2 in-memory for zero-dependency builds |
| **Packaging & App Server**| WAR archive | Standalone or Apache Tomcat 10.1 (`webapps/tqms.war`) |
| **Testing Framework** | JUnit 5 & Selenium WebDriver | Headless Chrome automated tests |
| **CI/CD Automation** | Jenkins Pipeline | Jenkinsfile (Multi-stage CI/CD) |
| **Containerization** | Docker | Multi-stage Dockerfile, Alpine runtime |
| **Configuration Mgmt** | Ansible / Puppet | Idempotent playbook / manifest for deployment |

---

## 4. Repository Structure
```
project/
├── .github/
│   └── ISSUE_TEMPLATE/
│       ├── bug_report.md          # Standard bug report template
│       ├── feature_request.md     # Feature enhancement proposal
│       └── user_story.md          # Agile sprint user story template
├── src/
│   ├── main/
│   │   ├── java/com/tqms/
│   │   │   ├── TqmsApplication.java           # Spring Boot application entrypoint
│   │   │   ├── ServletInitializer.java        # WAR deployment initializer
│   │   │   ├── config/                        # App & security configuration
│   │   │   ├── controller/
│   │   │   │   ├── QueueApiController.java    # REST API endpoints
│   │   │   │   └── WebController.java         # Web UI MVC controller
│   │   │   ├── dto/                           # Data Transfer Objects
│   │   │   ├── model/                         # JPA Entities & Enums
│   │   │   ├── repository/                    # Spring Data JPA repositories
│   │   │   └── service/                       # Business logic & state machine
│   │   └── resources/
│   │       ├── application.properties         # Environment & datasource settings (Port 8081)
│   │       ├── static/                        # CSS styles and JavaScript client
│   │       └── templates/                     # Thymeleaf UI templates (index.html)
│   └── test/
│       └── java/com/tqms/
│           └── TqmsApplicationTests.java      # Application context smoke tests
├── .gitignore                                 # Git ignore patterns for Maven, IDE, OS
├── BRANCHING_POLICY.md                        # Branch naming, PR rules, commit standards
├── pom.xml                                    # Maven POM configuration (Java 17, Spring Boot 3)
└── README.md                                  # Complete project documentation
```

---

## 5. Local Quickstart Guide

### Prerequisites
- JDK 17 or higher (`java -version`)
- Apache Maven 3.9+ (`mvn -version`)
- Git (`git --version`)

### Build & Run
```bash
# 1. Clone the repository
git clone https://github.com/Sheshank-singh/telemedicine-queue-management-system.git
cd telemedicine-queue-management-system

# 2. Package production WAR with metrics profile and testing
mvn clean package -Pmetrics -DskipTests=false

# 3. Run application on local port 8081
mvn spring-boot:run
```

Once started:
- Access Web Application: `http://localhost:8081/`
- Health Check: `http://localhost:8081/actuator/health`
- H2 Database Console: `http://localhost:8081/h2-console`

---

## 6. REST API Specification

| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/records` | Register patient & issue token | Receptionist |
| `GET` | `/api/records` | List active queue records | Staff |
| `GET` | `/api/records/{id}` | Retrieve patient record by ID | Staff |
| `PUT` | `/api/records/{id}` | Update patient details | Receptionist |
| `GET` | `/api/records/search` | Search by query (name/token) and status | Staff |
| `PATCH`| `/api/records/{id}/status` | Transition record status | Doctor / Receptionist |
| `GET` | `/api/public/status/{token}` | Public token queue status lookup | Public (Patients) |
| `GET` | `/api/dashboard/summary` | Queue statistics summary | Admin |
| `GET` | `/actuator/health` | Application health status | Public / Monitoring |

---

## 7. DevOps Curriculum Roadmap (Tasks 1 to 15)
- [x] **Task 1:** Problem Definition and Scope
- [x] **Task 2:** Agile Planning and DevOps Workflow
- [x] **Task 3:** Requirements, Architecture and Technology Setup
- [x] **Task 4:** Git and GitHub Repository Initialization
- [ ] **Task 5:** Feature Development with Branching
- [ ] **Task 6:** MVP Completion and Git Collaboration
- [ ] **Task 7:** Jenkins Installation and Continuous Integration Job
- [ ] **Task 8:** Pipeline as Code and Server Deployment (Tomcat)
- [ ] **Task 9:** Selenium Test Design and Local Execution
- [ ] **Task 10:** Continuous Testing in Jenkins
- [ ] **Task 11:** Docker Image and Container Lifecycle
- [ ] **Task 12:** Jenkins-Docker Continuous Deployment
- [ ] **Task 13:** Configuration Management Script (Ansible/Puppet)
- [ ] **Task 14:** Automated Provisioning and Reliability Validation
- [ ] **Task 15:** Final End-to-End Release, Documentation and Viva
