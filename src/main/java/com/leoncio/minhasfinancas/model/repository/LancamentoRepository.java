package com.leoncio.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.leoncio.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

}
