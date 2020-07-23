package com.leoncio.minhasfinancas.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.leoncio.minhasfinancas.exception.RegraNegocioException;
import com.leoncio.minhasfinancas.model.entity.Lancamento;
import com.leoncio.minhasfinancas.model.entity.Usuario;
import com.leoncio.minhasfinancas.model.enums.StatusLancamento;
import com.leoncio.minhasfinancas.model.repository.LancamentoRepository;
import com.leoncio.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.leoncio.minhasfinancas.service.impl.LancamentoServiceImpl;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {
	
	@SpyBean
	LancamentoServiceImpl service;
	@MockBean
	LancamentoRepository repository;
	
	@Test
	public void deveSalvarUmLancamento() {
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoASalvar);
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		
		Lancamento lancamento = service.salvar(lancamentoASalvar);
		
		assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);

	}
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeImplementacao() {
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);
		
		catchThrowableOfType(()->service.salvar(lancamentoASalvar), RegraNegocioException.class);
		
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		Mockito.doNothing().when(service).validar(lancamentoSalvo);
		
		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
		
		service.atualizar(lancamentoSalvo);
		
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
	}
	
	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoAindaQueAindaNaoFoiSalvo() {
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		
		catchThrowableOfType(()->service.atualizar(lancamentoASalvar), NullPointerException.class);
		
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}

	@Test
	public void deveDeletarUmLancamento() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		service.deletar(lancamento);
		
		Mockito.verify(repository).delete(lancamento);
	}
	
	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoAindaQueAindaNaoFoiSalvo() {
		Lancamento lancamentoDelete = LancamentoRepositoryTest.criarLancamento();
		
		catchThrowableOfType(()->service.deletar(lancamentoDelete), NullPointerException.class);
		
		Mockito.verify(repository, Mockito.never()).delete(lancamentoDelete);
	}
	
	@Test
	public void deveFiltrarLancamentos() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
		
		List<Lancamento> resultado = service.buscar(lancamento);
		
		assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);
	}
	
	@Test
	public void deveAtualizarStatusDeUmLancamentos() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);
		
		service.atualizarStatus(lancamento, novoStatus);
		
		assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		
		Mockito.verify(service).atualizar(lancamento);
	}
	
	@Test
	public void deveObterUmLancamentosporId() {
		Long id = 1l;
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
	
		Optional<Lancamento> resultado = service.obterPorId(id);
		
		assertThat(resultado.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornarVazioQuandoLancamentosNaoExiste() {
		Long id = 1l;
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
	
		Optional<Lancamento> resultado = service.obterPorId(id);
		
		assertThat(resultado.isPresent()).isFalse();
	}
	
	@Test
	public void deveErrosAoValidarLancamentos() {
		Lancamento lancamento = new Lancamento();
		
		Throwable erro = catchThrowable(()-> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");
		
		lancamento.setDescricao("Salario");
		
		erro = catchThrowable(()-> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês valido.");
		
		lancamento.setMes(5);
		
		erro = catchThrowable(()-> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano valido.");
		
		lancamento.setAno(2020);
		
		erro = catchThrowable(()-> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");
		
		lancamento.setUsuario(new Usuario());
		lancamento.getUsuario().setId(1l);
		
		erro = catchThrowable(()-> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor valido.");
		
		
	}


}
