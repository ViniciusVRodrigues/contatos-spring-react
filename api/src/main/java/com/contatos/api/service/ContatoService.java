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

/**
 * Service responsible for managing contacts (CRUD operations)
 * 
 * This service implements the core business rules for contacts:
 * - CPF validation using the official Brazilian algorithm
 * - CPF uniqueness per user (same CPF cannot be registered twice by the same user)
 * - Automatic geocoding via Google Maps API when coordinates are not provided
 * - Access control ensuring users can only manage their own contacts
 * - Pagination and search functionality
 * 
 * @see ContatoRepository
 * @see GoogleMapsService
 * @see CpfValidator
 */
@Service
@RequiredArgsConstructor
public class ContatoService {

    private final ContatoRepository contatoRepository;
    private final UsuarioRepository usuarioRepository;
    private final GoogleMapsService googleMapsService;

    /**
     * Retrieves the currently authenticated user from Spring Security context
     * 
     * @return the authenticated Usuario entity
     * @throws BusinessException if user is not found in database
     */
    private Usuario getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));
    }

    /**
     * Lists all contacts for the current authenticated user with optional search and pagination
     * 
     * Business Rules:
     * - Only returns contacts owned by the authenticated user
     * - Search works on both name (case-insensitive) and CPF fields
     * - Supports pagination, sorting via Pageable parameter
     * 
     * @param search optional search term to filter by name or CPF
     * @param pageable pagination and sorting parameters
     * @return paginated list of contacts matching the criteria
     */
    @Transactional(readOnly = true)
    public Page<ContatoResponse> listContatos(String search, Pageable pageable) {
        Usuario usuario = getCurrentUser();
        Page<Contato> contatos;

        if (search != null && !search.isBlank()) {
            // Search in both nome and CPF fields
            contatos = contatoRepository.findByUsuarioIdAndNomeContainingIgnoreCaseOrUsuarioIdAndCpfContaining(
                    usuario.getId(), search, usuario.getId(), search, pageable);
        } else {
            contatos = contatoRepository.findByUsuarioId(usuario.getId(), pageable);
        }

        return contatos.map(this::toResponse);
    }

    /**
     * Retrieves a specific contact by ID
     * 
     * Business Rules:
     * - User can only access their own contacts (ownership validation)
     * - Throws exception if contact doesn't exist or belongs to another user
     * 
     * @param id the contact ID
     * @return the contact data
     * @throws ResourceNotFoundException if contact doesn't exist
     * @throws BusinessException if user doesn't own the contact
     */
    @Transactional(readOnly = true)
    public ContatoResponse getContato(Long id) {
        Usuario usuario = getCurrentUser();
        Contato contato = contatoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contato não encontrado"));

        // Access control: verify ownership
        if (!contato.getUsuario().getId().equals(usuario.getId())) {
            throw new BusinessException("Acesso negado");
        }

        return toResponse(contato);
    }

    /**
     * Creates a new contact for the authenticated user
     * 
     * Business Rules:
     * - CPF must be valid according to Brazilian algorithm (CpfValidator)
     * - CPF must be unique per user (duplicate check)
     * - If latitude/longitude not provided or zero, automatically fetches from Google Maps API
     * - All address fields are required for geocoding
     * 
     * @param request contact creation data
     * @return created contact with generated ID and coordinates
     * @throws BusinessException if CPF is invalid, already registered, or geocoding fails
     */
    @Transactional
    public ContatoResponse createContato(ContatoRequest request) {
        Usuario usuario = getCurrentUser();

        // Validate CPF using official Brazilian algorithm
        if (!CpfValidator.isValid(request.getCpf())) {
            throw new BusinessException("CPF inválido");
        }

        // Check CPF uniqueness per user
        if (contatoRepository.existsByUsuarioIdAndCpf(usuario.getId(), request.getCpf())) {
            throw new BusinessException("CPF já cadastrado");
        }

        // Automatic geocoding: fetch coordinates from Google Maps if not provided
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

    /**
     * Updates an existing contact
     * 
     * Business Rules:
     * - User can only update their own contacts
     * - CPF must remain valid
     * - CPF must remain unique per user (excluding the contact being updated)
     * - If address fields changed, automatically recalculates coordinates via Google Maps
     * 
     * @param id contact ID to update
     * @param request new contact data
     * @return updated contact
     * @throws ResourceNotFoundException if contact doesn't exist
     * @throws BusinessException if validation fails or user doesn't own the contact
     */
    @Transactional
    public ContatoResponse updateContato(Long id, ContatoRequest request) {
        Usuario usuario = getCurrentUser();
        Contato contato = contatoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contato não encontrado"));

        // Access control: verify ownership
        if (!contato.getUsuario().getId().equals(usuario.getId())) {
            throw new BusinessException("Acesso negado");
        }

        // Validate CPF
        if (!CpfValidator.isValid(request.getCpf())) {
            throw new BusinessException("CPF inválido");
        }

        // Check CPF uniqueness (excluding current contact)
        if (contatoRepository.existsByUsuarioIdAndCpfAndIdNot(usuario.getId(), request.getCpf(), id)) {
            throw new BusinessException("CPF já cadastrado");
        }

        // Check if address changed to recalculate coordinates
        boolean enderecoMudou = !contato.getLogradouro().equals(request.getLogradouro()) ||
                                !contato.getNumero().equals(request.getNumero()) ||
                                !contato.getBairro().equals(request.getBairro()) ||
                                !contato.getCidade().equals(request.getCidade()) ||
                                !contato.getEstado().equals(request.getEstado()) ||
                                !contato.getCep().equals(request.getCep());

        // Recalculate coordinates if address changed or coordinates not provided
        Double latitude = request.getLatitude();
        Double longitude = request.getLongitude();
        
        // Fetch new coordinates if: address changed OR coordinates not provided/zero
        boolean needsCoordinates = (latitude == null || latitude == 0.0) || (longitude == null || longitude == 0.0);
        
        if (enderecoMudou || needsCoordinates) {
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

    /**
     * Deletes a contact permanently
     * 
     * Business Rules:
     * - User can only delete their own contacts
     * - Deletion is permanent (no soft delete)
     * 
     * @param id contact ID to delete
     * @throws ResourceNotFoundException if contact doesn't exist
     * @throws BusinessException if user doesn't own the contact
     */
    @Transactional
    public void deleteContato(Long id) {
        Usuario usuario = getCurrentUser();
        Contato contato = contatoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contato não encontrado"));

        // Access control: verify ownership
        if (!contato.getUsuario().getId().equals(usuario.getId())) {
            throw new BusinessException("Acesso negado");
        }

        contatoRepository.delete(contato);
    }

    /**
     * Converts a Contato entity to a ContatoResponse DTO
     * Utility method to separate domain model from API response
     * 
     * @param contato the entity to convert
     * @return DTO with contact data
     */
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

    /**
     * Checks if a CPF is already registered for the current user
     * 
     * Business Rules:
     * - Validates CPF format using official algorithm
     * - Checks uniqueness within the authenticated user's contacts
     * - Returns false if CPF is invalid or not registered
     * 
     * @param cpf the CPF to check (numbers only)
     * @return true if CPF exists and is valid, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean cpfExists(String cpf) {
        // Validate CPF format first
        if (!CpfValidator.isValid(cpf)) {
            return false;
        }
        
        Usuario usuario = getCurrentUser();
        return contatoRepository.existsByUsuarioIdAndCpf(usuario.getId(), cpf);
    }
}
