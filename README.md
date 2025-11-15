# contatos-spring-react

Sistema completo de gerenciamento de contatos com backend Spring Boot e frontend React com Material Design v3.

## 📁 Estrutura do Projeto

```
contatos-spring-react/
├── api/                    # Backend - Spring Boot API
│   ├── src/
│   ├── pom.xml
│   ├── README.md          # Documentação completa da API
│   └── docker-compose.yml
└── frontend/              # Frontend - React + Material-UI
    ├── src/
    ├── package.json
    └── README.md          # Documentação do frontend
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

O frontend foi desenvolvido em React com Material-UI v5 (Material Design v3). Veja a documentação completa em [`frontend/README.md`](./frontend/README.md).

**Início rápido:**

```bash
cd frontend
npm install
npm run dev
```

A aplicação estará disponível em `http://localhost:3000`

**Funcionalidades:**
- ✅ Login e Registro de usuários
- ✅ Lista de contatos com paginação e busca
- ✅ CRUD completo de contatos
- ✅ Integração com ViaCEP para busca de endereços
- ✅ Validação de CPF (algoritmo oficial)
- ✅ Visualização de contatos no Google Maps
- ✅ Interface responsiva com Material Design v3

## 📚 Documentação

- **API Backend**: [api/README.md](./api/README.md)
- **Frontend**: [frontend/README.md](./frontend/README.md)
- **OpenAPI Spec**: [api/contatos-api.yaml](./api/contatos-api.yaml)
- **Documento de Requisitos**: [Teste Desenvolvedor Fullstack Java Pleno [CUR].pdf](./Teste%20Desenvolvedor%20Fullstack%20Java%20Pleno%20%5BCUR%5D.pdf)

## 🛠 Tecnologias

### Backend
- Java 21
- Spring Boot 3.5.7
- PostgreSQL / H2
- JWT Authentication
- Docker
- Maven

### Frontend
- React 18
- Material-UI (MUI) v5 - Material Design v3
- React Router
- Axios
- Google Maps React
- Vite

## 🎯 Funcionalidades Completas

### Autenticação
- Registro de novos usuários
- Login com JWT
- Proteção de rotas privadas
- Logout

### Gerenciamento de Contatos
- Listar contatos com paginação
- Buscar por nome ou CPF
- Criar novo contato
- Editar contato existente
- Excluir contato
- Visualizar localização no mapa

### Integrações Externas
- **ViaCEP**: Busca automática de endereço por CEP
- **Google Maps**: Visualização da localização dos contatos

### Validações
- CPF (algoritmo oficial brasileiro)
- Email
- Campos obrigatórios
- CEP e telefone

## 🚀 Executando o Projeto Completo

### Opção 1: Desenvolvimento Local

**Terminal 1 - Backend:**
```bash
cd api
./mvnw spring-boot:run
```

**Terminal 2 - Frontend:**
```bash
cd frontend
npm install
npm run dev
```

Acesse: `http://localhost:3000`

### Opção 2: Docker (Backend)

```bash
cd api
docker-compose up
```

Depois execute o frontend normalmente.

## 📝 Licença

Este projeto foi desenvolvido como teste técnico para desenvolvedor Fullstack Java Pleno.
