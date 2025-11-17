import { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  Grid,
  MenuItem,
  Alert,
  CircularProgress,
  Box,
  Autocomplete,
  Paper,
  Typography,
} from '@mui/material';
import { contatosAPI, enderecosAPI } from '../services/api';
import { validarCPF, formatarCPF, formatarTelefone } from '../utils/validators';

const estados = [
  'AC', 'AL', 'AP', 'AM', 'BA', 'CE', 'DF', 'ES', 'GO', 'MA',
  'MT', 'MS', 'MG', 'PA', 'PB', 'PR', 'PE', 'PI', 'RJ', 'RN',
  'RS', 'RO', 'RR', 'SC', 'SP', 'SE', 'TO'
];

export default function ContatoForm({ open, contato, onSave, onClose }) {
  const [formData, setFormData] = useState({
    nome: '',
    cpf: '',
    telefone: '',
    cep: '',
    logradouro: '',
    numero: '',
    complemento: '',
    bairro: '',
    cidade: '',
    estado: '',
    latitude: 0,
    longitude: 0,
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [cepLoading, setCepLoading] = useState(false);
  const [error, setError] = useState('');
  const [logradouroSuggestions, setLogradouroSuggestions] = useState([]);
  const [logradouroLoading, setLogradouroLoading] = useState(false);
  const [logradouroInputValue, setLogradouroInputValue] = useState('');
  const [cpfChecking, setCpfChecking] = useState(false);

  useEffect(() => {
    if (contato) {
      setFormData({
        ...contato,
        cpf: formatarCPF(contato.cpf || ''),
        telefone: formatarTelefone(contato.telefone || ''),
      });
      setLogradouroInputValue(contato.logradouro || '');
    } else {
      setFormData({
        nome: '',
        cpf: '',
        telefone: '',
        cep: '',
        logradouro: '',
        numero: '',
        complemento: '',
        bairro: '',
        cidade: '',
        estado: '',
        latitude: 0,
        longitude: 0,
      });
      setLogradouroInputValue('');
    }
    setErrors({});
    setError('');
    setLogradouroSuggestions([]);
  }, [contato, open]);

  // Fetch address suggestions when typing logradouro
  useEffect(() => {
    const fetchSuggestions = async () => {
      if (logradouroInputValue.length >= 3 && formData.estado && formData.cidade) {
        setLogradouroLoading(true);
        try {
          const response = await enderecosAPI.buscarEnderecos(
            formData.estado,
            formData.cidade,
            logradouroInputValue
          );
          setLogradouroSuggestions(response.data || []);
        } catch (err) {
          console.error('Erro ao buscar sugestões:', err);
          setLogradouroSuggestions([]);
        } finally {
          setLogradouroLoading(false);
        }
      } else {
        setLogradouroSuggestions([]);
      }
    };

    const timeoutId = setTimeout(fetchSuggestions, 500);
    return () => clearTimeout(timeoutId);
  }, [logradouroInputValue, formData.estado, formData.cidade]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    
    let formattedValue = value;
    
    // Format CPF as user types
    if (name === 'cpf') {
      const numbers = value.replace(/[^\d]/g, '');
      if (numbers.length <= 11) {
        formattedValue = formatarCPF(numbers);
      } else {
        return; // Don't allow more than 11 digits
      }
    }
    
    // Format phone as user types
    if (name === 'telefone') {
      const numbers = value.replace(/[^\d]/g, '');
      if (numbers.length <= 11) {
        formattedValue = formatarTelefone(numbers);
      } else {
        return; // Don't allow more than 11 digits
      }
    }
    
    // Format numero to only numbers
    if (name === 'numero') {
      formattedValue = value.replace(/[^\d]/g, '');
    }
    
    setFormData(prev => ({ ...prev, [name]: formattedValue }));
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const handleCpfBlur = async () => {
    const cpfNumbers = formData.cpf.replace(/[^\d]/g, '');
    
    if (cpfNumbers.length === 11) {
      // Check if CPF is already registered (skip if editing the same contact)
      if (!contato || cpfNumbers !== contato.cpf) {
        setCpfChecking(true);
        try {
          // Use dedicated backend endpoint to verify CPF
          const response = await contatosAPI.verificarCpf(cpfNumbers);
          if (response.data.exists) {
            setErrors(prev => ({ ...prev, cpf: 'CPF já cadastrado no sistema' }));
          }
        } catch (err) {
          console.error('Erro ao verificar CPF:', err);
        } finally {
          setCpfChecking(false);
        }
      }
    }
  };

  const handleLogradouroSelect = (event, value) => {
    if (value) {
      // value is an address object from ViaCEP
      setFormData(prev => ({
        ...prev,
        logradouro: value.logradouro,
        cep: value.cep.replace('-', ''),
        bairro: value.bairro || prev.bairro,
      }));
      setLogradouroInputValue(value.logradouro);
    }
  };

  const handleCepBlur = async () => {
    const cep = formData.cep.replace(/\D/g, '');
    if (cep.length === 8) {
      setCepLoading(true);
      try {
        const response = await enderecosAPI.buscarCep(cep);
        const data = response.data;
        setFormData(prev => ({
          ...prev,
          logradouro: data.logradouro || prev.logradouro,
          bairro: data.bairro || prev.bairro,
          cidade: data.localidade || prev.cidade,
          estado: data.uf || prev.estado,
        }));
        setLogradouroInputValue(data.logradouro || prev.logradouro);
      } catch (err) {
        console.error('Erro ao buscar CEP:', err);
      } finally {
        setCepLoading(false);
      }
    }
  };

  const validate = () => {
    const newErrors = {};

    if (!formData.nome) newErrors.nome = 'Nome é obrigatório';
    
    const cpfNumbers = formData.cpf.replace(/[^\d]/g, '');
    if (!cpfNumbers) {
      newErrors.cpf = 'CPF é obrigatório';
    } else if (cpfNumbers.length !== 11) {
      newErrors.cpf = 'CPF deve ter 11 dígitos';
    } else if (!validarCPF(cpfNumbers)) {
      newErrors.cpf = 'CPF inválido';
    }
    
    if (!formData.telefone) newErrors.telefone = 'Telefone é obrigatório';
    if (!formData.cep) newErrors.cep = 'CEP é obrigatório';
    if (!formData.logradouro) newErrors.logradouro = 'Logradouro é obrigatório';
    if (!formData.numero) newErrors.numero = 'Número é obrigatório';
    if (!formData.bairro) newErrors.bairro = 'Bairro é obrigatório';
    if (!formData.cidade) newErrors.cidade = 'Cidade é obrigatória';
    if (!formData.estado) newErrors.estado = 'Estado é obrigatório';

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    setLoading(true);
    setError('');

    try {
      // Remove formatação do CPF, CEP e telefone
      const data = {
        ...formData,
        cpf: formData.cpf.replace(/\D/g, ''),
        cep: formData.cep.replace(/\D/g, ''),
        telefone: formData.telefone.replace(/\D/g, ''),
      };

      if (contato?.id) {
        await contatosAPI.update(contato.id, data);
      } else {
        await contatosAPI.create(data);
      }
      onSave();
    } catch (err) {
      setError(err.response?.data?.message || 'Erro ao salvar contato');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog 
      open={open} 
      onClose={(event, reason) => {
        // Only close on button click or ESC, not on backdrop click
        if (reason !== 'backdropClick') {
          onClose();
        }
      }} 
      maxWidth="md" 
      fullWidth
    >
      <DialogTitle>
        {contato ? 'Editar Contato' : 'Novo Contato'}
      </DialogTitle>
      <form onSubmit={handleSubmit}>
        <DialogContent>
          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Nome"
                name="nome"
                value={formData.nome}
                onChange={handleChange}
                error={!!errors.nome}
                helperText={errors.nome}
                required
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="CPF"
                name="cpf"
                value={formData.cpf}
                onChange={handleChange}
                onBlur={handleCpfBlur}
                error={!!errors.cpf}
                helperText={errors.cpf || 'Formato: 000.000.000-00'}
                required
                InputProps={{
                  endAdornment: cpfChecking && <CircularProgress size={20} />,
                }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Telefone"
                name="telefone"
                value={formData.telefone}
                onChange={handleChange}
                error={!!errors.telefone}
                helperText={errors.telefone || 'Formato: (00) 00000-0000'}
                required
              />
            </Grid>
            <Grid item xs={12} sm={4}>
              <TextField
                fullWidth
                label="CEP"
                name="cep"
                value={formData.cep}
                onChange={handleChange}
                onBlur={handleCepBlur}
                error={!!errors.cep}
                helperText={errors.cep}
                required
                InputProps={{
                  endAdornment: cepLoading && <CircularProgress size={20} />,
                }}
                inputProps={{ maxLength: 9 }}
              />
            </Grid>
            <Grid item xs={12} sm={4}>
              <TextField
                fullWidth
                label="Cidade"
                name="cidade"
                value={formData.cidade}
                onChange={handleChange}
                error={!!errors.cidade}
                helperText={errors.cidade}
                required
              />
            </Grid>
            <Grid item xs={12} sm={4}>
              <TextField
                fullWidth
                select
                label="UF"
                name="estado"
                value={formData.estado}
                onChange={handleChange}
                error={!!errors.estado}
                helperText={errors.estado}
                required
              >
                {estados.map((uf) => (
                  <MenuItem key={uf} value={uf}>
                    {uf}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12} sm={8}>
              <Autocomplete
                freeSolo
                options={logradouroSuggestions}
                getOptionLabel={(option) => 
                  typeof option === 'string' ? option : option.logradouro || ''
                }
                renderOption={(props, option) => (
                  <li {...props}>
                    <Box>
                      <Typography variant="body1">{option.logradouro}</Typography>
                      <Typography variant="caption" color="text.secondary">
                        {option.bairro} - CEP: {option.cep}
                      </Typography>
                    </Box>
                  </li>
                )}
                inputValue={logradouroInputValue}
                onInputChange={(event, newInputValue) => {
                  setLogradouroInputValue(newInputValue);
                  if (event?.type === 'change') {
                    setFormData(prev => ({ ...prev, logradouro: newInputValue }));
                  }
                }}
                onChange={handleLogradouroSelect}
                loading={logradouroLoading}
                disabled={!formData.estado || !formData.cidade}
                renderInput={(params) => (
                  <TextField
                    {...params}
                    label="Logradouro"
                    error={!!errors.logradouro}
                    helperText={
                      errors.logradouro || 
                      (!formData.estado || !formData.cidade 
                        ? 'Preencha UF e Cidade primeiro' 
                        : 'Digite 3 ou mais letras para sugestões')
                    }
                    required
                    InputProps={{
                      ...params.InputProps,
                      endAdornment: (
                        <>
                          {logradouroLoading ? <CircularProgress size={20} /> : null}
                          {params.InputProps.endAdornment}
                        </>
                      ),
                    }}
                  />
                )}
              />
            </Grid>
            <Grid item xs={12} sm={4}>
              <TextField
                fullWidth
                label="Número"
                name="numero"
                value={formData.numero}
                onChange={handleChange}
                error={!!errors.numero}
                helperText={errors.numero}
                required
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Complemento"
                name="complemento"
                value={formData.complemento}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Bairro"
                name="bairro"
                value={formData.bairro}
                onChange={handleChange}
                error={!!errors.bairro}
                helperText={errors.bairro}
                required
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={onClose}>Cancelar</Button>
          <Button type="submit" variant="contained" disabled={loading || cpfChecking}>
            {loading ? 'Salvando...' : 'Salvar'}
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
}