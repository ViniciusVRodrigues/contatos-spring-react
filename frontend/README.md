# Frontend - Gerenciamento de Contatos

Interface React com Material-UI para gerenciamento de contatos com integração ViaCEP e Google Maps.

## 🚀 Tecnologias

- React 18
- Material-UI v5
- React Router v6
- Axios
- Vite
- Google Maps API

## ⚙️ Configuração

### 1. Instale as dependências

```bash
npm install
```

### 2. Configure o ambiente

Copie `.env.example` para `.env`:

```bash
cp .env.example .env
```

> **Nota**: A chave do Google Maps é opcional. Sem ela, o mapa não será exibido, mas o restante funciona normalmente.

### 3. Execute

```bash
npm run dev
```

Acesse: `http://localhost:3000`

## 🔧 Build

```bash
npm run build
```

Os arquivos de produção estarão em `dist/`.

## 📋 Funcionalidades

- Autenticação (Login/Registro)
- CRUD de contatos com paginação
- Busca por nome ou CPF
- Integração ViaCEP
- Validação de CPF
- Visualização no Google Maps
- Interface responsiva

## 🔐 Backend

A aplicação se conecta à API em `http://localhost:8080/api` (configurado via proxy no Vite).

Certifique-se de que o backend está rodando antes de iniciar o frontend.

## 👤 Autor

Desenvolvido por [Vinicius Veiga Rodrigues](https://github.com/ViniciusVRodrigues)
