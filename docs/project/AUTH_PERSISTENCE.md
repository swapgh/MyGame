# Auth Persistence

Short reference for how auth data is stored right now and how it will move to PostgreSQL later.

## Current State

- Accounts and characters are currently stored only in memory
- The active temporary implementations are:
  - [InMemoryAccountDao](/home/fer/Documentos/game/server/src/main/java/com/game/server/auth/database/InMemoryAccountDao.java:1)
  - [InMemoryCharacterDao](/home/fer/Documentos/game/server/src/main/java/com/game/server/auth/database/InMemoryCharacterDao.java:1)
- Data is lost when the auth server stops
- The `dev` account is seeded again on startup in [AuthServerMain](/home/fer/Documentos/game/server/src/main/java/com/game/server/auth/AuthServerMain.java:1)

## DAO Boundary

- Services do not talk directly to storage details
- They use DAO interfaces:
  - [AccountDao](/home/fer/Documentos/game/server/src/main/java/com/game/server/auth/database/AccountDao.java:1)
  - [CharacterDao](/home/fer/Documentos/game/server/src/main/java/com/game/server/auth/database/CharacterDao.java:1)
  - [BanDao](/home/fer/Documentos/game/server/src/main/java/com/game/server/auth/database/BanDao.java:1)

## PostgreSQL Later

- We do not need to comment out auth logic later
- The clean swap is:
  - add `PostgresAccountDao`
  - add `PostgresCharacterDao`
  - add `PostgresBanDao`
  - replace the `InMemory...Dao` wiring inside `createAuthDatabase()` in [AuthServerMain](/home/fer/Documentos/game/server/src/main/java/com/game/server/auth/AuthServerMain.java:1)

## Important Point

- The service layer should stay
- The DAO interfaces should stay
- Only the DAO implementations and database wiring should change
