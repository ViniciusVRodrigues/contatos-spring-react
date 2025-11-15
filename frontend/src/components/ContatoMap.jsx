import { Paper, Typography, Box } from '@mui/material';
import { GoogleMap, LoadScript, Marker } from '@react-google-maps/api';

const mapContainerStyle = {
  width: '100%',
  height: '500px',
};

const defaultCenter = {
  lat: -25.4284,
  lng: -49.2733,
};

export default function ContatoMap({ contato }) {
  if (!contato) {
    return (
      <Paper sx={{ p: 3, height: 500, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <Typography color="text.secondary">
          Selecione um contato para ver no mapa
        </Typography>
      </Paper>
    );
  }

  const center = {
    lat: contato.latitude || defaultCenter.lat,
    lng: contato.longitude || defaultCenter.lng,
  };

  return (
    <Paper>
      <Box sx={{ p: 2, borderBottom: 1, borderColor: 'divider' }}>
        <Typography variant="h6">{contato.nome}</Typography>
        <Typography variant="body2" color="text.secondary">
          {contato.logradouro}, {contato.numero} - {contato.bairro}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          {contato.cidade}/{contato.estado} - CEP: {contato.cep}
        </Typography>
      </Box>
      <LoadScript googleMapsApiKey={import.meta.env.VITE_GOOGLE_MAPS_API_KEY || ''}>
        <GoogleMap
          mapContainerStyle={mapContainerStyle}
          center={center}
          zoom={15}
        >
          <Marker position={center} />
        </GoogleMap>
      </LoadScript>
    </Paper>
  );
}
