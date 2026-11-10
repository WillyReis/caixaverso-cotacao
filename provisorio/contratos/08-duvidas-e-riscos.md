# Duvidas e Riscos

## Duvidas em aberto

| ID | Duvida | Responsavel | Criterio de resolucao | Status |
| --- | --- | --- | --- | --- |
| D-001 | Quais tres das quatro entregas candidatas compoem a primeira versao? | Grupo | Registrar a selecao em `01-contexto.md` e atualizar requisitos, criterios e tarefas. | Aberta |
| D-002 | A API somente leitora do ADR-002 sera entregue na primeira versao, ou sua responsabilidade arquitetural nao implica compromisso de entrega agora? | Grupo | Reconciliar ADR-002 com a selecao de escopo e registrar decisao. | Aberta |
| D-003 | Qual e o contrato do canal de consumo: filtros, formato, campo e valores de disponibilidade, dados parciais, erros e status HTTP? O acesso direto ao banco e permitido? | Grupo/equipe tecnica | Aprovar contrato e consumidor de teste antes da implementacao do canal. | Aberta |
| D-004 | Qual calendario deve executar as coletas (dias uteis, feriados e excecoes) e qual identificador de fuso representa o horario de Brasilia? | Grupo/equipe tecnica | Definir calendario e validar horarios de 10h e 13h em teste. | Aberta |
| D-005 | Como recuperar pendencias e tempos de tentativa apos reinicio ou indisponibilidade do job? | Equipe tecnica | Definir estado persistido e demonstrar retomada sem perda ou duplicidade. | Aberta |
| D-006 | Qual mecanismo, destino e escalonamento usar para avisos de pendencia prolongada? Havera limite final de tentativas? | Grupo/equipe operacional | Definir politica e criterio de encerramento/escalonamento. | Aberta |
| D-007 | `CotacaoMoedaPeriodo` retorna dados completos e tipos de boletim adequados para reconciliar USD e EUR? Qual periodo e estrategia usar? | Equipe tecnica | Executar prova de conceito em intervalo conhecido; diferenciar resposta vazia de erro tecnico. | Aberta |
| D-008 | Como classificar datas sem publicacao sem confundir feriados com atraso ou falha? Existe fonte confiavel alem do recurso de periodo? | Grupo/equipe tecnica | Aprovar regra baseada em evidencia; nao inferir ausencia por resposta vazia isolada. | Aberta |
| D-009 | O criterio historico de rejeitar consultas para datas futuras aplica-se ao canal que for selecionado? Qual resposta deve ser apresentada? | Grupo | Aprovar ou retirar CA-009 e definir resposta observavel. | Aberta |
| D-010 | O ADR-001 completo e os diagramas C4 de niveis 1 e 2 estao disponiveis para incorporacao? | Grupo | Localizar e versionar os artefatos citados no material. | Aberta |
| D-011 | Quais sao as URLs oficiais completas das referencias da API PTAX e dos exemplos BACEN? | Grupo/equipe tecnica | Recuperar, validar e registrar URLs nao truncadas. | Aberta |
| D-012 | Quais limites de desempenho, disponibilidade, volume e retencao sao necessarios para a entrega selecionada? | Grupo | Definir alvos antes de fechar requisitos de producao. | Aberta |
| D-013 | Qual e a versao final do Golden Path e quais de suas orientacoes se aplicam a este projeto e ao uso de LLM no fluxo SDD? | Grupo/equipe tecnica | Validar a referencia vigente e registrar as orientacoes aplicaveis antes de iniciar tarefas de implementacao com LLM. | Aberta |

## Riscos

| ID | Risco | Probabilidade | Impacto | Mitigacao ou contingencia |
| --- | --- | --- | --- | --- |
| R-001 | A publicacao do boletim pode atrasar ou nao ocorrer na janela esperada. | A avaliar | Historico incompleto ou atrasado para consumidores. | Manter pendencias independentes, aplicar novas tentativas e comunicar atraso sem coleta sob demanda. |
| R-002 | Uma resposta vazia ou falha tecnica pode ser confundida com feriado/dia sem publicacao. | A avaliar | Lacunas silenciosas ou classificacao incorreta do historico. | Aplicar RN-009; validar `CotacaoMoedaPeriodo` e nao inferir ausencia de publicacao sem evidencia. |
| R-003 | Reinicio, recuperacao entre dias ou repeticao da resposta pode perder ou duplicar registros. | A avaliar | Historico inconsistente. | Persistir estado de pendencia e adotar identificacao idempotente por moeda, data e tipo; provar com testes. |
| R-004 | A primeira entrega pode crescer para incluir API e pagina sem decisao sobre prioridade ou contrato. | A avaliar | Atraso e escopo ambiguo para SDD/LLM. | Escolher explicitamente as entregas; tarefas condicionais nao iniciam antes da aprovacao. |
| R-005 | Consultas podem apresentar historico desatualizado ou parcial, pois dependem do job agendado. | A avaliar | Consumidor interpretar ausencia como cotacao inexistente. | Definir campo/estado de disponibilidade no canal selecionado e documentar limites de atualizacao. |
| R-006 | Stack, diagramas e referencias oficiais citados no material podem nao estar versionados ou completos. | A avaliar | Implementacao baseada em artefatos nao verificaveis. | Resolver D-010 e D-011 antes de fechar plano tecnico e integracao. |