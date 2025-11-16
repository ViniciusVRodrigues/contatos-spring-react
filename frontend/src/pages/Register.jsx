import { useState } from 'react';
import {
  Container,
  Box,
  Card,
  CardContent,
  TextField,
  Button,
  Typography,
  Alert,
  Link,
  CircularProgress,
  InputAdornment,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { authAPI } from '../services/api';

export default function Register() {
  const [nome, setNome] = useState('');
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [confirmSenha, setConfirmSenha] = useState('');
  const [error, setError] = useState('');
  const [emailError, setEmailError] = useState('');
  const [emailChecking, setEmailChecking] = useState(false);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { register } = useAuth();

  const validateEmail = (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  const handleEmailBlur = async () => {
    setEmailError('');
    
    if (!email) {
      setEmailError('Email é obrigatório');
      return;
    }
    
    if (!validateEmail(email)) {
      setEmailError('Email inválido');
      return;
    }
    
    // Check if email is already registered using the backend endpoint
    setEmailChecking(true);
    try {
      const response = await authAPI.verificarEmail(email);
      if (response.data.exists) {
        setEmailError('Este email já está cadastrado no sistema');
      }
    } catch (err) {
      console.error('Erro ao verificar email:', err);
    } finally {
      setEmailChecking(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    // Validate email format
    if (!validateEmail(email)) {
      setError('Email inválido');
      return;
    }

    // Check if email is already in use
    if (emailError) {
      setError('Corrija os erros no formulário antes de continuar');
      return;
    }

    if (senha !== confirmSenha) {
      setError('As senhas não coincidem');
      return;
    }

    if (senha.length < 6) {
      setError('A senha deve ter no mínimo 6 caracteres');
      return;
    }

    setLoading(true);

    try {
      await register(nome, email, senha);
      alert('Cadastro realizado com sucesso! Faça login para continuar.');
      navigate('/login');
    } catch (err) {
      const errorMessage = err.response?.data?.message || 'Erro ao cadastrar. Tente novamente.';
      if (errorMessage.toLowerCase().includes('email')) {
        setError('Este email já está cadastrado no sistema');
      } else {
        setError(errorMessage);
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container component="main" maxWidth="xs">
      <Box
        sx={{
          marginTop: 8,
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
        }}
      >
        <Card sx={{ width: '100%', mt: 3 }}>
          <CardContent>
            <Typography component="h1" variant="h5" align="center" gutterBottom>
              Criar Conta
            </Typography>
            <Typography variant="body2" align="center" color="text.secondary" sx={{ mb: 3 }}>
              Preencha os dados para se cadastrar
            </Typography>
            {error && (
              <Alert severity="error" sx={{ mb: 2 }}>
                {error}
              </Alert>
            )}
            <Box component="form" onSubmit={handleSubmit} noValidate>
              <TextField
                margin="normal"
                required
                fullWidth
                id="nome"
                label="Nome Completo"
                name="nome"
                autoComplete="name"
                autoFocus
                value={nome}
                onChange={(e) => setNome(e.target.value)}
              />
              <TextField
                margin="normal"
                required
                fullWidth
                id="email"
                label="Email"
                name="email"
                autoComplete="email"
                value={email}
                onChange={(e) => {
                  setEmail(e.target.value);
                  setEmailError('');
                }}
                onBlur={handleEmailBlur}
                error={!!emailError}
                helperText={emailError || 'Use um email válido (ex: usuario@exemplo.com)'}
                InputProps={{
                  endAdornment: emailChecking && (
                    <InputAdornment position="end">
                      <CircularProgress size={20} />
                    </InputAdornment>
                  ),
                }}
              />
              <TextField
                margin="normal"
                required
                fullWidth
                name="senha"
                label="Senha"
                type="password"
                id="senha"
                autoComplete="new-password"
                value={senha}
                onChange={(e) => setSenha(e.target.value)}
                helperText="Mínimo 6 caracteres"
              />
              <TextField
                margin="normal"
                required
                fullWidth
                name="confirmSenha"
                label="Confirmar Senha"
                type="password"
                id="confirmSenha"
                autoComplete="new-password"
                value={confirmSenha}
                onChange={(e) => setConfirmSenha(e.target.value)}
              />
              <Button
                type="submit"
                fullWidth
                variant="contained"
                sx={{ mt: 3, mb: 2 }}
                disabled={loading || !!emailError || emailChecking}
              >
                {loading ? 'Cadastrando...' : 'Cadastrar'}
              </Button>
              <Box sx={{ textAlign: 'center' }}>
                <Link href="/login" variant="body2">
                  Já tem uma conta? Entre aqui
                </Link>
              </Box>
            </Box>
          </CardContent>
        </Card>
      </Box>
    </Container>
  );
}
