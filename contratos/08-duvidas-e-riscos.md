# 08 · Dúvidas e Riscos

## Situação do registro

- Aberta: dúvida que ainda depende de decisão, definição ou validação. Seu registro não autoriza a implementação de uma proposta ou a escolha de parâmetros por suposição.
- Parcialmente resolvida: parte da dúvida já possui evidência ou decisão registrada, mas o restante continua pendente.
- As dúvidas de entregas condicionais se aplicam somente ao escopo selecionado. Elas não bloqueiam entregas independentes que não utilizam o comportamento em questão.
- Os IDs D-001 a D-013 e R-001 a R-006 da primeira versão provisória foram preservados. D-014 a D-020 complementam o registro com pendências já descritas nos contratos oficiais, sem criar novas entregas.
- Os responsáveis são indicados por papel, conforme o material do grupo. A atribuição nominal de responsáveis pelas decisões e tarefas ainda deve ser confirmada.
- As probabilidades dos riscos permanecem a avaliar. Não foram acordadas classificações numéricas ou níveis de aceitação, e uma mitigação planejada não significa que o risco já foi eliminado.

## Decisões já registradas

- Java 25 e Quarkus 3.40 LTS estão definidos no [ADR-001](../adr/adr-001.md), já localizado. Sua disponibilidade não permanece como dúvida aberta; a incorporação dos diagramas C4 ainda está pendente em D-010.
- H2 substitui PostgreSQL conforme a Constituição e o [ADR-002 consolidado](../adr/adr-002.md). Modo de operação, armazenamento, modelo físico e acesso ao banco continuam pendentes.
- O escopo cambial inicial contempla USD e EUR, com compra e venda dos boletins de abertura e encerramento, sem os boletins intermediários das 11h e 12h.
- A ingestão é assíncrona e agendada. Os consumidores consultam somente dados locais, e a ausência de dados não inicia consulta síncrona ao BACEN nem coleta sob demanda.
- Valores monetários usam `BigDecimal`. Isso não define automaticamente precisão, escala, tipos físicos no H2 ou formato de exposição.

## Dúvidas

| ID | Dúvida | Responsável | Critério de resolução | Situação |
| --- | --- | --- | --- | --- |
| D-001 | Quais três das quatro entregas candidatas compõem a primeira versão? | Grupo | Registrar a seleção no contexto e atualizar requisitos, critérios, plano e tarefas, conforme T-001. | Aberta. |
| D-002 | A API somente leitora descrita no ADR-002 será entregue na primeira versão, ou sua responsabilidade arquitetural não implica compromisso de entrega agora? | Grupo | Reconciliar o ADR-002 com a seleção de escopo e registrar a aplicabilidade da API, conforme T-001 e T-008. | Aberta. |
| D-003 | Qual é o contrato do canal de consumo: filtros, formato, campo e valores de disponibilidade, dados parciais, erros e status HTTP? O acesso direto ao banco é permitido? | Grupo e equipe técnica | Aprovar o contrato do canal selecionado, a forma de acesso e o consumidor de teste antes da implementação, conforme T-008 e os critérios aplicáveis CA-006, CA-007 e CA-011. | Aberta; detalhes condicionados ao canal selecionado. |
| D-004 | Quais horários, calendário e identificador de fuso devem ser usados nas coletas, incluindo dias úteis, feriados e exceções? | Grupo e equipe técnica | Resolver a divergência entre 10h/13h e 9h/16h registrada no contexto. Aprovar horários, fuso e calendário e detalhar a verificação de independência do fuso do host, conforme T-004 e RNF-001. | Aberta. |
| D-005 | Como recuperar pendências e tempos de tentativa após reinício ou indisponibilidade do job? | Equipe técnica | Aprovar a estratégia de preservação ou reconstrução e o estado necessário, mantendo data, moeda e tipo de boletim. Detalhar o teste de retomada sem perda ou duplicidade, conforme T-004, T-005 e RNF-006; sua execução deve acompanhar T-007 e T-011. | Aberta. |
| D-006 | Quais limites de execução, mecanismo, destino e escalonamento devem ser usados para falhas e pendências prolongadas? Haverá limite final de tentativas? | Grupo e equipe operacional | Aprovar formato, destino, severidade e mecanismo dos registros e avisos, além dos limites e eventual encerramento ou escalonamento. Definir a borda exata da transição das três horas iniciais para as tentativas horárias, conforme T-004, RN-005, RN-007 e RNF-004, sem alterar implicitamente a política registrada. | Aberta. |
| D-007 | `CotacaoMoedaPeriodo` retorna dados completos e tipos de boletim adequados para reconciliar USD e EUR? Qual período e estratégia devem ser usados? | Equipe técnica | Executar a prova de conceito em intervalo conhecido e registrar completude, tipos e resultados, distinguindo resposta vazia de erro técnico. Submeter o fluxo à aprovação ou rejeição do grupo antes da implementação, conforme T-010, RF-008 e CA-008. | Aberta; reconciliação proposta. |
| D-008 | Como comprovar datas sem publicação sem confundir feriados com atraso ou falha? Existe fonte confiável além do recurso de período? | Grupo e equipe técnica | Validar e aprovar as fontes e os critérios de evidência aplicáveis, conforme T-003 e T-004; considerar T-010 somente se a reconciliação for aprovada. A restrição da RN-009 permanece obrigatória: resposta vazia ou falha técnica não comprova, isoladamente, ausência de publicação. | Aberta; restrição de classificação já confirmada. |
| D-009 | O critério histórico de rejeitar consultas por datas futuras aplica-se à API, se selecionada? Qual resposta deve ser apresentada? | Grupo | Aprovar ou retirar CA-009 e, se aprovado, definir a referência da data atual e a resposta observável, incluindo status e mensagem, conforme T-008. | Aberta; condicionada à seleção da API e à aprovação da regra. |
| D-010 | Os diagramas C4 de níveis 1 e 2 mencionados pelo grupo estão disponíveis e incorporados à documentação? | Grupo | O ADR-001 já está localizado. Localizar, versionar e incorporar os diagramas, reconciliando-os com os ADRs aceitos e com o escopo selecionado, conforme T-002. | Parcialmente resolvida; ADR-001 disponível, incorporação dos C4 pendente. |
| D-011 | Quais são as URLs oficiais completas da API PTAX e dos exemplos BACEN? | Grupo e equipe técnica | Recuperar, validar e registrar referências não truncadas e as evidências das consultas de USD e EUR, conforme T-003. | Aberta. |
| D-012 | Quais limites de desempenho, disponibilidade, volume, retenção e crescimento são necessários para a entrega selecionada? | Grupo | Aprovar alvos e métodos de medição antes de fechar os requisitos de produção, conforme T-004 e RNF-007. As verificações dos compromissos acordados devem ser consolidadas em T-011. | Aberta; nenhum alvo numérico acordado. |
| D-013 | Qual é a versão final do Golden Path e quais orientações se aplicam ao projeto e ao uso de LLM no fluxo SDD? | Grupo e equipe técnica | Validar a referência vigente e registrar as orientações aplicáveis na Constituição e no plano antes de iniciar tarefas de implementação com LLM, conforme T-012. | Aberta. |

