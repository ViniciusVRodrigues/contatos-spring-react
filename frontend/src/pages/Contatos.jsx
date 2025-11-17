import { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  IconButton,
  TextField,
  Drawer,
  InputAdornment,
  Alert,
  Fab,
  Toolbar,
} from '@mui/material';
import {
  Add as AddIcon,
  Search as SearchIcon,
} from '@mui/icons-material';
import { contatosAPI } from '../services/api';
import ContatosList from '../components/ContatosList';
import ContatoForm from '../components/ContatoForm';
import ContatoMap from '../components/ContatoMap';

const drawerWidth = 400;

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
    <Box sx={{ display: 'flex', height: 'calc(100vh - 64px)', width: '100vw', left: 0, top: 64, position: 'absolute' }}>
      {/* Side Menu com lista de contatos */}
      <Drawer
        variant="permanent"
        sx={{
          width: drawerWidth,
          flexShrink: 0,
          '& .MuiDrawer-paper': {
            width: drawerWidth,
            boxSizing: 'border-box',
            position: 'relative',
            height: '100%',
          },
        }}
      >
        <Box sx={{ p: 2 }}>
          <Typography variant="h5" component="h1" gutterBottom>
            Meus Contatos
          </Typography>
          
          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          {/* Search bar with + button */}
          <Box sx={{ display: 'flex', gap: 1, mb: 2 }}>
            <TextField
              fullWidth
              size="small"
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
            <Fab
              color="success"
              size="small"
              onClick={handleAddContato}
              sx={{ minWidth: 40, width: 40, height: 40 }}
            >
              <AddIcon />
            </Fab>
          </Box>

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
        </Box>
      </Drawer>

      {/* Main content area with map */}
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          width: `calc(100% - ${drawerWidth}px)`,
          height: '100%',
          overflow: 'auto',
        }}
      >
        <ContatoMap 
          contatos={contatos} 
          selectedContato={selectedContato}
          onContatoSelect={handleContatoClick}
        />
      </Box>

      <ContatoForm
        open={showForm}
        contato={editContato}
        onSave={handleSaveContato}
        onClose={handleCloseForm}
      />
    </Box>
  );
}
