# GastroFlow CA

Sistema de gestão para restaurantes desenvolvido para o Tech Challenge (Fase 2) da pós-graduação em Arquitetura e Desenvolvimento Java da FIAP.

Permite cadastrar tipos de usuário, restaurantes e itens de cardápio, com autenticação via login e JWT.

> Para mais detalhes e decisões técnicas, veja a documentação do projeto.
<!-- [`documentação`](./documentacao.pdf) -->

## Funcionalidades

- Cadastro e gestão de tipos de usuário (ex.: Admin, Cliente, Dono de Restaurante), cada um com um conjunto de permissões.
- Cadastro e gestão de usuários, associados a um tipo de usuário.
- Cadastro e gestão de restaurantes (nome, endereço, tipo de cozinha, horário de funcionamento e dono).
- Cadastro e gestão de itens de cardápio, vinculados a um restaurante.
- Login com autenticação via JWT.
- Autorização baseada em permissões do tipo de usuário, incluindo distinção entre ações sobre os próprios dados/restaurante e ações sobre dados de terceiros.

## Stack

- Java 21 + Spring Boot
- Spring Data JPA + PostgreSQL
- Spring Security + JWT (JJWT)
- Clean Architecture (camada `core` isolada de Spring)
- Docker / Docker Compose
- JUnit 5 + Mockito (testes unitários e de integração)

## Arquitetura

O projeto segue Clean Architecture, com a camada de regras de negócio isolada do Spring:

- `core`: entidades, use cases, controllers, gateways, interfaces, DTOs, exceptions e enums. Não depende de nenhuma classe do Spring.
- `api`: camada de infraestrutura (Spring). RestAdapters (controllers REST), configuração, segurança (JWT), persistência JPA e o handler global de exceções.

Fluxo de uma requisição: `RestAdapter` (api) recebe o HTTP, monta o DTO de entrada e chama o `Controller` (core), que aciona o `UseCase` correspondente, passando por um `Gateway` (core) até o `DataSource` (implementado em `api` com JPA).

## Endpoints

| Recurso | Método | Rota | Descrição |
|---|---|---|---|
| Auth | POST | `/auth/login` | Login, retorna token JWT |
| Usuários | POST | `/users` | Cria usuário |
| Usuários | GET | `/users` | Lista usuários (paginado) |
| Usuários | GET | `/users/{id}` | Busca usuário por id |
| Usuários | PUT | `/users/{id}` | Atualiza usuário |
| Usuários | DELETE | `/users/{id}` | Remove usuário |
| Tipos de usuário | POST | `/usertype` | Cria tipo de usuário |
| Tipos de usuário | GET | `/usertype` | Lista tipos de usuário (paginado) |
| Tipos de usuário | GET | `/usertype/{id}` | Busca tipo de usuário por id |
| Tipos de usuário | PUT | `/usertype/{id}` | Atualiza tipo de usuário |
| Tipos de usuário | DELETE | `/usertype/{id}` | Remove tipo de usuário |
| Restaurantes | POST | `/restaurants` | Cria restaurante |
| Restaurantes | GET | `/restaurants` | Lista restaurantes (paginado) |
| Restaurantes | GET | `/restaurants/{id}` | Busca restaurante por id |
| Restaurantes | PUT | `/restaurants/{id}` | Atualiza restaurante |
| Restaurantes | DELETE | `/restaurants/{id}` | Remove restaurante |
| Itens de cardápio | POST | `/menu-items` | Cria item de cardápio |
| Itens de cardápio | GET | `/menu-items` | Lista itens de cardápio (paginado) |
| Itens de cardápio | GET | `/menu-items/{id}` | Busca item por id |
| Itens de cardápio | PUT | `/menu-items/{id}` | Atualiza item |
| Itens de cardápio | DELETE | `/menu-items/{id}` | Remove item |

Todas as rotas exigem o header `Authorization: Bearer <token>`, exceto `/auth/login`.

## Configuração `.env`

1. Copie `.env.example` para `.env` e preencha as variáveis (banco de dados, `JWT_SECRET`, credenciais do admin).

```bash
cp .env.example .env
```

Conteúdo recomendado:

```dotenv
DB_URL=jdbc:postgresql://postgres:5432/gastroflow_ca
DB_NAME=gastroflow_ca
DB_USERNAME=postgres
DB_PASSWORD=postgres

JWT_SECRET=uma-chave-com-pelo-menos-32-caracteres
JWT_EXPIRATION_MS=3600000

ADMIN_NAME=Admin
ADMIN_EMAIL=admin@gastoflow.com
ADMIN_PASSWORD=admin123
```

## Como rodar com Docker

2. Suba a aplicação e o banco:

```bash
docker compose up -d --build
```

A API sobe em `http://localhost:8080`.

## Como rodar localmente (sem Docker)

1. Tenha um PostgreSQL rodando e configure o `.env` (ou variáveis de ambiente equivalentes).
2. Rode:

```bash
mvn spring-boot:run
```

## Testes

```bash
mvn test
```

Inclui testes unitários (use cases, controllers, segurança) e testes de integração (fluxo HTTP completo com banco H2 em memória).

## Usuário inicial

Ao subir a aplicação, um seeder cria automaticamente um usuário Admin (login `admin`, com todas as permissões) e usuários de exemplo (`cliente`, `restowner`), além de um restaurante e itens de cardápio de demonstração. A senha do admin é definida em `.env` (`ADMIN_PASSWORD`).

## Testando a API

Uma collection do Postman está disponível em [`gastroflow_ca.postman_collection.json`](./gastroflow_ca.postman_collection.json).
