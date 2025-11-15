import { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Button,
  TextField,
  Grid,
  InputAdornment,
  Paper,
  Alert,
} from '@mui/material';
import {
  Add as AddIcon,
  Search as SearchIcon,
} from '@mui/icons-material';
import { contatosAPI } from '../services/api';
import ContatosList from '../components/ContatosList';
import ContatoForm from '../components/ContatoForm';
import ContatoMap from '../components/ContatoMap';

export default function Contatos() {
  const [contatos, setContatos] = useState([]);
  const [selectedContato, setSelectedContato] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [editContato, setEditContato] = useState(null);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const loadContatos = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await contatosAPI.list({
        search: search || undefined,
        page,
        size: 10,
        sort: 'nome,asc',
      });
      setContatos(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch (err) {
      setError('Erro ao carregar contatos');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadContatos();
  }, [page, search]);

  const handleAddContato = () => {
    setEditContato(null);
    setShowForm(true);
  };

  const handleEditContato = (contato) => {
    setEditContato(contato);
    setShowForm(true);
  };

  const handleDeleteContato = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir este contato?')) {
      try {
        await contatosAPI.delete(id);
        loadContatos();
        if (selectedContato?.id === id) {
          setSelectedContato(null);
        }
      } catch (err) {
        alert('Erro ao excluir contato');
        console.error(err);
      }
    }
  };

  const handleSaveContato = async () => {
    setShowForm(false);
    setEditContato(null);
    await loadContatos();
  };

  const handleCloseForm = () => {
    setShowForm(false);
    setEditContato(null);
  };

  const handleContatoClick = (contato) => {
    setSelectedContato(contato);
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" component="h1">
          Meus Contatos
        </Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={handleAddContato}
        >
          Novo Contato
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 2, mb: 2 }}>
            <TextField
              fullWidth
              placeholder="Buscar por nome ou CPF..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon />
                  </InputAdornment>
                ),
              }}
            />
          </Paper>
          <ContatosList
            contatos={contatos}
            loading={loading}
            onEdit={handleEditContato}
            onDelete={handleDeleteContato}
            onContatoClick={handleContatoClick}
            selectedContato={selectedContato}
            page={page}
            totalPages={totalPages}
            onPageChange={setPage}
          />
        </Grid>
        <Grid item xs={12} md={6}>
          <ContatoMap contato={selectedContato} />
        </Grid>
      </Grid>

      <ContatoForm
        open={showForm}
        contato={editContato}
        onSave={handleSaveContato}
        onClose={handleCloseForm}
      />
    </Box>
  );
}
