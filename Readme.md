# CodJam-Leg4

<p align="center">
  <strong>Modernizing a legacy robotics programming environment — one documented step at a time.</strong>
</p>

<p align="center">
  <a href="https://github.com/DarlysonGabriel/CodJam-Leg4">
    <img src="https://img.shields.io/badge/Project-CodJam--Leg4-30363D?style=for-the-badge&logo=github" alt="CodJam-Leg4">
  </a>
  <img src="https://img.shields.io/badge/Domain-Robotics%20%26%20Education-6A5ACD?style=for-the-badge" alt="Robotics and education">
  <img src="https://img.shields.io/badge/Status-Research%20%26%20modernization-orange?style=for-the-badge" alt="Research and modernization">
</p>

<p align="center">
  <sub>A community-oriented modernization effort around the legacy LEGAL 3 environment.</sub>
</p>

---

## Overview

**CodJam-Leg4** is an ongoing project to study, organize, document, and progressively improve the legacy **LEGAL 3** robotics programming environment.

The original environment is designed around educational robotics: users write programs that control a robot and interact with its sensors and actuators. CodJam-Leg4 explores how this experience and its supporting code can become easier to understand, maintain, and improve.

The current repository is primarily a **legacy application snapshot and modernization workspace**. It contains Java bytecode, application resources, supporting libraries, native components, and project documentation. It should not yet be interpreted as a clean, independently buildable source-code distribution.

> **Project status:** Early-stage modernization. Investigation, documentation, compatibility work, and incremental improvements are in progress. A reproducible build, complete source recovery, stable release, and end-to-end validation with the physical robot have not been established.

## Goals

The project is being developed around a few practical goals:

- **Understand the legacy system** — map the application, its components, dependencies, and execution flow before making invasive changes.
- **Improve maintainability** — make the repository easier to navigate, inspect, document, and work on.
- **Clarify the programming experience** — investigate compiler messages and language documentation so that errors are easier for learners to understand.
- **Preserve the educational purpose** — keep the focus on programming robots rather than changing the project's purpose.
- **Validate incrementally** — distinguish changes that have been implemented from those that still require testing with the original environment or hardware.

These are project goals, not a claim that every item is already implemented.

## Repository at a glance

The current tree contains several identifiable areas:

| Path | What can be identified |
| --- | --- |
| `Legal3/` | Legacy application package and compiled Java classes. |
| `PNCA/` | Compiled components associated with the compiler and sensor-related functionality. |
| `Montador/` | A component area associated with the assembler/build side of the legacy environment. |
| `Legal3Utils/` | Supporting UI/utility classes and image resources. |
| `JarMateyNatives/` | Native libraries for Windows and Linux, in 32-bit and 64-bit variants. |
| `Jama/`, `bsh/`, `org/`, `com/` | Embedded packages and third-party Java components. |
| `THIRD_PARTY_LICENSES.md` | Preliminary inventory of third-party license notices and unresolved provenance checks. |
| `checkstyle.xml` | A legacy Checkstyle configuration file. |
| `config.properties` | A small application configuration file. |

This is a high-level map of the repository as currently stored; it is not a guarantee that every component is complete, active, or independently usable.

## Current development areas

### 1. Legacy code and architecture investigation

The first challenge is understanding what is actually present in the repository. Much of the application is represented by compiled `.class` files rather than recoverable, editable Java source files.

Work in this area includes identifying package responsibilities, locating dependencies, understanding startup assumptions, and separating original application code from bundled third-party components.

### 2. Compiler messages and language clarity

A visible area of ongoing work is the review of LEGAL language/compiler messages. The repository's issue tracker records proposed wording improvements and test notes for messages involving expressions and sensors.

The intention is to make diagnostic messages clearer and more actionable for learners. A wording change is not considered fully validated merely because a patch or proposal exists: the relevant scenario still needs to be tested in the original environment.

### 3. Compatibility and execution

The repository includes native components and legacy Java artifacts. Their presence does not establish that the application currently launches or works on every operating system.

