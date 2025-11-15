package com.contatos.api.repository;

import com.contatos.api.model.Contato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContatoRepository extends JpaRepository<Contato, Long> {

    Page<Contato> findByUsuarioId(Long usuarioId, Pageable pageable);

    Page<Contato> findByUsuarioIdAndNomeContainingIgnoreCaseOrUsuarioIdAndCpfContaining(
        Long usuarioId1, String nome, Long usuarioId2, String cpf, Pageable pageable);

    boolean existsByUsuarioIdAndCpf(Long usuarioId, String cpf);

    boolean existsByUsuarioIdAndCpfAndIdNot(Long usuarioId, String cpf, Long id);
}