## Pendências complementares dos contratos oficiais

| ID | Dúvida | Responsável | Critério de resolução | Situação |
| --- | --- | --- | --- | --- |
| D-014 | Qual modelo físico, precisão, escala, restrições e estratégia de persistência no H2 assegurarão a integridade e a ausência de duplicidade? A resposta original do BACEN será armazenada? | Equipe técnica | Aprovar no plano o modelo e a representação física dos valores em `BigDecimal`, materializando a identificação lógica por data, moeda e tipo de boletim sem duplicidade. Registrar a decisão sobre a resposta original, conforme T-005, RN-008 e RNF-002. | Aberta; banco e identificação lógica já definidos. |
| D-015 | Qual será a organização dos componentes e a topologia de processos, containers e implantação? Como serão definidos configuração, modo de operação e armazenamento do H2 e acesso aos dados? | Grupo e equipe técnica | Aprovar a topologia e as definições de operação e acesso no plano, conforme T-002 e T-005, sem presumir processos separados ou um mecanismo de compartilhamento do H2. | Aberta. |
| D-016 | Se a API for entregue, quais mecanismos de autenticação e autorização, perfis e gestão de credenciais serão adotados? | Grupo e equipe técnica | Aprovar a política e os critérios de acesso de consumidores autorizados e não autorizados, conforme T-008 e RNF-005, com verificação em T-011. SSO permanece uma proposta, não uma escolha implícita. | Aberta; condicionada à seleção da API. |
| D-017 | Qual versão estável do Maven será fixada no início da implementação? | Equipe técnica | Registrar a versão selecionada no plano e na configuração de build ao preparar a estrutura de implementação, conforme T-006, sem substituir as versões já aprovadas de Java e Quarkus. | Aberta; definição prevista no início da implementação. |
| D-018 | Se a página web for entregue, quais interface, tecnologia, visualização e estados de ausência, parcialidade e erro serão adotados? Haverá gráficos? | Grupo e equipe técnica | Aprovar o comportamento e a tecnologia da página e atualizar RF-007, CA-010 e o plano antes de implementar T-009. Angular, Angular Material e gráficos não devem ser presumidos. | Aberta; condicionada à seleção da página. |
| D-019 | A proposta de incluir Selic será aprovada? Qual série seria utilizada? | Grupo | Registrar aprovação ou rejeição no contexto. Se aprovada, validar a série e detalhar requisitos, critérios, plano e tarefas antes da implementação; a série 1178 citada no material não constitui decisão aprovada. | Aberta; proposta fora do escopo cambial aprovado. |
| D-020 | É necessária carga inicial do histórico desde 01/01/2020, como proposto na segunda versão provisória? | Grupo e equipe técnica | Registrar a decisão no contexto. Se aprovada, definir período, estratégia, critérios e tarefas da carga inicial antes de implementá-la, sem tratá-la como consequência automática da coleta agendada. | Aberta; proposta de carga inicial. |

