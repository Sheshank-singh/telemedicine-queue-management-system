# Git Branching Strategy & Contribution Policy

## 1. Overview
The **DevOps-Enabled Telemedicine Queue Management System (TQMS)** follows a structured **GitFlow-inspired branching strategy** to guarantee stability across automated CI/CD pipelines, Selenium testing gates, Docker builds, and deployment stages.

---

## 2. Core Branches

| Branch | Lifecycle | Protection Level | Purpose |
| :--- | :--- | :--- | :--- |
| `main` | Permanent | Protected (No direct commits) | Production-ready baseline. All commits must be tagged releases (`v1.0.0`). |
| `develop` | Permanent | Integration baseline | Integration branch for verified feature work and continuous integration builds. |

---

## 3. Supporting Branches

| Branch Prefix | Example | Base Branch | Target Merge |
| :--- | :--- | :--- | :--- |
| `feature/<name>` | `feature/patient-registration` | `develop` | `develop` via Pull Request |
| `bugfix/<name>` | `bugfix/status-transition-null` | `develop` | `develop` via Pull Request |
| `release/<version>` | `release/v1.0.0` | `develop` | `main` and back to `develop` |
| `hotfix/<name>` | `hotfix/critical-db-timeout` | `main` | `main` and `develop` |

---

## 4. Branch Naming Rules
1. All branch names must be **lowercase** using hyphens (`-`) as separators.
2. Feature branches must specify the feature domain: `feature/queue-status-workflow`, `feature/token-search`.
3. Never use spaces, uppercase characters, or special punctuation.

---

## 5. Commit Message Standard (Conventional Commits)
All commit messages must adhere to the Conventional Commits specification:
```
<type>(<scope>): <short description in present tense>

[optional body describing why and how]

[optional footer referencing issue numbers: Closes #12]
```

### Allowed Types:
- `feat`: A new feature or capability.
- `fix`: A bug fix.
- `docs`: Documentation updates.
- `chore`: Build scripts, dependencies, configuration files.
- `test`: Adding or refactoring unit or Selenium test cases.
- `ci`: CI/CD pipeline changes (Jenkinsfile, GitHub Actions).
- `refactor`: Code changes that neither fix bugs nor add features.

---

## 6. Pull Request & Review Policy
1. Direct pushes to `main` are strictly prohibited.
2. PRs targeting `develop` must have:
   - Descriptive title and summary linking the relevant GitHub Issue.
   - Clean local build pass (`mvn clean test`).
   - At least 1 code review approval before merging.
   - No unresolved merge conflicts.
3. Feature branches are merged using `--no-ff` (non-fast-forward) to preserve commit topology history in git log.