Runtime dependencies, architecture-specific libraries, Java compatibility, and hardware communication must be investigated and tested before publishing supported platform claims.

### 4. Third-party components and licensing

The repository contains multiple third-party packages and native binaries. Their licenses and exact versions do not necessarily share one common set of terms.

The file [THIRD_PARTY_LICENSES.md](THIRD_PARTY_LICENSES.md) is a **preliminary notice and inventory document**. It explicitly records unresolved version, provenance, and missing-notice checks. It is not a completed legal audit and does not apply one license to the whole repository.

Before redistributing a build, the applicable licenses, source obligations, notices, and native-library terms must be verified for the exact components included.

## Development status

| Area | Current understanding |
| --- | --- |
| Legacy application snapshot | Present in the repository as compiled artifacts and resources. |
| Repository investigation | Ongoing. |
| Compiler-message improvements | Tracked through issues and patch notes; validation is still required. |
| Editable source recovery | Not complete. |
| Reproducible build | Not established. |
| Supported platform matrix | Not established. |
| Physical robot validation | Not established. |
| Stable release | Not available yet. |

The project is therefore a **work in progress**, not a ready-to-use replacement for the original LEGAL software.

## How to follow the work

The safest way to understand what is currently being investigated is to follow the repository's issue tracker and commit history:

- [Open issues and development tasks](https://github.com/DarlysonGabriel/CodJam-Leg4/issues)
- [Commit history](https://github.com/DarlysonGabriel/CodJam-Leg4/commits/main)
- [Repository contents](https://github.com/DarlysonGabriel/CodJam-Leg4)
- [Third-party license inventory](THIRD_PARTY_LICENSES.md)

Issues may describe proposals, patch status, or tests still waiting to be performed. Check each issue's current state before treating its content as an implemented feature.

## Building and running

**There is currently no verified, reproducible build or installation procedure documented for this repository.**

The checked-in files include compiled Java classes, native libraries, and legacy dependencies, but that alone is not enough to guarantee a working launch command or a supported setup.

For now:

1. Inspect the repository and its current issues before attempting to run it.
2. Check the Java runtime and native dependencies expected by the original application.
3. Do not assume that a successful Java launch means communication with a physical robot is working.
4. Record the exact operating system, Java version, commands, and error output when testing.
5. Treat any successful run as an environment-specific test until it can be reproduced.

A proper setup guide should be added after the build and runtime requirements have been verified.

## Contributing and reporting findings

Investigation of legacy software benefits from small, reproducible observations. When reporting a problem or proposing an improvement, include:

- The operating system and Java runtime version, when relevant.
- The exact file, package, or component involved.
- The steps needed to reproduce the behavior.
- The expected result and the observed result.
- Relevant logs, screenshots, or a minimal test case.
- Whether the change was tested in the original LEGAL environment or with physical hardware.

For compiler-message changes, include an example input that triggers the message and the expected user-facing wording. Mark hardware-dependent checks clearly when they have not been performed.

Use the [issue tracker](https://github.com/DarlysonGabriel/CodJam-Leg4/issues) to discuss bugs, research findings, and proposed improvements.

## Development and AI assistance

CodJam-Leg4 is maintained by **Darlyson Gabriel**, with **ChatGPT** and **Claude Code** used as AI-assisted development tools for research, documentation, analysis, and implementation support.

AI-generated suggestions are not treated as verified results by default. Technical decisions, review, testing, project direction, and release decisions remain the responsibility of the maintainer.

## Licensing

This repository contains legacy application material and third-party components with different licensing terms.

- See [THIRD_PARTY_LICENSES.md](THIRD_PARTY_LICENSES.md) for the current preliminary inventory and unresolved checks.
- Do not assume that the entire repository is distributed under a single license.
- Verify the license and redistribution requirements of each component before reusing or publishing a modified build.

The current license inventory is informational and does not replace legal review or the original upstream notices.

---

<p align="center">
  <strong>Preserve what is useful. Understand what is old. Improve what can be verified.</strong>
  <br>
  <sub>Maintained by Darlyson Gabriel · CodJam-Leg4</sub>
</p>
