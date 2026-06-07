/**
 * api.js — camada de comunicação com o backend Java
 * Todas as funções retornam Promises.
 * Troca apenas esta URL se o backend mudar de porta.
 */

const API = 'http://localhost:8080/api';

const Api = {

  // POST /api/login
  login(login, senha) {
    return fetch(`${API}/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ login, senha })
    }).then(res => {
      if (!res.ok) return res.json().then(e => Promise.reject(e.erro || 'Erro no login'));
      return res.json();
    });
  },

  // GET /api/chamados  ou  /api/chamados?solicitante=Nome
  listarChamados(solicitante = null) {
    const url = solicitante
      ? `${API}/chamados?solicitante=${encodeURIComponent(solicitante)}`
      : `${API}/chamados`;
    return fetch(url).then(res => res.json());
  },

  // GET /api/chamados/{id}
  buscarChamado(id) {
    return fetch(`${API}/chamados/${id}`).then(res => {
      if (!res.ok) return res.json().then(e => Promise.reject(e.erro));
      return res.json();
    });
  },

  // POST /api/chamados
  criarChamado(titulo, aplicativo, descricao, solicitante, loginSolicitante) {
    return fetch(`${API}/chamados`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ titulo, aplicativo, descricao, solicitante, loginSolicitante })
    }).then(res => {
      if (!res.ok) return res.json().then(e => Promise.reject(e.erro));
      return res.json();
    });
  },

  // PUT /api/chamados/{id}/atuar
  atuarChamado(id, usuario) {
    return fetch(`${API}/chamados/${id}/atuar`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ nome: usuario.nome, login: usuario.login, perfil: usuario.perfil })
    }).then(res => {
      if (!res.ok) return res.json().then(e => Promise.reject(e.erro));
      return res.json();
    });
  },

  // PUT /api/chamados/{id}/salvar
  salvarChamado(id, dados, usuario) {
    return fetch(`${API}/chamados/${id}/salvar`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        atendente:    dados.atendente,
        dataPrevista: dados.dataPrevista,
        solucao:      dados.solucao,
        horasGastas:  dados.horasGastas,
        nomeUsuario:  usuario.nome,
        loginUsuario: usuario.login
      })
    }).then(res => {
      if (!res.ok) return res.json().then(e => Promise.reject(e.erro));
      return res.json();
    });
  },

  // PUT /api/chamados/{id}/finalizar
  finalizarChamado(id, dados, usuario) {
    return fetch(`${API}/chamados/${id}/finalizar`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        atendente:    dados.atendente,
        dataPrevista: dados.dataPrevista,
        solucao:      dados.solucao,
        horasGastas:  dados.horasGastas,
        nomeUsuario:  usuario.nome,
        loginUsuario: usuario.login
      })
    }).then(res => {
      if (!res.ok) return res.json().then(e => Promise.reject(e.erro));
      return res.json();
    });
  },

  // PUT /api/chamados/{id}/cancelar
  cancelarChamado(id, usuario) {
    return fetch(`${API}/chamados/${id}/cancelar`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        nomeUsuario:  usuario.nome,
        loginUsuario: usuario.login,
        perfil:       usuario.perfil
      })
    }).then(res => {
      if (!res.ok) return res.json().then(e => Promise.reject(e.erro));
      return res.json();
    });
  },

  // GET /api/historico/{id}
  historico(id) {
    return fetch(`${API}/historico/${id}`).then(res => res.json());
  },

  // GET /api/aplicativos
  aplicativos() {
    return fetch(`${API}/aplicativos`).then(res => res.json());
  }
};
