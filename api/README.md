# API de Gerenciamento de Contatos

Sistema de cadastro de contatos com integração Via CEP e Google Maps, desenvolvido com Spring Boot 3.5.7 e Java 21.

## 📋 Características

- **Autenticação JWT**: Sistema de autenticação seguro com tokens JWT
- **Gerenciamento de Contatos**: CRUD completo com paginação, ordenação e filtros
- **Validação de CPF**: Algoritmo oficial de validação com detecção de duplicatas
- **Integração ViaCEP**: Consulta de endereços por CEP e busca avançada
- **Geocodificação**: Integração com Google Maps para obter coordenadas geográficas
- **API RESTful**: Endpoints bem estruturados seguindo as melhores práticas REST

## 🚀 Tecnologias

- Java 21
- Spring Boot 3.5.7
- Spring Security (JWT)
- Spring Data JPA
- PostgreSQL (produção)
- H2 Database (testes)
- Lombok
- Bean Validation
- Maven

## 📦 Pré-requisitos

- JDK 21
- Maven 3.6+
- PostgreSQL 12+ (para ambiente de produção)
- Google Maps API Key (opcional, para geocodificação automática)

## 🔧 Configuração

### 1. Clone o repositório

```bash
git clone https://github.com/ViniciusVRodrigues/contatos-spring-react.git
cd contatos-spring-react/api
```

### 2. Configure o banco de dados

Crie um banco de dados PostgreSQL:

```sql
CREATE DATABASE contatos;
```

### 3. Configure as variáveis de ambiente

Crie um arquivo `.env` ou configure as variáveis de ambiente:

```bash
# Database
export DATABASE_URL=jdbc:postgresql://localhost:5432/contatos
export DATABASE_USERNAME=seu_usuario
export DATABASE_PASSWORD=sua_senha

# JWT
export JWT_SECRET=sua-chave-secreta-super-segura-com-pelo-menos-256-bits-para-hs256

# Google Maps (opcional)
export GOOGLE_MAPS_API_KEY=sua-api-key-do-google-maps
```

### 4. Compile e execute

```bash
./mvnw clean install
./mvnw spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

## 🧪 Executar Testes

```bash
./mvnw test
```

## 📚 Documentação da API

A API segue a especificação OpenAPI 3.0 disponível em `contatos-api.yaml`.

### Endpoints Principais

#### Autenticação

- `POST /api/auth/registro` - Registrar novo usuário
- `POST /api/auth/login` - Realizar login

#### Contatos (requer autenticação)

- `GET /api/contatos` - Listar contatos (com paginação e filtros)
- `GET /api/contatos/{id}` - Buscar contato por ID
- `POST /api/contatos` - Criar novo contato
- `PUT /api/contatos/{id}` - Atualizar contato
- `DELETE /api/contatos/{id}` - Deletar contato

#### Endereços (requer autenticação)

- `GET /api/enderecos/cep/{cep}` - Buscar endereço por CEP
- `GET /api/enderecos/search?uf={uf}&cidade={cidade}&logradouro={logradouro}` - Buscar endereços

#### Conta (requer autenticação)

- `POST /api/conta` - Deletar conta do usuário

### Exemplo de Uso

#### 1. Registrar Usuário

```bash
curl -X POST http://localhost:8080/api/auth/registro \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@example.com",
    "senha": "senha123"
  }'
```

#### 2. Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@example.com",
    "senha": "senha123"
  }'
```

Resposta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipo": "Bearer",
  "usuario": {
    "id": 1,
    "nome": "João Silva",
    "email": "joao@example.com"
  }
}
```

#### 3. Criar Contato

```bash
curl -X POST http://localhost:8080/api/contatos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {seu-token}" \
  -d '{
    "nome": "Maria Santos",
    "cpf": "12345678909",
    "telefone": "41999887766",
    "cep": "80010000",
    "logradouro": "Rua das Flores",
    "numero": "123",
    "bairro": "Centro",
    "cidade": "Curitiba",
    "estado": "PR"
  }'
```

**Nota**: Se as coordenadas (latitude/longitude) não forem fornecidas, o sistema tentará obtê-las automaticamente via Google Maps Geocoding API (requer configuração da API key).

#### 4. Buscar CEP

```bash
curl -X GET http://localhost:8080/api/enderecos/cep/80010000 \
  -H "Authorization: Bearer {seu-token}"
```

## 🔐 Segurança

- **Autenticação JWT**: Tokens com expiração de 24 horas
- **Senha**: Criptografadas com BCrypt
- **CORS**: Configurado para aceitar requisições do frontend
- **Validação**: Bean Validation em todas as entradas
- **Autorização**: Usuários só podem acessar seus próprios contatos

## 🎯 Regras de Negócio

1. **CPF**: Validado segundo algoritmo oficial, não permitindo duplicatas por usuário
2. **Email**: Único no sistema
3. **Complemento**: Único campo opcional no endereço
4. **Paginação**: Padrão de 10 itens por página
5. **Ordenação**: Padrão alfabético crescente por nome
6. **Filtro**: Busca por nome ou CPF
7. **Geocodificação**: Automática se coordenadas não fornecidas

## 📁 Estrutura do Projeto

```
api/
├── src/
│   ├── main/
│   │   ├── java/com/contatos/api/
│   │   │   ├── config/          # Configurações (Security, CORS)
│   │   │   ├── controller/      # Controllers REST
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── exception/        # Exceções customizadas
│   │   │   ├── model/            # Entidades JPA
│   │   │   ├── repository/       # Repositories
│   │   │   ├── security/         # JWT e autenticação
│   │   │   ├── service/          # Lógica de negócio
│   │   │   └── util/             # Utilitários (CPF Validator)
│   │   └── resources/
│   │       └── application.properties
│   └── test/                     # Testes unitários e de integração
├── contatos-api.yaml             # Especificação OpenAPI
└── pom.xml                       # Dependências Maven
```

## 🚢 Deploy

### Usando Docker (Recomendado)

```bash
# Build
docker build -t contatos-api .

# Run
docker run -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/contatos \
  -e DATABASE_USERNAME=postgres \
  -e DATABASE_PASSWORD=postgres \
  -e JWT_SECRET=sua-chave-secreta \
  -e GOOGLE_MAPS_API_KEY=sua-api-key \
  contatos-api
```

### Build para Produção

```bash
./mvnw clean package -DskipTests
java -jar target/api-0.0.1-SNAPSHOT.jar
```

## 🧑‍💻 Desenvolvimento

### Executar em modo desenvolvimento

```bash
./mvnw spring-boot:run
```

O DevTools está habilitado para hot reload automático.

### Acessar H2 Console (em testes)

Ao executar testes, o H2 Console está disponível em: `http://localhost:8080/h2-console`

## 📝 Licença

Este projeto foi desenvolvido como teste técnico para desenvolvedor Fullstack Java Pleno.

## 👥 Autor

Desenvolvido por [Vinicius Veiga Rodrigues](https://github.com/ViniciusVRodrigues)

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📞 Suporte

Para questões e suporte, abra uma issue no repositório do projeto.
