/**
 * ui.js — helpers de interface
 */

const UI = {

  // ── Troca de tela ────────────────────────────────────────────────────────
  mostrarTela(id) {
    document.querySelectorAll('.tela').forEach(t => t.classList.remove('ativa'));
    document.getElementById(id).classList.add('ativa');
  },

  // ── Modal de mensagem ────────────────────────────────────────────────────
  mostrarMensagem(titulo, texto, tipo = 'sucesso') {
    document.getElementById('msg-titulo').textContent = titulo;
    document.getElementById('msg-texto').textContent  = texto;

    const wrapper = document.getElementById('msg-icone-wrapper');
    const icone   = document.getElementById('msg-icone');

    wrapper.className = 'msg-icone-wrapper ' + tipo;
    icone.textContent  = tipo === 'sucesso' ? '✓' : '✕';

    document.getElementById('msg-overlay').style.display = 'flex';

    return new Promise(resolve => {
      const btn = document.getElementById('btn-msg-ok');
      const handler = () => {
        document.getElementById('msg-overlay').style.display = 'none';
        btn.removeEventListener('click', handler);
        resolve();
      };
      btn.addEventListener('click', handler);
    });
  },

  mostrarSucesso(titulo, texto) { return this.mostrarMensagem(titulo, texto, 'sucesso'); },
  mostrarErro(titulo, texto)    { return this.mostrarMensagem(titulo, texto, 'erro');    },

  // ── Status badge ─────────────────────────────────────────────────────────
  badgeStatus(status) {
    const s = (status || '').toLowerCase();
    let cls = 'status-badge ';
    if (s.includes('aberto'))        cls += 'status-aberto';
    else if (s.includes('atendimento')) cls += 'status-atendimento';
    else if (s.includes('finalizado'))  cls += 'status-finalizado';
    else cls += 'status-aberto';
    return `<span class="${cls}">${status || ''}</span>`;
  },

  // ── Popula tabela admin ──────────────────────────────────────────────────
  popularTabelaAdmin(chamados, onAbrir) {
    const tbody = document.getElementById('admin-tbody');
    const vazia = document.getElementById('admin-vazia');
    tbody.innerHTML = '';

    const filtroTexto  = document.getElementById('admin-filtro').value.toLowerCase();
    const filtroStatus = document.getElementById('admin-filtro-status').value;

    const filtrados = chamados.filter(c => {
      const texto = `${c.titulo} ${c.solicitante} ${c.status}`.toLowerCase();
      const passaTexto  = !filtroTexto  || texto.includes(filtroTexto);
      const passaStatus = !filtroStatus || c.status === filtroStatus;
      return passaTexto && passaStatus;
    });

    if (filtrados.length === 0) {
      vazia.style.display = 'block';
      return;
    }
    vazia.style.display = 'none';

    filtrados.forEach(c => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td><b>#${c.id}</b></td>
        <td>${this.esc(c.titulo)}</td>
        <td>${this.esc(c.solicitante)}</td>
        <td>${this.esc(c.dataSolicitacao)}</td>
        <td>${c.atendente ? this.esc(c.atendente) : '<span style="color:#aaa">Não atribuído</span>'}</td>
        <td>${c.dataPrevistaEncerramento || '<span style="color:#aaa">Pendente</span>'}</td>
        <td>${this.badgeStatus(c.status)}</td>
        <td><button class="btn-acao" data-id="${c.id}">Abrir</button></td>
      `;
      tr.querySelector('.btn-acao').addEventListener('click', () => onAbrir(c.id));
      tbody.appendChild(tr);
    });
  },

  // ── Popula tabela solicitante ─────────────────────────────────────────────
  popularTabelaSolicitante(chamados, onAbrir) {
    const tbody = document.getElementById('sol-tbody');
    const vazia = document.getElementById('sol-vazia');
    tbody.innerHTML = '';

    if (chamados.length === 0) {
      vazia.style.display = 'block';
      return;
    }
    vazia.style.display = 'none';

    chamados.forEach(c => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td><b>#${c.id}</b></td>
        <td>${this.esc(c.titulo)}</td>
        <td>${this.esc(c.dataSolicitacao)}</td>
        <td>${c.atendente ? this.esc(c.atendente) : '<span style="color:#aaa">Não atribuído</span>'}</td>
        <td>${c.dataPrevistaEncerramento || '<span style="color:#aaa">Pendente</span>'}</td>
        <td>${this.badgeStatus(c.status)}</td>
        <td><button class="btn-acao" data-id="${c.id}">Ver</button></td>
      `;
      tr.querySelector('.btn-acao').addEventListener('click', () => onAbrir(c.id));
      tbody.appendChild(tr);
    });
  },

  // ── Preenche modal detalhe ────────────────────────────────────────────────
  preencherDetalhe(chamado, isAdmin) {
    document.getElementById('modal-titulo').textContent = `Detalhes do Chamado #${chamado.id}`;
    document.getElementById('det-id').textContent          = '#' + chamado.id;
    document.getElementById('det-titulo').textContent      = chamado.titulo || '';
    document.getElementById('det-aplicativo').textContent  = chamado.aplicativo || '';
    document.getElementById('det-solicitante').textContent = chamado.solicitante || '';
    document.getElementById('det-data-sol').textContent    = chamado.dataSolicitacao || '';
    document.getElementById('det-descricao').value         = chamado.descricao || '';
    document.getElementById('det-atendente').value         = chamado.atendente || '';
    document.getElementById('det-data-prevista').value     = chamado.dataPrevistaEncerramento || '';
    document.getElementById('det-solucao').value           = chamado.solucao || '';
    document.getElementById('det-horas').value             = chamado.horasGastas || '';

    // status badge
    document.getElementById('det-status-badge').innerHTML = this.badgeStatus(chamado.status);

    const finalizado = (chamado.status || '').toLowerCase().includes('finalizado');
    const aberto     = (chamado.status || '').toLowerCase().includes('aberto');
    const cancelado  = (chamado.status || '').toLowerCase().includes('cancelado');

    if (isAdmin) {
      document.getElementById('modal-botoes-admin').style.display = 'flex';
      document.getElementById('modal-botoes-sol').style.display   = 'none';
      document.getElementById('det-atendente').readOnly     = false;
      document.getElementById('det-data-prevista').readOnly = false;
      document.getElementById('det-solucao').readOnly       = false;
      document.getElementById('det-horas').readOnly         = false;
      document.getElementById('btn-det-atuar').disabled    = !aberto;
      document.getElementById('btn-det-salvar').disabled   = finalizado;
      document.getElementById('btn-det-finalizar').disabled = finalizado;
      document.getElementById('btn-det-cancelar').disabled  = finalizado || cancelado;
    } else {
      document.getElementById('modal-botoes-admin').style.display = 'none';
      document.getElementById('modal-botoes-sol').style.display   = 'flex';
      document.getElementById('det-atendente').readOnly     = true;
      document.getElementById('det-data-prevista').readOnly = true;
      document.getElementById('det-solucao').readOnly       = true;
      document.getElementById('det-horas').readOnly         = true;
      document.getElementById('btn-det-cancelar-sol').disabled = finalizado || cancelado;
    }

    // Volta pra aba dados
    this.trocarAbaModal('dados');
  },

  // ── Preenche histórico no modal ───────────────────────────────────────────
  preencherHistorico(historicos) {
    const lista = document.getElementById('historico-lista');
    lista.innerHTML = '';

    if (!historicos || historicos.length === 0) {
      lista.innerHTML = '<p style="color:#aaa;text-align:center;padding:20px">Sem histórico.</p>';
      return;
    }

    historicos.forEach(h => {
      const div = document.createElement('div');
      div.className = 'historico-item';
      div.innerHTML = `
        <div class="historico-data">${this.esc(h.dataHora)}</div>
        <div class="historico-usuario">${this.esc(h.usuario)}</div>
        <div class="historico-acao">${this.esc(h.acao)}</div>
      `;
      lista.appendChild(div);
    });
  },

  // ── Trocar aba do modal ───────────────────────────────────────────────────
  trocarAbaModal(aba) {
    document.querySelectorAll('.modal-abas .aba-btn').forEach(b => {
      b.classList.toggle('ativa', b.dataset.modalAba === aba);
    });
    document.getElementById('modal-painel-dados').classList.toggle('ativo',     aba === 'dados');
    document.getElementById('modal-painel-historico').classList.toggle('ativo', aba === 'historico');
  },

  // ── Abrir / fechar modal detalhe ──────────────────────────────────────────
  abrirModal()  { document.getElementById('modal-overlay').style.display = 'flex'; },
  fecharModal() { document.getElementById('modal-overlay').style.display = 'none'; },

  // ── Popula select de aplicativos ──────────────────────────────────────────
  popularAplicativos(apps) {
    const sel = document.getElementById('sol-aplicativo');
    sel.innerHTML = '<option value="">Selecione o aplicativo...</option>';
    apps.forEach(a => {
      const opt = document.createElement('option');
      opt.value = a;
      opt.textContent = a;
      sel.appendChild(opt);
    });
  },

  // ── Escape HTML ───────────────────────────────────────────────────────────
  esc(str) {
    if (!str) return '';
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }
};
