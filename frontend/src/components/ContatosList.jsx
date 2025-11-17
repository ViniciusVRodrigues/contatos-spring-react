import {
  List,
  ListItem,
  ListItemButton,
  ListItemText,
  ListItemSecondaryAction,
  IconButton,
  Paper,
  Typography,
  Box,
  CircularProgress,
  Pagination,
  Divider,
} from '@mui/material';
import {
  Edit as EditIcon,
  Delete as DeleteIcon,
  Phone as PhoneIcon,
} from '@mui/icons-material';
import { formatarCPF, formatarTelefone } from '../utils/validators';

export default function ContatosList({
  contatos,
  loading,
  onEdit,
  onDelete,
  onContatoClick,
  selectedContato,
  page,
  totalPages,
  onPageChange,
}) {
  if (loading) {
    return (
      <Paper sx={{ p: 3, textAlign: 'center' }}>
        <CircularProgress />
      </Paper>
    );
  }

  if (contatos.length === 0) {
    return (
      <Paper sx={{ p: 3, textAlign: 'center' }}>
        <Typography color="text.secondary">
          Nenhum contato encontrado. Adicione um novo contato!
        </Typography>
      </Paper>
    );
  }

  return (
    <Paper>
      <List>
        {contatos.map((contato, index) => (
          <div key={contato.id}>
            {index > 0 && <Divider />}
            <ListItem
              disablePadding
              sx={{
                bgcolor: selectedContato?.id === contato.id ? 'action.selected' : 'transparent',
              }}
            >
              <ListItemButton onClick={() => onContatoClick(contato)}>
                <ListItemText
                  primary={contato.nome}
                  secondary={
                    <Box component="span" sx={{ display: 'block' }}>
                      <Box component="span" sx={{ display: 'block' }}>
                        CPF: {formatarCPF(contato.cpf)}
                      </Box>
                      <Box component="span" sx={{ display: 'block' }}>
                        <PhoneIcon sx={{ fontSize: 14, mr: 0.5, verticalAlign: 'middle' }} />
                        {formatarTelefone(contato.telefone)}
                      </Box>
                      <Box component="span" sx={{ display: 'block' }}>
                        {contato.cidade}/{contato.estado}
                      </Box>
                    </Box>
                  }
                />
                <ListItemSecondaryAction>
                  <IconButton
                    edge="end"
                    aria-label="edit"
                    onClick={(e) => {
                      e.stopPropagation();
                      onEdit(contato);
                    }}
                    sx={{ mr: 1 }}
                  >
                    <EditIcon />
                  </IconButton>
                  <IconButton
                    edge="end"
                    aria-label="delete"
                    onClick={(e) => {
                      e.stopPropagation();
                      onDelete(contato.id);
                    }}
                  >
                    <DeleteIcon />
                  </IconButton>
                </ListItemSecondaryAction>
              </ListItemButton>
            </ListItem>
          </div>
        ))}
      </List>
      {totalPages > 1 && (
        <Box sx={{ p: 2, display: 'flex', justifyContent: 'center' }}>
          <Pagination
            count={totalPages}
            page={page + 1}
            onChange={(e, value) => onPageChange(value - 1)}
            color="primary"
          />
        </Box>
      )}
    </Paper>
  );
}
