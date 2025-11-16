import { useEffect, useRef, useState } from 'react';
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

export default function ContatoMap({ contatos, selectedContato, onContatoSelect }) {
  const mapRef = useRef(null);
  const [mapInstance, setMapInstance] = useState(null);

  // Zoom to selected contact when it changes
  useEffect(() => {
    if (mapInstance && selectedContato) {
      const center = {
        lat: selectedContato.latitude || defaultCenter.lat,
        lng: selectedContato.longitude || defaultCenter.lng,
      };
      mapInstance.panTo(center);
      mapInstance.setZoom(16);
    }
  }, [selectedContato, mapInstance]);

  const onLoad = (map) => {
    setMapInstance(map);
    mapRef.current = map;
  };

  // Calculate center based on all contacts or default
  const getCenter = () => {
    if (selectedContato) {
      return {
        lat: selectedContato.latitude || defaultCenter.lat,
        lng: selectedContato.longitude || defaultCenter.lng,
      };
    }
    if (contatos && contatos.length > 0) {
      // Calculate average position of all contacts
      const avgLat = contatos.reduce((sum, c) => sum + (c.latitude || 0), 0) / contatos.length;
      const avgLng = contatos.reduce((sum, c) => sum + (c.longitude || 0), 0) / contatos.length;
      return { lat: avgLat, lng: avgLng };
    }
    return defaultCenter;
  };

  const getZoom = () => {
    if (selectedContato) return 16;
    if (contatos && contatos.length > 0) return 12;
    return 10;
  };

  if (!contatos || contatos.length === 0) {
    return (
      <Paper sx={{ p: 3, height: 500, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <Typography color="text.secondary">
          Nenhum contato para exibir no mapa
        </Typography>
      </Paper>
    );
  }

  return (
    <Paper>
      {selectedContato && (
        <Box sx={{ p: 2, borderBottom: 1, borderColor: 'divider' }}>
          <Typography variant="h6">{selectedContato.nome}</Typography>
          <Typography variant="body2" color="text.secondary">
            {selectedContato.logradouro}, {selectedContato.numero} - {selectedContato.bairro}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            {selectedContato.cidade}/{selectedContato.estado} - CEP: {selectedContato.cep}
          </Typography>
        </Box>
      )}
      <LoadScript googleMapsApiKey={import.meta.env.VITE_GOOGLE_MAPS_API_KEY || ''}>
        <GoogleMap
          mapContainerStyle={mapContainerStyle}
          center={getCenter()}
          zoom={getZoom()}
          onLoad={onLoad}
        >
          {contatos.map((contato) => (
            <Marker
              key={contato.id}
              position={{
                lat: contato.latitude || defaultCenter.lat,
                lng: contato.longitude || defaultCenter.lng,
              }}
              onClick={() => onContatoSelect(contato)}
              title={contato.nome}
              icon={
                selectedContato?.id === contato.id
                  ? {
                      url: 'http://maps.google.com/mapfiles/ms/icons/blue-dot.png',
                    }
                  : undefined
              }
            />
          ))}
        </GoogleMap>
      </LoadScript>
    </Paper>
  );
}
