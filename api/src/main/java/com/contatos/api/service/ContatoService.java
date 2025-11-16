package com.contatos.api.service;

import com.contatos.api.dto.ContatoRequest;
import com.contatos.api.dto.ContatoResponse;
import com.contatos.api.dto.GoogleGeocodingResponse;
import com.contatos.api.exception.BusinessException;
import com.contatos.api.exception.ResourceNotFoundException;
import com.contatos.api.model.Contato;
import com.contatos.api.model.Usuario;
import com.contatos.api.repository.ContatoRepository;
import com.contatos.api.repository.UsuarioRepository;
import com.contatos.api.util.CpfValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContatoService {

    private final ContatoRepository contatoRepository;
    private final UsuarioRepository usuarioRepository;
    private final GoogleMapsService googleMapsService;

    private Usuario getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));
    }

    @Transactional(readOnly = true)
    public Page<ContatoResponse> listContatos(String search, Pageable pageable) {
        Usuario usuario = getCurrentUser();
        Page<Contato> contatos;

        if (search != null && !search.isBlank()) {
            contatos = contatoRepository.findByUsuarioIdAndNomeContainingIgnoreCaseOrUsuarioIdAndCpfContaining(
                    usuario.getId(), search, usuario.getId(), search, pageable);
        } else {
            contatos = contatoRepository.findByUsuarioId(usuario.getId(), pageable);
        }

        return contatos.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ContatoResponse getContato(Long id) {
        Usuario usuario = getCurrentUser();
        Contato contato = contatoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contato não encontrado"));

        if (!contato.getUsuario().getId().equals(usuario.getId())) {
            throw new BusinessException("Acesso negado");
        }

        return toResponse(contato);
    }

    @Transactional
    public ContatoResponse createContato(ContatoRequest request) {
        Usuario usuario = getCurrentUser();

        if (!CpfValidator.isValid(request.getCpf())) {
            throw new BusinessException("CPF inválido");
        }

        if (contatoRepository.existsByUsuarioIdAndCpf(usuario.getId(), request.getCpf())) {
            throw new BusinessException("CPF já cadastrado");
        }

        // Se latitude/longitude não foram fornecidas ou são zero, busca do Google Maps
        Double latitude = request.getLatitude();
        Double longitude = request.getLongitude();
        
        if ((latitude == null || latitude == 0.0) || (longitude == null || longitude == 0.0)) {
            try {
                var location = googleMapsService.getCoordinates(
                    request.getLogradouro(), 
                    request.getNumero(),
                    request.getBairro(), 
                    request.getCidade(), 
                    request.getEstado(), 
                    request.getCep()
                );
                latitude = location.getLat();
                longitude = location.getLng();
            } catch (Exception e) {
                throw new BusinessException("Não foi possível obter coordenadas para o endereço fornecido. Configure a chave da API do Google Maps ou forneça as coordenadas manualmente.");
            }
        }

        Contato contato = Contato.builder()
                .nome(request.getNome())
                .cpf(request.getCpf())
                .telefone(request.getTelefone())
                .cep(request.getCep())
                .logradouro(request.getLogradouro())
                .numero(request.getNumero())
                .complemento(request.getComplemento())
                .bairro(request.getBairro())
                .cidade(request.getCidade())
                .estado(request.getEstado())
                .latitude(latitude)
                .longitude(longitude)
                .usuario(usuario)
                .build();

        contato = contatoRepository.save(contato);
        return toResponse(contato);
    }

    @Transactional
    public ContatoResponse updateContato(Long id, ContatoRequest request) {
        Usuario usuario = getCurrentUser();
        Contato contato = contatoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contato não encontrado"));

        if (!contato.getUsuario().getId().equals(usuario.getId())) {
            throw new BusinessException("Acesso negado");
        }

        if (!CpfValidator.isValid(request.getCpf())) {
            throw new BusinessException("CPF inválido");
        }

        if (contatoRepository.existsByUsuarioIdAndCpfAndIdNot(usuario.getId(), request.getCpf(), id)) {
            throw new BusinessException("CPF já cadastrado");
        }

        // Verifica se o endereço mudou para recalcular as coordenadas
        boolean enderecoMudou = !contato.getLogradouro().equals(request.getLogradouro()) ||
                                !contato.getNumero().equals(request.getNumero()) ||
                                !contato.getBairro().equals(request.getBairro()) ||
                                !contato.getCidade().equals(request.getCidade()) ||
                                !contato.getEstado().equals(request.getEstado()) ||
                                !contato.getCep().equals(request.getCep());

        // Se latitude/longitude não foram fornecidas, são zero, ou o endereço mudou, busca do Google Maps
        Double latitude = request.getLatitude();
        Double longitude = request.getLongitude();
        
        if ((latitude == null || latitude == 0.0) || (longitude == null || longitude == 0.0) || enderecoMudou) {
            try {
                var location = googleMapsService.getCoordinates(
                    request.getLogradouro(), 
                    request.getNumero(),
                    request.getBairro(), 
                    request.getCidade(), 
                    request.getEstado(), 
                    request.getCep()
                );
                latitude = location.getLat();
                longitude = location.getLng();
            } catch (Exception e) {
                throw new BusinessException("Não foi possível obter coordenadas para o endereço fornecido. Configure a chave da API do Google Maps ou forneça as coordenadas manualmente.");
            }
        }

        contato.setNome(request.getNome());
        contato.setCpf(request.getCpf());
        contato.setTelefone(request.getTelefone());
        contato.setCep(request.getCep());
        contato.setLogradouro(request.getLogradouro());
        contato.setNumero(request.getNumero());
        contato.setComplemento(request.getComplemento());
        contato.setBairro(request.getBairro());
        contato.setCidade(request.getCidade());
        contato.setEstado(request.getEstado());
        contato.setLatitude(latitude);
        contato.setLongitude(longitude);

        contato = contatoRepository.save(contato);
        return toResponse(contato);
    }

    @Transactional
    public void deleteContato(Long id) {
        Usuario usuario = getCurrentUser();
        Contato contato = contatoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contato não encontrado"));

        if (!contato.getUsuario().getId().equals(usuario.getId())) {
            throw new BusinessException("Acesso negado");
        }

        contatoRepository.delete(contato);
    }

    private ContatoResponse toResponse(Contato contato) {
        return ContatoResponse.builder()
                .id(contato.getId())
                .nome(contato.getNome())
                .cpf(contato.getCpf())
                .telefone(contato.getTelefone())
                .cep(contato.getCep())
                .logradouro(contato.getLogradouro())
                .numero(contato.getNumero())
                .complemento(contato.getComplemento())
                .bairro(contato.getBairro())
                .cidade(contato.getCidade())
                .estado(contato.getEstado())
                .latitude(contato.getLatitude())
                .longitude(contato.getLongitude())
                .createdAt(contato.getCreatedAt())
                .updatedAt(contato.getUpdatedAt())
                .build();
    }
}
