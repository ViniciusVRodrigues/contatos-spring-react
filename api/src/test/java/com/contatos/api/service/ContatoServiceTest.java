package com.contatos.api.service;

import com.contatos.api.config.TestConfig;
import com.contatos.api.dto.ContatoRequest;
import com.contatos.api.dto.ContatoResponse;
import com.contatos.api.dto.UsuarioRegistroRequest;
import com.contatos.api.exception.BusinessException;
import com.contatos.api.exception.ResourceNotFoundException;
import com.contatos.api.model.Contato;
import com.contatos.api.repository.ContatoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ContatoService
 * Tests cover main business rules: CPF validation, duplicate detection, CRUD operations, and pagination
 */
@SpringBootTest
@Transactional
@Import(TestConfig.class)
class ContatoServiceTest {

    @Autowired
    private ContatoService contatoService;

    @Autowired
    private AuthService authService;

    @Autowired
    private ContatoRepository contatoRepository;

    private String userEmail = "test-contato@example.com";

    @BeforeEach
    void setUp() {
        // Register and authenticate a test user before each test
        UsuarioRegistroRequest registerRequest = UsuarioRegistroRequest.builder()
                .nome("Contato Test User")
                .email(userEmail)
                .senha("password123")
                .build();

        authService.register(registerRequest);

        // Authenticate the user for the test
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userEmail, null)
        );
    }

    /**
     * Test: Should create a new contact successfully with valid data
     * Business Rule: CPF must be valid and unique per user
     */
    @Test
    void shouldCreateContatoSuccessfully() {
        ContatoRequest request = ContatoRequest.builder()
                .nome("João Silva")
                .cpf("12345678909")
                .telefone("41999887766")
                .cep("80010000")
                .logradouro("Rua José Loureiro")
                .numero("123")
                .bairro("Centro")
                .cidade("Curitiba")
                .estado("PR")
                .latitude(-25.4284)
                .longitude(-49.2733)
                .build();

        ContatoResponse response = contatoService.createContato(request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("João Silva", response.getNome());
        assertEquals("12345678909", response.getCpf());
        assertEquals("41999887766", response.getTelefone());
        assertEquals("Curitiba", response.getCidade());
    }

    /**
     * Test: Should reject contact creation with invalid CPF
     * Business Rule: CPF must pass official Brazilian validation algorithm
     */
    @Test
    void shouldRejectInvalidCpf() {
        ContatoRequest request = ContatoRequest.builder()
                .nome("Maria Santos")
                .cpf("12345678901") // Invalid CPF
                .telefone("41999887766")
                .cep("80010000")
                .logradouro("Rua José Loureiro")
                .numero("123")
                .bairro("Centro")
                .cidade("Curitiba")
                .estado("PR")
                .latitude(-25.4284)
                .longitude(-49.2733)
                .build();

        assertThrows(BusinessException.class, () -> contatoService.createContato(request));
    }

    /**
     * Test: Should reject duplicate CPF for the same user
     * Business Rule: CPF must be unique within each user's contact list
     */
    @Test
    void shouldRejectDuplicateCpfForSameUser() {
        ContatoRequest request1 = ContatoRequest.builder()
                .nome("Pedro Oliveira")
                .cpf("12345678909")
                .telefone("41999887766")
                .cep("80010000")
                .logradouro("Rua José Loureiro")
                .numero("123")
                .bairro("Centro")
                .cidade("Curitiba")
                .estado("PR")
                .latitude(-25.4284)
                .longitude(-49.2733)
                .build();

        contatoService.createContato(request1);

        ContatoRequest request2 = ContatoRequest.builder()
                .nome("Ana Costa")
                .cpf("12345678909") // Same CPF
                .telefone("41988776655")
                .cep("80020000")
                .logradouro("Rua Voluntários da Pátria")
                .numero("456")
                .bairro("Centro")
                .cidade("Curitiba")
                .estado("PR")
                .latitude(-25.4284)
                .longitude(-49.2733)
                .build();

        BusinessException exception = assertThrows(BusinessException.class,
                () -> contatoService.createContato(request2));
        assertEquals("CPF já cadastrado", exception.getMessage());
    }

    /**
     * Test: Should list contacts with pagination
     * Business Rule: Contacts should be paginated and only show current user's contacts
     */
    @Test
    void shouldListContatosWithPagination() {
        // Create multiple contacts
        for (int i = 1; i <= 3; i++) {
            ContatoRequest request = ContatoRequest.builder()
                    .nome("Contato " + i)
                    .cpf(generateValidCpf(i))
                    .telefone("4199988776" + i)
                    .cep("80010000")
                    .logradouro("Rua José Loureiro")
                    .numero(String.valueOf(i))
                    .bairro("Centro")
                    .cidade("Curitiba")
                    .estado("PR")
                    .latitude(-25.4284)
                    .longitude(-49.2733)
                    .build();
            contatoService.createContato(request);
        }

        Pageable pageable = PageRequest.of(0, 2);
        Page<ContatoResponse> page = contatoService.listContatos(null, pageable);

        assertNotNull(page);
        assertEquals(3, page.getTotalElements());
        assertEquals(2, page.getSize());
        assertTrue(page.getContent().size() <= 2);
    }

    /**
     * Test: Should filter contacts by search term (name or CPF)
     * Business Rule: Search should work for both name and CPF fields
     */
    @Test
    void shouldFilterContatosBySearch() {
        ContatoRequest request1 = ContatoRequest.builder()
                .nome("Carlos Eduardo")
                .cpf("12345678909")
                .telefone("41999887766")
                .cep("80010000")
                .logradouro("Rua José Loureiro")
                .numero("100")
                .bairro("Centro")
                .cidade("Curitiba")
                .estado("PR")
                .latitude(-25.4284)
                .longitude(-49.2733)
                .build();

        ContatoRequest request2 = ContatoRequest.builder()
                .nome("Fernanda Lima")
                .cpf("11144477735")
                .telefone("41988776655")
                .cep("80020000")
                .logradouro("Rua Voluntários da Pátria")
                .numero("200")
                .bairro("Centro")
                .cidade("Curitiba")
                .estado("PR")
                .latitude(-25.4284)
                .longitude(-49.2733)
                .build();

        contatoService.createContato(request1);
        contatoService.createContato(request2);

        Pageable pageable = PageRequest.of(0, 10);

        // Search by name
        Page<ContatoResponse> pageByName = contatoService.listContatos("Carlos", pageable);
        assertEquals(1, pageByName.getTotalElements());
        assertEquals("Carlos Eduardo", pageByName.getContent().get(0).getNome());

        // Search by CPF
        Page<ContatoResponse> pageByCpf = contatoService.listContatos("11144477735", pageable);
        assertEquals(1, pageByCpf.getTotalElements());
        assertEquals("Fernanda Lima", pageByCpf.getContent().get(0).getNome());
    }

    /**
     * Test: Should retrieve a specific contact by ID
     * Business Rule: User can only access their own contacts
     */
    @Test
    void shouldGetContatoById() {
        ContatoRequest request = ContatoRequest.builder()
                .nome("Lucas Martins")
                .cpf("12345678909")
                .telefone("41999887766")
                .cep("80010000")
                .logradouro("Rua José Loureiro")
                .numero("789")
                .bairro("Centro")
                .cidade("Curitiba")
                .estado("PR")
                .latitude(-25.4284)
                .longitude(-49.2733)
                .build();

        ContatoResponse created = contatoService.createContato(request);
        ContatoResponse found = contatoService.getContato(created.getId());

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals("Lucas Martins", found.getNome());
    }

    /**
     * Test: Should throw exception when contact not found
     */
    @Test
    void shouldThrowExceptionWhenContatoNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> contatoService.getContato(99999L));
    }

    /**
     * Test: Should update contact successfully
     * Business Rule: Can update contact data including CPF if it remains unique
     */
    @Test
    void shouldUpdateContatoSuccessfully() {
        ContatoRequest createRequest = ContatoRequest.builder()
                .nome("Original Name")
                .cpf("12345678909")
                .telefone("41999887766")
                .cep("80010000")
                .logradouro("Rua José Loureiro")
                .numero("100")
                .bairro("Centro")
                .cidade("Curitiba")
                .estado("PR")
                .latitude(-25.4284)
                .longitude(-49.2733)
                .build();

        ContatoResponse created = contatoService.createContato(createRequest);

        ContatoRequest updateRequest = ContatoRequest.builder()
                .nome("Updated Name")
                .cpf("12345678909") // Same CPF
                .telefone("41988776655") // Different phone
                .cep("80020000")
                .logradouro("Rua Voluntários da Pátria")
                .numero("200")
                .bairro("Centro")
                .cidade("Curitiba")
                .estado("PR")
                .latitude(-25.4300)
                .longitude(-49.2800)
                .build();

        ContatoResponse updated = contatoService.updateContato(created.getId(), updateRequest);

        assertNotNull(updated);
        assertEquals(created.getId(), updated.getId());
        assertEquals("Updated Name", updated.getNome());
        assertEquals("41988776655", updated.getTelefone());
        assertEquals("Rua Voluntários da Pátria", updated.getLogradouro());
    }

    /**
     * Test: Should delete contact successfully
     * Business Rule: User can delete their own contacts
     */
    @Test
    void shouldDeleteContatoSuccessfully() {
        ContatoRequest request = ContatoRequest.builder()
                .nome("To Delete")
                .cpf("12345678909")
                .telefone("41999887766")
                .cep("80010000")
                .logradouro("Rua José Loureiro")
                .numero("999")
                .bairro("Centro")
                .cidade("Curitiba")
                .estado("PR")
                .latitude(-25.4284)
                .longitude(-49.2733)
                .build();

        ContatoResponse created = contatoService.createContato(request);
        Long contatoId = created.getId();

        contatoService.deleteContato(contatoId);

        assertThrows(ResourceNotFoundException.class,
                () -> contatoService.getContato(contatoId));
    }

    /**
     * Helper method to generate valid CPF for testing
     * Uses pre-calculated valid CPFs that pass the official validation algorithm
     */
    private String generateValidCpf(int seed) {
        // Pre-calculated VALID CPFs for testing (verified with official algorithm)
        // Each test needs unique CPFs to avoid duplicate detection
        switch (seed) {
            case 1: return "11144477735";  // Valid CPF
            case 2: return "52998224725";  // Valid CPF  
            case 3: return "84434895028";  // Valid CPF
            default: return "11144477735";
        }
    }
}
