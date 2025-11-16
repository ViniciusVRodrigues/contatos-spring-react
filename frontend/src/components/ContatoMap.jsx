import { useEffect, useRef, useState } from 'react';
import { Paper, Typography, Box, IconButton } from '@mui/material';
import { GoogleMap, LoadScript, MarkerF } from '@react-google-maps/api';
import { ArrowBack as ArrowBackIcon } from '@mui/icons-material';

const mapContainerStyle = {
  width: '100%',
  height: '100%',
  minHeight: '600px',
};

const defaultCenter = {
  lat: -25.4284,
  lng: -49.2733,
};

export default function ContatoMap({ contatos, selectedContato, onContatoSelect }) {
  const mapRef = useRef(null);
  const [mapInstance, setMapInstance] = useState(null);
  const [isLoaded, setIsLoaded] = useState(false);

  // Filter contacts with valid coordinates
  const contatosComCoordenadas = contatos?.filter(c => 
    c.latitude != null && 
    c.longitude != null && 
    !isNaN(c.latitude) && 
    !isNaN(c.longitude) &&
    c.latitude !== 0 && 
    c.longitude !== 0
  ) || [];

  // Calculate bounds to fit all markers
  const fitBounds = () => {
    if (!mapInstance || contatosComCoordenadas.length === 0) return;

    const bounds = new window.google.maps.LatLngBounds();
    contatosComCoordenadas.forEach(contato => {
      bounds.extend({
        lat: contato.latitude,
        lng: contato.longitude,
      });
    });

    mapInstance.fitBounds(bounds, {
      padding: { top: 50, right: 50, bottom: 50, left: 50 }
    });
  };

  // Fit bounds when contacts change or map loads
  useEffect(() => {
    if (mapInstance && contatosComCoordenadas.length > 0 && !selectedContato) {
      fitBounds();
    }
  }, [mapInstance, contatos, selectedContato]);

  // Zoom to selected contact when it changes
  useEffect(() => {
    if (mapInstance && selectedContato) {
      const hasValidCoordinates = 
        selectedContato.latitude != null && 
        selectedContato.longitude != null &&
        !isNaN(selectedContato.latitude) &&
        !isNaN(selectedContato.longitude) &&
        selectedContato.latitude !== 0 &&
        selectedContato.longitude !== 0;
      
      if (hasValidCoordinates) {
        const center = {
          lat: selectedContato.latitude,
          lng: selectedContato.longitude,
        };
        mapInstance.panTo(center);
        mapInstance.setZoom(16);
      }
    }
  }, [selectedContato, mapInstance]);

  const onLoad = (map) => {
    setMapInstance(map);
    mapRef.current = map;
    setIsLoaded(true);
  };

  const handleBackToAll = () => {
    onContatoSelect(null);
    if (mapInstance && contatosComCoordenadas.length > 0) {
      fitBounds();
    }
  };

  // Calculate center based on all contacts or default
  const getCenter = () => {
    if (selectedContato && 
        selectedContato.latitude != null && 
        selectedContato.longitude != null &&
        selectedContato.latitude !== 0 &&
        selectedContato.longitude !== 0) {
      return {
        lat: selectedContato.latitude,
        lng: selectedContato.longitude,
      };
    }
    if (contatosComCoordenadas.length > 0) {
      const avgLat = contatosComCoordenadas.reduce((sum, c) => sum + c.latitude, 0) / contatosComCoordenadas.length;
      const avgLng = contatosComCoordenadas.reduce((sum, c) => sum + c.longitude, 0) / contatosComCoordenadas.length;
      return { lat: avgLat, lng: avgLng };
    }
    return defaultCenter;
  };

  const getZoom = () => {
    if (selectedContato) return 16;
    if (contatosComCoordenadas.length > 0) return 12;
    return 10;
  };

  if (!contatos || contatos.length === 0) {
    return (
      <Paper sx={{ p: 3, height: '100%', minHeight: 600, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <Typography color="text.secondary">
          Nenhum contato para exibir no mapa
        </Typography>
      </Paper>
    );
  }

  return (
    <Paper sx={{ height: '100%', minHeight: 600, display: 'flex', flexDirection: 'column' }}>
      {selectedContato && (
        <Box sx={{ p: 2, borderBottom: 1, borderColor: 'divider', display: 'flex', alignItems: 'center', gap: 1 }}>
          <IconButton 
            onClick={handleBackToAll}
            size="small"
            sx={{ 
              bgcolor: 'primary.main', 
              color: 'white',
              '&:hover': { bgcolor: 'primary.dark' }
            }}
          >
            <ArrowBackIcon />
          </IconButton>
          <Box sx={{ flexGrow: 1 }}>
            <Typography variant="h6">{selectedContato.nome}</Typography>
            <Typography variant="body2" color="text.secondary">
              {selectedContato.logradouro}, {selectedContato.numero} - {selectedContato.bairro}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {selectedContato.cidade}/{selectedContato.estado} - CEP: {selectedContato.cep}
            </Typography>
          </Box>
        </Box>
      )}
      <Box sx={{ flexGrow: 1, position: 'relative' }}>
        <LoadScript 
          googleMapsApiKey={import.meta.env.VITE_GOOGLE_MAPS_API_KEY || ''}
          loadingElement={<Box sx={{ p: 3, textAlign: 'center' }}>Carregando mapa...</Box>}
        >
          <GoogleMap
            mapContainerStyle={mapContainerStyle}
            center={getCenter()}
            zoom={getZoom()}
            onLoad={onLoad}
            options={{
              zoomControl: true,
              mapTypeControl: false,
              scaleControl: true,
              streetViewControl: false,
              rotateControl: false,
              fullscreenControl: true,
            }}
          >
            {isLoaded && contatosComCoordenadas.map((contato) => (
              <MarkerF
                key={contato.id}
                position={{
                  lat: contato.latitude,
                  lng: contato.longitude,
                }}
                onClick={() => onContatoSelect(contato)}
                title={contato.nome}
                icon={{
                  url: selectedContato?.id === contato.id
                    ? 'http://maps.google.com/mapfiles/ms/icons/blue-dot.png'
                    : 'http://maps.google.com/mapfiles/ms/icons/red-dot.png',
                  scaledSize: new window.google.maps.Size(40, 40),
                }}
              />
            ))}
          </GoogleMap>
        </LoadScript>
      </Box>
    </Paper>
  );
}
