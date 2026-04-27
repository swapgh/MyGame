# Auth Protocol

This document describes the temporary line-based TCP protocol used by the Phase 2 auth server.

## Format

Each packet is sent as a single line:

```text
OPCODE|field1|field2|...
```

Rules:

- fields are separated by `|`
- one packet per line
- escaping is not supported yet
- this is a temporary development protocol, not the final production format

## Requests

### Login

```text
LOGIN_REQUEST|username|password
```

Example:

```text
LOGIN_REQUEST|dev|dev-password
```

### Register

```text
REGISTER_REQUEST|username|password
```

Example:

```text
REGISTER_REQUEST|newuser|supersecret
```

## Responses

### Login response

```text
LOGIN_RESPONSE|success|message|sessionToken|accountId
```

Example success:

```text
LOGIN_RESPONSE|true|LOGIN_SUCCESS|abc-token|1
```

Example failure:

```text
LOGIN_RESPONSE|false|INVALID_CREDENTIALS||-1
```

### Register response

```text
REGISTER_RESPONSE|success|message
```

Example:

```text
REGISTER_RESPONSE|true|REGISTER_SUCCESS
```

### Character list

```text
CHARACTER_LIST|accountId|name1,name2,name3
```

Example:

```text
CHARACTER_LIST|1|DevKnight
```

### Error

```text
ERROR|code|message
```

Example:

```text
ERROR|UNHANDLED_PACKET|No handler for ERROR
```

## Local Testing

Start the auth server:

```bash
./tools/run-auth-server.sh
```

Test login with the seeded development account:

```bash
./tools/test-auth-client.sh 127.0.0.1 7777 login dev dev-password
```

Test registration:

```bash
./tools/test-auth-client.sh 127.0.0.1 7777 register testuser testpass123
```
