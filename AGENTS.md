# Agents Specification

Este repositório suporta uso de agentes automatizados de geração de código.

Este documento descreve como agentes devem operar.

Este documento é **documentação viva**.

---

## Arquitetura obrigatória

O sistema deve seguir:

* Domain Driven Design
* Clean Architecture

---

## Estrutura de diretórios obrigatória

```
domain
application
infrastructure
interfaces
```

---

## Regras arquiteturais

### Domain

O domínio:

* não depende de frameworks
* não depende de banco
* não depende de mensageria

---

### Application

Responsável por:

```
use cases
```

---

### Infrastructure

Responsável por:

```
persistence
messaging
framework integrations
```

---

### Interfaces

Responsável por:

```
REST controllers
DTOs
external adapters
```

---

## Convenções

IDs:

```
UUID
```

Eventos:

```
domain/event
```

Repositórios:

```
domain/repository
```

Implementações:

```
infrastructure/persistence
```

---

## Validações

Validações críticas devem existir:

* no domínio
* não apenas na API

---

## Testes

Testes são obrigatórios.

Cobertura mínima:

```
80%
```

Agentes devem gerar:

* unit tests
* integration tests

---

## Responsabilidades de geração de código

Agentes devem ser capazes de gerar:

1. entidades de domínio
2. use cases
3. entidades JPA
4. repositórios
5. producers RabbitMQ
6. consumers RabbitMQ
7. migrations Flyway
8. controllers REST
9. testes

