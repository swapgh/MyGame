# Environment Guide

This guide explains how to run this repo on:

- Ubuntu + VS Code
- Windows + IntelliJ IDEA

It also covers what to do when Java, Gradle, or the IDE gets out of sync.

## Project Requirements

- JDK: `25`
- Gradle: use the wrapper included in the repo
- Main modules:
  - `client`
  - `server`
  - `shared`

This repo already includes:

- `gradlew` for Linux/macOS
- `gradlew.bat` for Windows
- `gradle/wrapper/gradle-wrapper.properties`

That means:

- you do not need to install Gradle manually
- the project downloads the expected Gradle version through the wrapper
- the wrapper currently points to `Gradle 9.4.0`

## Quick Rule

If the IDE and Gradle disagree, trust Gradle first.

Test that with:

### Ubuntu

```bash
./gradlew build
```

### Windows

```bat
gradlew.bat build
```

If that works but the IDE shows errors, the problem is usually IDE indexing or IDE Java configuration.

## Where To Get JDK 25

You need a real JDK, not only a JRE.

Good places to get it:

- Eclipse Temurin / Adoptium
- Oracle JDK

Recommended default for this repo:

- install Temurin JDK 25

## Ubuntu + VS Code Setup

### 1. Install JDK 25

Install a JDK 25 distribution on your system.

After installing, confirm:

```bash
java -version
javac -version
```

Both should report Java 25.

If they do not, your shell may still be pointing to another Java installation.

### 2. Open the Repo

Open the project root folder in VS Code:

- open `/home/fer/Documentos/game`
- do not open only `client/` or only `server/`

### 3. Install VS Code Extensions

Use:

- Extension Pack for Java
- Gradle for Java

### 4. Let VS Code Import the Gradle Project

When VS Code detects the Gradle project:

- allow Java project import
- allow Gradle project import

The repo already contains `.vscode/launch.json` for:

- `ClientMain`
- `AuthServerMain`
- `WorldServerMain`

### 5. Verify from Terminal

Run:

```bash
./gradlew build
./gradlew :client:runClient
```

Optional Linux helper scripts:

```bash
./tools/run-auth-server.sh
./tools/run-world-server.sh
./tools/run-client.sh
```

These are convenience scripts for Linux only. They are not required by the project itself.

## Windows + IntelliJ IDEA Setup

### 1. Install JDK 25

Install JDK 25 on Windows.

Then verify in a terminal:

```bat
java -version
javac -version
```

### 2. Open the Repo as a Gradle Project

In IntelliJ:

1. Open the project root folder
2. Let IntelliJ detect the Gradle build
3. Import or trust the Gradle project

### 3. Set Java Correctly

Check both of these:

1. `Project SDK` = JDK 25
2. `Gradle JVM` = JDK 25

If one is 25 and the other is older, IntelliJ can show false errors or fail Gradle sync.

### 4. Use Gradle Tasks

Use the Gradle tool window or terminal:

```bat
gradlew.bat build
gradlew.bat :client:runClient
gradlew.bat :server:runAuthServer
gradlew.bat :server:runWorldServer
```

Ignore:

- `.vscode/`
- `tools/*.sh`

Those are not part of your Windows IntelliJ workflow.

## What Changes Between Ubuntu/VS Code and Windows/IntelliJ

What stays the same:

- source code
- Gradle files
- module structure
- commands and task names
- Java requirement: JDK 25

What changes:

- Linux uses `./gradlew`
- Windows uses `gradlew.bat`
- VS Code may use `.vscode/launch.json`
- IntelliJ uses its own run configurations
- Linux helper scripts are `.sh` and are not for Windows

## If My PC Does Not Have The Exact JDK

### Case 1: No JDK installed

Symptoms:

- `java -version` fails
- `javac -version` fails
- Gradle import fails immediately

Fix:

1. Install JDK 25
2. Reopen terminal or IDE
3. Re-run `java -version`
4. Reimport the Gradle project

### Case 2: Java exists, but it is not version 25

Symptoms:

- `java -version` shows 17, 21, or another version
- Gradle or the IDE complains about toolchains or unsupported language level

Fix:

1. Install JDK 25
2. Point the IDE to JDK 25
3. Make sure terminal commands also use JDK 25

Important:

- do not quietly change the repo from Java 25 to another version just because one machine is missing the correct JDK
- the repo should stay consistent across machines

### Case 3: IntelliJ uses the correct JDK, but terminal does not

Symptoms:

- IntelliJ sync works
- terminal `java -version` shows another version

Fix:

- IntelliJ is configured separately from your shell
- update your system Java configuration only if you also want terminal commands to use Java 25

### Case 4: Terminal uses Java 25, but the IDE does not

Symptoms:

- `./gradlew build` works
- the IDE still shows red imports or type errors

Fix:

- in VS Code, clean Java workspace and refresh Gradle
- in IntelliJ, reimport Gradle and verify `Project SDK` and `Gradle JVM`

## Gradle Questions

### Do I need to install Gradle manually?

No.

Use:

- `./gradlew` on Ubuntu
- `gradlew.bat` on Windows

### Which Gradle version does this repo use?

The wrapper controls it.

Current wrapper version:

- `9.4.0`

That is defined in:

- `gradle/wrapper/gradle-wrapper.properties`

### What if my machine has another Gradle version installed globally?

That does not matter much if you use the wrapper.

Use the wrapper anyway.

## Common VS Code Problems

### VS Code shows many Java errors, but Gradle build works

Cause:

- stale Java Language Server cache
- Gradle import not refreshed
- wrong JDK selected by VS Code extensions

Fix order:

1. Confirm Gradle works:

```bash
./gradlew build
```

2. Run `Java: Clean Java Language Server Workspace`
3. Reload VS Code window
4. Refresh Gradle project
5. Check that VS Code is using JDK 25

### VS Code launches the wrong main class

Cause:

- stale launch configuration

Fix:

- use `.vscode/launch.json`
- if needed, delete old manual run configs and recreate them

## Common IntelliJ Problems

### IntelliJ says Gradle JVM is invalid

Cause:

- IntelliJ cannot find the configured JDK

Fix:

1. Open Gradle settings
2. Select a valid installed JDK 25
3. Reimport the project

### IntelliJ shows errors after many refactors

Cause:

- stale indexing
- Gradle model not refreshed

Fix:

1. Reload all Gradle projects
2. Verify `Project SDK`
3. Verify `Gradle JVM`
4. If needed, invalidate caches and restart

## Useful Commands

### Build everything

Ubuntu:

```bash
./gradlew build
```

Windows:

```bat
gradlew.bat build
```

### Run tests

Ubuntu:

```bash
./gradlew test
```

Windows:

```bat
gradlew.bat test
```

### Run client

Ubuntu:

```bash
./gradlew :client:runClient
```

Windows:

```bat
gradlew.bat :client:runClient
```

### Run auth server

Ubuntu:

```bash
./gradlew :server:runAuthServer
```

Windows:

```bat
gradlew.bat :server:runAuthServer
```

### Run world server

Ubuntu:

```bash
./gradlew :server:runWorldServer
```

Windows:

```bat
gradlew.bat :server:runWorldServer
```

## Recommended Habit

When changing machines:

1. Install JDK 25
2. Open the repo root
3. Run the Gradle wrapper once
4. Let the IDE import the Gradle project
5. If the IDE shows errors but Gradle builds, refresh the IDE instead of changing the code
