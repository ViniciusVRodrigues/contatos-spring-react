import { useEffect, useRef, useState } from 'react';
import { Paper, Typography, Box, IconButton, Menu, MenuItem, Divider } from '@mui/material';
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
  const [anchorEl, setAnchorEl] = useState(null);
  const [groupedContatos, setGroupedContatos] = useState([]);

  // Filter contacts with valid coordinates
  const contatosComCoordenadas = contatos?.filter(c => 
    c.latitude != null && 
    c.longitude != null && 
    !isNaN(c.latitude) && 
    !isNaN(c.longitude) &&
    c.latitude !== 0 && 
    c.longitude !== 0
  ) || [];

  // Group contacts by location (same lat/lng)
  const groupContatosByLocation = () => {
    const locationMap = new Map();
    
    contatosComCoordenadas.forEach(contato => {
      const key = `${contato.latitude.toFixed(6)},${contato.longitude.toFixed(6)}`;
      
      if (!locationMap.has(key)) {
        locationMap.set(key, {
          lat: contato.latitude,
          lng: contato.longitude,
          contatos: []
        });
      }
      
      locationMap.get(key).contatos.push(contato);
    });
    
    return Array.from(locationMap.values());
  };

  const locationGroups = groupContatosByLocation();

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
    setAnchorEl(null);
    if (mapInstance && contatosComCoordenadas.length > 0) {
      fitBounds();
    }
  };

  const handleMarkerClick = (group, event) => {
    if (group.contatos.length === 1) {
      // Se há apenas 1 contato, seleciona diretamente
      onContatoSelect(group.contatos[0]);
    } else {
      // Se há múltiplos contatos, abre menu
      setGroupedContatos(group.contatos);
      setAnchorEl(event.domEvent.currentTarget);
    }
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
    setGroupedContatos([]);
  };

  const handleSelectFromMenu = (contato) => {
    onContatoSelect(contato);
    handleMenuClose();
  };

  // Create custom marker icon with count badge
  const createMarkerIcon = (count, isSelected) => {
    const color = isSelected ? '4285F4' : 'EA4335'; // Blue if selected, Red otherwise
    
    if (count === 1) {
      return {
        url: `http://maps.google.com/mapfiles/ms/icons/${isSelected ? 'blue' : 'red'}-dot.png`,
        scaledSize: new window.google.maps.Size(40, 40),
      };
    }
    
    // For multiple contacts, create a marker with a badge
    return {
      url: `data:image/svg+xml;charset=UTF-8,${encodeURIComponent(`
        <svg xmlns="http://www.w3.org/2000/svg" width="50" height="50" viewBox="0 0 50 50">
          <!-- Pin base -->
          <path d="M25 5 C17 5 10 12 10 20 C10 30 25 45 25 45 C25 45 40 30 40 20 C40 12 33 5 25 5 Z" 
                fill="#${color}" stroke="white" stroke-width="2"/>
          <!-- White circle for count -->
          <circle cx="25" cy="20" r="8" fill="white"/>
          <!-- Count text -->
          <text x="25" y="25" font-family="Arial, sans-serif" font-size="12" font-weight="bold" 
                text-anchor="middle" fill="#${color}">${count}</text>
        </svg>
      `)}`,
      scaledSize: new window.google.maps.Size(50, 50),
      anchor: new window.google.maps.Point(25, 45),
    };
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
            {isLoaded && locationGroups.map((group, index) => {
              const isGroupSelected = group.contatos.some(c => c.id === selectedContato?.id);
              
              return (
                <MarkerF
                  key={`group-${index}-${group.lat}-${group.lng}`}
                  position={{
                    lat: group.lat,
                    lng: group.lng,
                  }}
                  onClick={(e) => handleMarkerClick(group, e)}
                  title={group.contatos.length === 1 
                    ? group.contatos[0].nome 
                    : `${group.contatos.length} contatos neste endereço`}
                  icon={createMarkerIcon(group.contatos.length, isGroupSelected)}
                />
              );
            })}
          </GoogleMap>
        </LoadScript>
      </Box>

      {/* Menu for multiple contacts at same location */}
      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleMenuClose}
        PaperProps={{
          sx: { maxHeight: 400, width: 300 }
        }}
      >
        <MenuItem disabled>
          <Typography variant="subtitle2" color="primary">
            {groupedContatos.length} contatos neste endereço:
          </Typography>
        </MenuItem>
        <Divider />
        {groupedContatos.map((contato) => (
          <MenuItem 
            key={contato.id} 
            onClick={() => handleSelectFromMenu(contato)}
            selected={selectedContato?.id === contato.id}
          >
            <Box>
              <Typography variant="body1">{contato.nome}</Typography>
              <Typography variant="caption" color="text.secondary">
                {contato.logradouro}, {contato.numero}
              </Typography>
            </Box>
          </MenuItem>
        ))}
      </Menu>
    </Paper>
  );
}
