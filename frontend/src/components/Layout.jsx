import { useState } from 'react';
import {
  AppBar,
  Box,
  Toolbar,
  Typography,
  IconButton,
  Menu,
  MenuItem,
  Container,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  Alert,
} from '@mui/material';
import {
  AccountCircle,
  Contacts as ContactsIcon,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { contaAPI } from '../services/api';

/**
 * Layout component that provides the main application structure
 * Includes navigation bar with user menu and account management options
 */
export default function Layout({ children }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [anchorEl, setAnchorEl] = useState(null);
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);
  const [deletePassword, setDeletePassword] = useState('');
  const [deleteError, setDeleteError] = useState('');
  const [deleteLoading, setDeleteLoading] = useState(false);

  const handleMenu = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
    handleClose();
  };

  const handleOpenDeleteDialog = () => {
    setShowDeleteDialog(true);
    handleClose();
  };

  const handleCloseDeleteDialog = () => {
    setShowDeleteDialog(false);
    setDeletePassword('');
    setDeleteError('');
  };

  const handleDeleteAccount = async () => {
    setDeleteError('');
    
    if (!deletePassword) {
      setDeleteError('Digite sua senha para confirmar');
      return;
    }

    setDeleteLoading(true);
    try {
      await contaAPI.deletar(deletePassword);
      alert('Conta deletada com sucesso');
      logout();
      navigate('/login');
    } catch (err) {
      setDeleteError(err.response?.data?.message || 'Erro ao deletar conta. Verifique sua senha.');
    } finally {
      setDeleteLoading(false);
    }
  };

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
      <AppBar position="static">
        <Toolbar>
          <ContactsIcon sx={{ mr: 2 }} />
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Sistema de Contatos
          </Typography>
          <Typography variant="body1" sx={{ mr: 2 }}>
            {user?.nome}
          </Typography>
          <IconButton
            size="large"
            aria-label="account of current user"
            aria-controls="menu-appbar"
            aria-haspopup="true"
            onClick={handleMenu}
            color="inherit"
          >
            <AccountCircle />
          </IconButton>
          <Menu
            id="menu-appbar"
            anchorEl={anchorEl}
            anchorOrigin={{
              vertical: 'top',
              horizontal: 'right',
            }}
            keepMounted
            transformOrigin={{
              vertical: 'top',
              horizontal: 'right',
            }}
            open={Boolean(anchorEl)}
            onClose={handleClose}
          >
            <MenuItem onClick={handleLogout}>Sair</MenuItem>
            <MenuItem onClick={handleOpenDeleteDialog} sx={{ color: 'error.main' }}>
              Deletar Conta
            </MenuItem>
          </Menu>
        </Toolbar>
      </AppBar>
      <Container component="main" sx={{ mt: 4, mb: 4, flexGrow: 1 }}>
        {children}
      </Container>

      {/* Delete Account Confirmation Dialog */}
      <Dialog 
        open={showDeleteDialog} 
        onClose={handleCloseDeleteDialog}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>Confirmar Exclusão de Conta</DialogTitle>
        <DialogContent>
          {deleteError && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {deleteError}
            </Alert>
          )}
          <Typography variant="body1" paragraph>
            Esta ação é irreversível. Todos os seus contatos serão permanentemente excluídos.
          </Typography>
          <Typography variant="body2" color="text.secondary" paragraph>
            Digite sua senha para confirmar a exclusão da sua conta:
          </Typography>
          <TextField
            fullWidth
            type="password"
            label="Senha"
            value={deletePassword}
            onChange={(e) => setDeletePassword(e.target.value)}
            autoFocus
            disabled={deleteLoading}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDeleteDialog} disabled={deleteLoading}>
            Cancelar
          </Button>
          <Button 
            onClick={handleDeleteAccount} 
            color="error" 
            variant="contained"
            disabled={deleteLoading}
          >
            {deleteLoading ? 'Deletando...' : 'Deletar Conta'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
