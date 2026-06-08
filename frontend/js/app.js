/**
 * app.js — lógica principal da aplicação
 */

// Usuário logado
let usuarioAtual = null;
// Lista de chamados em memória (para filtro local sem re-fetch)
let chamadosCache = [];
// ID do chamado aberto no modal
let chamadoModalId = null;

// ═══════════════════════════════════════════════════════════════════════════
// LOGIN
// ═══════════════════════════════════════════════════════════════════════════

document.getElementById('btn-entrar').addEventListener('click', fazerLogin);
document.getElementById('inp-senha').addEventListener('keydown', e => {
  if (e.key === 'Enter') fazerLogin();
});

function fazerLogin() {
  const login = document.getElementById('inp-login').value.trim();
  const senha = document.getElementById('inp-senha').value.trim();

  if (!login || !senha) {
    UI.mostrarErro('Campos obrigatórios', 'Preencha o login e a senha.');
    return;
  }

  Api.login(login, senha)
    .then(usuario => {
      usuarioAtual = usuario;

      if (usuario.perfil === 'ADMINISTRADOR') {
        document.getElementById('admin-nome-header').textContent = usuario.nome;
        UI.mostrarTela('tela-admin');
        carregarChamadosAdmin();
      } else {
        document.getElementById('sol-nome-header').textContent = usuario.nome;
        document.getElementById('sol-solicitante').value = usuario.nome;
        UI.mostrarTela('tela-solicitante');
        carregarAplicativos();
        carregarChamadosSolicitante();
      }
    })
    .catch(msg => UI.mostrarErro('Ops! Algo deu errado', msg || 'Login ou senha inválidos.'));
}

function deslogar() {
  usuarioAtual = null;
  chamadosCache = [];
  document.getElementById('inp-login').value = '';
  document.getElementById('inp-senha').value = '';
  UI.mostrarTela('tela-login');
}

document.getElementById('btn-admin-deslogar').addEventListener('click', deslogar);
document.getElementById('btn-sol-deslogar').addEventListener('click', deslogar);
document.getElementById('btn-sol-deslogar2').addEventListener('click', deslogar);

// ═══════════════════════════════════════════════════════════════════════════
// ADMINISTRADOR — TABELA
// ═══════════════════════════════════════════════════════════════════════════

function carregarChamadosAdmin() {
  Api.listarChamados()
    .then(lista => {
      chamadosCache = lista;
      atualizarStats(lista);
      UI.popularTabelaAdmin(lista, abrirModalChamado);
    })
    .catch(() => UI.mostrarErro('Erro', 'Não foi possível carregar os chamados.'));
}

function atualizarStats(lista) {
  document.getElementById('stat-total').textContent  = lista.length;
  document.getElementById('stat-aberto').textContent = lista.filter(c => c.status === 'Aberto').length;
  document.getElementById('stat-atend').textContent  = lista.filter(c => c.status === 'Em Atendimento').length;
  document.getElementById('stat-final').textContent  = lista.filter(c => c.status === 'Finalizado').length;
}

document.getElementById('btn-admin-atualizar').addEventListener('click', carregarChamadosAdmin);

// Filtros em tempo real
document.getElementById('admin-filtro').addEventListener('input', () => {
  UI.popularTabelaAdmin(chamadosCache, abrirModalChamado);
});
document.getElementById('admin-filtro-status').addEventListener('change', () => {
  UI.popularTabelaAdmin(chamadosCache, abrirModalChamado);
});

document.getElementById('admin-filtro-id').addEventListener('input', buscarChamadoAdminPorIdNaTabela);

function buscarChamadoAdminPorIdNaTabela() {
  const id = document.getElementById('admin-filtro-id').value.trim();

  if (!id) {
    UI.popularTabelaAdmin(chamadosCache, abrirModalChamado);
    return;
  }

  Api.buscarChamado(id)
      .then(chamado => {
        UI.popularTabelaAdmin([chamado], abrirModalChamado);
      })
      .catch(() => {
        UI.popularTabelaAdmin([], abrirModalChamado);
      });
}

// ═══════════════════════════════════════════════════════════════════════════
// SOLICITANTE — ABAS
// ═══════════════════════════════════════════════════════════════════════════

document.querySelectorAll('.abas-nav .aba-btn').forEach(btn => {
  btn.addEventListener('click', () => {
    document.querySelectorAll('.abas-nav .aba-btn').forEach(b => b.classList.remove('ativa'));
    btn.classList.add('ativa');

    const aba = btn.dataset.aba;
    document.querySelectorAll('.painel-aba').forEach(p => p.classList.remove('ativo'));
    document.getElementById('painel-' + aba).classList.add('ativo');

    if (aba === 'meus-chamados') carregarChamadosSolicitante();
  });
});

