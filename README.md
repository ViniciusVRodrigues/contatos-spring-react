# Sistema de Gerenciamento de Contatos

Sistema completo de gerenciamento de contatos com backend Spring Boot e frontend React.

## 🚀 Tecnologias

**Backend:**
- Java 21
- Spring Boot 3.5.7
- PostgreSQL
- JWT Authentication
- Docker

**Frontend:**
- React 18
- Material-UI v5
- React Router v6
- Vite

## ⚡ Quick Start

### Backend

```bash
cd api
docker-compose up
```

API disponível em: `http://localhost:8080`

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Interface disponível em: `http://localhost:3000`

## 📚 Documentação

- **API Backend**: [api/README.md](./api/README.md)
- **Frontend**: [frontend/README.md](./frontend/README.md)
- **Swagger UI**: `http://localhost:8080/swagger-ui.html` (após iniciar a API)

## 📋 Funcionalidades

- Autenticação JWT (Login/Registro)
- CRUD de contatos com paginação
- Busca por nome ou CPF
- Integração ViaCEP
- Validação de CPF
- Visualização no Google Maps
- Interface responsiva

## 👤 Autor

Desenvolvido por [Vinicius Veiga Rodrigues](https://github.com/ViniciusVRodrigues)
