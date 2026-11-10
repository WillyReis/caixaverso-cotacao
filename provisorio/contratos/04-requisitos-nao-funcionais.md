# Requisitos Nao Funcionais

Os parametros sem decisao do grupo estao identificados como pendentes; nao assumir valores padrao como compromisso de produto.

| ID | Categoria | Requisito | Criterio ou situacao |
| --- | --- | --- | --- |
| RNF-001 | Tempo e agendamento | Interpretar os horarios de coleta no horario de Brasilia, sem depender do fuso configurado no host. | Validar execucoes de abertura as 10h e encerramento as 13h no fuso acordado. O identificador de fuso e calendario ainda precisam ser confirmados. |
| RNF-002 | Integridade | Reprocessar um boletim nao deve produzir duplicidade para a mesma data, moeda e tipo de boletim. | Verificar em teste de persistencia/idempotencia; mecanismo e chave fisica pendentes no plano tecnico. |
| RNF-003 | Isolamento de falhas | Falha ou indisponibilidade de um item de coleta nao deve impedir o controle dos demais itens pendentes nem as coletas do dia atual. | Verificar com falhas independentes de moeda, data e boletim (CA-001 e CA-005). |
| RNF-004 | Observabilidade | Registrar falhas e pendencias prolongadas de modo que a equipe possa identifica-las. | O aviso de pendencia prolongada esta definido; formato, destino, severidade e mecanismo ainda precisam de decisao. |
| RNF-005 | Seguranca | Caso a API interna seja entregue, somente consumidores autorizados podem consultar os dados. | Confirmado como intencao; autenticacao, autorizacao, perfis e gestao de credenciais pendentes. |
| RNF-006 | Operacao e recuperacao | O comportamento do job apos reinicio deve preservar ou reconstruir pendencias sem perder a data, moeda e boletim de origem. | Necessidade identificada; estrategia de persistencia e criterio de teste pendentes antes da implementacao. |
| RNF-007 | Desempenho, disponibilidade e retencao | Definir limites de resposta, disponibilidade, volume, retencao e crescimento para a entrega selecionada. | Nenhum alvo numerico foi acordado no material; levantar antes de fechar requisitos de producao. |