// ═══════════════════════════════════════════════════════════════════════════
// SOLICITANTE — CHAMADOS
// ═══════════════════════════════════════════════════════════════════════════

function carregarChamadosSolicitante() {
  if (!usuarioAtual) return;
  Api.listarChamados(usuarioAtual.nome)
    .then(lista => UI.popularTabelaSolicitante(lista, abrirModalChamado))
    .catch(() => UI.mostrarErro('Erro', 'Não foi possível carregar seus chamados.'));
}

document.getElementById('btn-sol-atualizar').addEventListener('click', carregarChamadosSolicitante);

document.getElementById('btn-sol-buscar-id').addEventListener('click', buscarChamadoSolicitantePorId);

document.getElementById('sol-busca-id').addEventListener('keydown', e => {
  if (e.key === 'Enter') {
    buscarChamadoSolicitantePorId();
  }
});

function buscarChamadoSolicitantePorId() {
  const id = document.getElementById('sol-busca-id').value.trim();

  if (!id) {
    UI.mostrarErro('ID obrigatório', 'Digite o ID do chamado que deseja buscar.');
    return;
  }

  Api.buscarChamado(id)
      .then(chamado => {
        if (usuarioAtual.perfil !== 'ADMINISTRADOR' && chamado.solicitante !== usuarioAtual.nome) {
          UI.mostrarErro('Acesso negado', 'Esse chamado não pertence ao seu usuário.');
          return;
        }

        abrirModalChamado(chamado.id);
      })
      .catch(() => UI.mostrarErro('Não encontrado', 'Nenhum chamado foi encontrado com esse ID.'));
}

// ═══════════════════════════════════════════════════════════════════════════
// SOLICITANTE — CRIAR CHAMADO
// ═══════════════════════════════════════════════════════════════════════════

function carregarAplicativos() {
  Api.aplicativos()
    .then(apps => UI.popularAplicativos(apps))
    .catch(() => {});
}

document.getElementById('btn-enviar-chamado').addEventListener('click', () => {
  const titulo      = document.getElementById('sol-titulo').value.trim();
  const aplicativo  = document.getElementById('sol-aplicativo').value;
  const descricao   = document.getElementById('sol-descricao').value.trim();

  if (!titulo || !aplicativo || !descricao) {
    UI.mostrarErro('Campos obrigatórios', 'Preencha título, aplicativo e descrição.');
    return;
  }

  Api.criarChamado(titulo, aplicativo, descricao, usuarioAtual.nome, usuarioAtual.login)
    .then(() => {
      document.getElementById('sol-titulo').value = '';
      document.getElementById('sol-aplicativo').selectedIndex = 0;
      document.getElementById('sol-descricao').value = '';
      return UI.mostrarSucesso('Chamado enviado', 'Seu chamado foi aberto com sucesso!');
    })
    .catch(msg => UI.mostrarErro('Erro ao criar chamado', msg || 'Tente novamente.'));
});

// ═══════════════════════════════════════════════════════════════════════════
// MODAL — ABRIR CHAMADO
// ═══════════════════════════════════════════════════════════════════════════

function abrirModalChamado(id) {
  chamadoModalId = id;
  const isAdmin = usuarioAtual && usuarioAtual.perfil === 'ADMINISTRADOR';

  Api.buscarChamado(id)
    .then(chamado => {
      UI.preencherDetalhe(chamado, isAdmin);
      UI.abrirModal();
    })
    .catch(() => UI.mostrarErro('Erro', 'Não foi possível carregar o chamado.'));
}

// Fechar modal
document.getElementById('modal-fechar').addEventListener('click', fecharModal);
document.getElementById('btn-det-fechar').addEventListener('click', fecharModal);
document.getElementById('btn-det-fechar-sol').addEventListener('click', fecharModal);
document.getElementById('modal-overlay').addEventListener('click', e => {
  if (e.target === document.getElementById('modal-overlay')) fecharModal();
});

function fecharModal() {
  UI.fecharModal();
  chamadoModalId = null;
  // Recarrega a tabela correspondente
  if (usuarioAtual) {
    if (usuarioAtual.perfil === 'ADMINISTRADOR') carregarChamadosAdmin();
    else carregarChamadosSolicitante();
  }
}

// ═══════════════════════════════════════════════════════════════════════════
// MODAL — ABAS
// ═══════════════════════════════════════════════════════════════════════════

