# Frontend - Sistema de Gerenciamento de Contatos

Frontend em React com Material-UI (Material Design v3) para o sistema de gerenciamento de contatos.

## 🚀 Tecnologias

- **React 18** - Biblioteca JavaScript para construção de interfaces
- **Material-UI (MUI) v5** - Componentes que implementam Material Design v3
- **React Router** - Navegação entre páginas
- **Axios** - Cliente HTTP para comunicação com a API
- **Vite** - Build tool e dev server
- **Google Maps React** - Integração com Google Maps

## 📋 Funcionalidades

- ✅ Autenticação (Login e Registro)
- ✅ Listagem de contatos com paginação
- ✅ Busca por nome ou CPF
- ✅ Cadastro de novos contatos
- ✅ Edição de contatos existentes
- ✅ Exclusão de contatos
- ✅ Integração com ViaCEP para busca de endereço
- ✅ Validação de CPF (algoritmo oficial)
- ✅ Visualização de contatos no Google Maps
- ✅ Interface responsiva com Material Design v3

## 🔧 Configuração

### 1. Instalar dependências

```bash
npm install
```

### 2. Configurar variáveis de ambiente

Crie um arquivo `.env` na raiz do projeto frontend:

```env
VITE_GOOGLE_MAPS_API_KEY=sua-chave-da-api-do-google-maps
```

> **Nota**: A chave do Google Maps é opcional. Se não configurada, o mapa não será exibido, mas o resto da aplicação funcionará normalmente.

### 3. Executar em modo de desenvolvimento

```bash
npm run dev
```

A aplicação estará disponível em: `http://localhost:3000`

### 4. Build para produção

```bash
npm run build
```

Os arquivos de produção estarão na pasta `dist/`.

## 🏗 Estrutura do Projeto

```
frontend/
├── src/
│   ├── components/          # Componentes reutilizáveis
│   │   ├── Layout.jsx       # Layout com AppBar e navegação
│   │   ├── ContatosList.jsx # Lista de contatos
│   │   ├── ContatoForm.jsx  # Formulário de contato
│   │   └── ContatoMap.jsx   # Mapa com Google Maps
│   ├── pages/               # Páginas da aplicação
│   │   ├── Login.jsx        # Página de login
│   │   ├── Register.jsx     # Página de registro
│   │   └── Contatos.jsx     # Página principal de contatos
│   ├── services/            # Serviços e API
│   │   └── api.js           # Cliente Axios e endpoints
│   ├── contexts/            # Contexts do React
│   │   └── AuthContext.jsx  # Context de autenticação
│   ├── utils/               # Utilitários
│   │   └── validators.js    # Validações e formatações
│   ├── App.jsx              # Componente principal
│   ├── main.jsx             # Entry point
│   └── theme.js             # Tema Material-UI
├── index.html
├── vite.config.js
└── package.json
```

## 🔐 Integração com o Backend

O frontend se comunica com a API backend através dos seguintes endpoints:

- **Base URL**: `http://localhost:8080/api`
- **Autenticação**: JWT Token via header `Authorization: Bearer {token}`

### Endpoints utilizados:

- `POST /auth/registro` - Registrar novo usuário
- `POST /auth/login` - Fazer login
- `GET /contatos` - Listar contatos (com paginação e busca)
- `GET /contatos/{id}` - Buscar contato específico
- `POST /contatos` - Criar novo contato
- `PUT /contatos/{id}` - Atualizar contato
- `DELETE /contatos/{id}` - Excluir contato
- `GET /enderecos/cep/{cep}` - Buscar endereço por CEP

## 🎨 Material Design v3

A interface segue as diretrizes do Material Design v3:

- **Cores primárias e secundárias** definidas no tema
- **Tipografia** Roboto
- **Componentes** com elevação e sombras apropriadas
- **Feedback visual** em interações
- **Design responsivo** para mobile e desktop

## 📱 Responsividade

A aplicação é totalmente responsiva e se adapta a diferentes tamanhos de tela:

- **Desktop**: Layout com duas colunas (lista e mapa lado a lado)
- **Mobile**: Layout em coluna única com navegação otimizada

## 🧪 Validações

- **CPF**: Validação usando algoritmo oficial brasileiro
- **Email**: Validação de formato
- **Senha**: Mínimo de 6 caracteres
- **Campos obrigatórios**: Validação em todos os campos necessários

## 🗺 Google Maps

O mapa exibe a localização do contato selecionado:

- Marcador na posição do contato
- Zoom automático na localização
- Informações do endereço no cabeçalho

## 📝 Notas

- O proxy do Vite redireciona requisições `/api` para `http://localhost:8080`
- Certifique-se de que o backend está rodando na porta 8080
- O token JWT é armazenado no localStorage
- Logout limpa o token e redireciona para login
