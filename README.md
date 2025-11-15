# contatos-spring-react

Sistema de gerenciamento de contatos com backend Spring Boot e frontend React (em desenvolvimento).

## 📁 Estrutura do Projeto

```
contatos-spring-react/
├── api/                    # Backend - Spring Boot API
│   ├── src/
│   ├── pom.xml
│   ├── README.md          # Documentação completa da API
│   └── docker-compose.yml
└── frontend/              # Frontend - React (a ser desenvolvido)
```

## 🚀 Quick Start

### Backend (API)

A API está completamente implementada e pronta para uso. Veja a documentação completa em [`api/README.md`](./api/README.md).

**Início rápido com Docker:**

```bash
cd api
docker-compose up
```

A API estará disponível em `http://localhost:8080`

**Endpoints principais:**
- `POST /api/auth/registro` - Registrar usuário
- `POST /api/auth/login` - Login
- `GET /api/contatos` - Listar contatos (autenticado)
- `POST /api/contatos` - Criar contato (autenticado)

### Frontend

O frontend será desenvolvido em React seguindo Material Design v2/v3.

## 📚 Documentação

- **API Backend**: [api/README.md](./api/README.md)
- **OpenAPI Spec**: [api/contatos-api.yaml](./api/contatos-api.yaml)
- **Documento de Requisitos**: [Teste Desenvolvedor Fullstack Java Pleno [CUR].pdf](./Teste%20Desenvolvedor%20Fullstack%20Java%20Pleno%20%5BCUR%5D.pdf)

## 🛠 Tecnologias

### Backend
- Java 21
- Spring Boot 3.5.7
- PostgreSQL
- JWT Authentication
- Docker

### Frontend (a desenvolver)
- React
- Material-UI
- Google Maps API
- Axios

## 📝 Licença

Este projeto foi desenvolvido como teste técnico para desenvolvedor Fullstack Java Pleno.