## Riscos

| ID | Risco | Probabilidade | Impacto | Mitigação ou contingência |
| --- | --- | --- | --- | --- |
| R-001 | A publicação do boletim pode atrasar ou não ocorrer na janela esperada. | A avaliar | Histórico incompleto ou atrasado para os consumidores. | Manter pendências independentes, aplicar as tentativas e a recuperação aprovadas e comunicar pendências prolongadas sem coleta sob demanda, conforme RN-005 a RN-007 e T-007. Resolver D-004 e D-006 para os parâmetros operacionais. |
| R-002 | Uma resposta vazia ou falha técnica pode ser confundida com feriado ou dia sem publicação. | A avaliar | Lacunas silenciosas ou classificação incorreta do histórico. | Aplicar RN-009 independentemente da aprovação da reconciliação e não inferir ausência sem evidência. Resolver D-008 e validar `CotacaoMoedaPeriodo` em D-007 e T-010 somente para o fluxo proposto. |
| R-003 | Reinício, recuperação entre dias ou repetição de respostas pode perder ou duplicar registros. | A avaliar | Histórico e controle de pendências inconsistentes. | Resolver D-005 e D-014 e definir a preservação ou reconstrução de pendências e a persistência sem duplicidade. Verificar retomada e reprocessamento, conforme RN-008, RNF-002, RNF-006, T-005, T-007 e T-011. |
| R-004 | A primeira entrega pode crescer para incluir API e página sem decisão sobre prioridade ou contrato. | A avaliar | Atraso e escopo ambíguo para o fluxo SDD com LLM. | Resolver D-001 e D-002, selecionar explicitamente as entregas em T-001 e aprovar seus contratos. T-008 e T-009 só implementam canais selecionados; propostas complementares exigem decisão e atualização dos contratos. |
| R-005 | As consultas podem apresentar histórico desatualizado ou parcial, pois dependem do job agendado. | A avaliar | O consumidor interpretar ausência local como cotação inexistente. | Resolver D-003 e definir a representação de disponibilidade e os estados do canal selecionado, conforme T-008 e, se aplicável, T-009. Documentar os limites de atualização sem introduzir coleta sob demanda. |
| R-006 | Diagramas, referências oficiais e orientações aplicáveis podem permanecer incompletos ou não validados. | A avaliar | Implementação baseada em artefatos não verificáveis ou fluxos históricos superados. | Resolver a parte pendente de D-010 e validar D-011 e D-013, conforme T-002, T-003 e T-012. Usar os ADRs já disponíveis; não reabrir a stack nem seguir diagramas de coleta sob demanda superados pela RN-011. |

## Tratamento das dúvidas e riscos

- As decisões devem registrar resultado, responsável pela aprovação e evidências ou referências aplicáveis. Atualizar os contratos e ADRs afetados antes ou junto da implementação, mantendo a rastreabilidade com requisitos, critérios e tarefas.
- A resolução de uma dúvida técnica deve definir o comportamento e sua verificação. A aprovação da solução não equivale à conclusão da implementação ou à execução dos testes.
- Uma proposta rejeitada ou entrega não selecionada deve ser registrada com sua justificativa e aplicabilidade, sem remover, reutilizar ou renumerar os IDs.
- Dúvidas bloqueantes impedem somente as tarefas que dependem delas. Levantamentos independentes podem prosseguir sem assumir respostas para as pendências.
- A prova de conceito não autoriza automaticamente uma solução de produção. A reconciliação continua condicionada à validação e à aprovação previstas em T-010.
- Avaliar os riscos e acompanhar as evidências das mitigações nas verificações de T-011. Não declarar um risco eliminado apenas porque sua mitigação consta do plano.

## Referências

- [Constituição do Projeto](00-constituicao.md)
- [Contexto](01-contexto.md)
- [Requisitos Funcionais](02-requisitos-funcionais.md)
- [Regras de Negócio](03-regras-de-negocio.md)
- [Requisitos Não Funcionais](04-requisitos-nao-funcionais.md)
- [Critérios de Aceite](05-criterios-de-aceite.md)
- [Plano Técnico](06-plano-tecnico.md)
- [Tarefas](07-tarefas.md)
- [ADR-001: Adoção de Java 25 e Quarkus 3.40 LTS](../adr/adr-001.md)
- [ADR-002: Ingestão agendada com persistência local](../adr/adr-002.md)
- [Primeira versão provisória das dúvidas e riscos](../provisorio/contratos/08-duvidas-e-riscos.md)
