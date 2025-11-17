# API de Gerenciamento de Contatos

Sistema de cadastro de contatos com integração ViaCEP e Google Maps, desenvolvido com Spring Boot 3.5.7 e Java 21.

## 🚀 Tecnologias

- Java 21
- Spring Boot 3.5.7
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- H2 (testes)
- Lombok
- SpringDoc OpenAPI

## 📦 Pré-requisitos

- JDK 21
- PostgreSQL (ou use Docker Compose)
- Google Maps API Key (opcional)

## ⚙️ Configuração Rápida

### 1. Clone e configure

```bash
git clone https://github.com/ViniciusVRodrigues/contatos-spring-react.git
cd contatos-spring-react/api
```

### 2. Configure as variáveis de ambiente

Copie `.env.example` para `.env` e ajuste as variáveis:

```bash
cp .env.example .env
```

### 3. Execute com Docker Compose (recomendado)

```bash
docker-compose up
```

Ou execute manualmente:

```bash
./mvnw spring-boot:run
```

A API estará em: `http://localhost:8080`

## 📚 Documentação

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **API Docs**: `http://localhost:8080/v3/api-docs`

### Principais Endpoints

#### Autenticação
- `POST /api/auth/registro` - Registrar usuário
- `POST /api/auth/login` - Login

#### Contatos (autenticação necessária)
- `GET /api/contatos` - Listar (paginação e filtros)
- `POST /api/contatos` - Criar
- `PUT /api/contatos/{id}` - Atualizar
- `DELETE /api/contatos/{id}` - Deletar

#### Endereços
- `GET /api/enderecos/cep/{cep}` - Buscar por CEP
- `GET /api/enderecos/search` - Buscar por UF/cidade/logradouro

## 🧪 Testes

```bash
./mvnw test
```

## 🐳 Docker

```bash
# Build
docker build -t contatos-api .

# Run
docker run -p 8080:8080 --env-file .env contatos-api
```

## 👤 Autor

Desenvolvido por [Vinicius Veiga Rodrigues](https://github.com/ViniciusVRodrigues)