document.getElementById('modal-aba-dados').addEventListener('click', () => UI.trocarAbaModal('dados'));

document.getElementById('modal-aba-historico').addEventListener('click', () => {
  UI.trocarAbaModal('historico');
  if (chamadoModalId !== null) {
    Api.historico(chamadoModalId)
      .then(hist => UI.preencherHistorico(hist))
      .catch(() => UI.preencherHistorico([]));
  }
});

// ═══════════════════════════════════════════════════════════════════════════
// MODAL — BOTÕES ADMIN
// ═══════════════════════════════════════════════════════════════════════════

// Cancelar chamado (admin)
document.getElementById('btn-det-cancelar').addEventListener('click', () => {
  if (chamadoModalId === null) return;
  confirmarCancelar(chamadoModalId);
});

// Cancelar chamado (solicitante)
document.getElementById('btn-det-cancelar-sol').addEventListener('click', () => {
  if (chamadoModalId === null) return;
  confirmarCancelar(chamadoModalId);
});

function confirmarCancelar(id) {
  UI.mostrarMensagem(
    'Cancelar Chamado',
    'Tem certeza que deseja cancelar este chamado? Esta ação não pode ser desfeita.',
    'erro'
  ).then(() => {
    Api.cancelarChamado(id, usuarioAtual)
      .then(() => fecharModal())
      .catch(msg => UI.mostrarErro('Erro', msg || 'Não foi possível cancelar o chamado.'));
  });
}

// Atuar no chamado
document.getElementById('btn-det-atuar').addEventListener('click', () => {
  if (chamadoModalId === null) return;

  Api.atuarChamado(chamadoModalId, usuarioAtual)
    .then(chamado => {
      UI.preencherDetalhe(chamado, true);
      document.getElementById('det-atendente').value = usuarioAtual.nome;
      UI.mostrarSucesso('Chamado assumido', 'Você está atuando neste chamado.');
    })
    .catch(msg => UI.mostrarErro('Erro', msg || 'Não foi possível atuar no chamado.'));
});

// Salvar dados
document.getElementById('btn-det-salvar').addEventListener('click', () => {
  if (chamadoModalId === null) return;

  const dados = coletarDadosModal();
  if (!dados) return;

  Api.salvarChamado(chamadoModalId, dados, usuarioAtual)
    .then(chamado => {
      UI.preencherDetalhe(chamado, true);
      UI.mostrarSucesso('Dados salvos', 'Dados do chamado salvos com sucesso.');
    })
    .catch(msg => UI.mostrarErro('Erro', msg || 'Não foi possível salvar.'));
});

// Finalizar chamado
document.getElementById('btn-det-finalizar').addEventListener('click', () => {
  if (chamadoModalId === null) return;

  const dados = coletarDadosModal(true);
  if (!dados) return;

  Api.finalizarChamado(chamadoModalId, dados, usuarioAtual)
    .then(() => {
      UI.mostrarSucesso('Chamado finalizado', 'Chamado finalizado com sucesso.')
        .then(() => fecharModal());
    })
    .catch(msg => UI.mostrarErro('Erro', msg || 'Não foi possível finalizar.'));
});

function coletarDadosModal(validar = false) {
  const atendente    = document.getElementById('det-atendente').value.trim();
  const dataPrevista = document.getElementById('det-data-prevista').value.trim();
  const solucao      = document.getElementById('det-solucao').value.trim();
  const horasStr     = document.getElementById('det-horas').value.trim();

  if (validar) {
    if (!atendente || !dataPrevista || !solucao || !horasStr) {
      UI.mostrarErro('Campos obrigatórios', 'Preencha todos os campos antes de finalizar.');
      return null;
    }
  }

  let horasGastas = 0;
  if (horasStr) {
    horasGastas = parseFloat(horasStr.replace(',', '.'));
    if (isNaN(horasGastas)) {
      UI.mostrarErro('Valor inválido', 'Horas gastas deve ser um número.');
      return null;
    }
  }

  return { atendente, dataPrevista, solucao, horasGastas };
}

// ═══════════════════════════════════════════════════════════════════════════
// MÁSCARA DATA (dd/mm/aaaa)
// ═══════════════════════════════════════════════════════════════════════════

document.getElementById('det-data-prevista').addEventListener('input', function () {
  let v = this.value.replace(/\D/g, '').substring(0, 8);
  if (v.length >= 3) v = v.substring(0,2) + '/' + v.substring(2);
  if (v.length >= 6) v = v.substring(0,5) + '/' + v.substring(5);
  this.value = v;
});
