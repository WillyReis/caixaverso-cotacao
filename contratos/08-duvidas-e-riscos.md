# 08 · Dúvidas e Riscos

## Situação do registro

- Aberta: dúvida que ainda depende de decisão, definição ou validação. Seu registro não autoriza a implementação de uma proposta ou a escolha de parâmetros por suposição.
- Parcialmente resolvida: parte da dúvida já possui evidência ou decisão registrada, mas o restante continua pendente.
- Resolvida: decisão registrada nos contratos e ADRs; não significa implementação ou testes concluídos.
- Fora do escopo: dúvida histórica de entrega não selecionada; preservar o ID sem bloquear o MVP. As propostas não aprovadas também não bloqueiam entregas independentes.
- Os IDs D-001 a D-013 e R-001 a R-006 da primeira versão provisória foram preservados. D-014 a D-020 complementam o registro com pendências já descritas nos contratos oficiais, sem criar novas entregas.
- Os responsáveis são indicados por papel, conforme o material do grupo. A atribuição nominal de responsáveis pelas decisões e tarefas ainda deve ser confirmada.
- As probabilidades dos riscos permanecem a avaliar. Não foram acordadas classificações numéricas ou níveis de aceitação, e uma mitigação planejada não significa que o risco já foi eliminado.

## Decisões já registradas

- Java 25 e Quarkus 3.40 LTS estão definidos no [ADR-001](../adr/adr-001.md), já localizado. Sua disponibilidade não permanece como dúvida aberta; a incorporação dos diagramas C4 ainda está pendente em D-010.
- Quarkus REST, Hibernate ORM com Panache Repository, MicroProfile REST Client, Quarkus Scheduler e Flyway foram definidos pelo grupo em 2026-11-13. As escolhas complementares delegadas à IA priorizam Quarkus e SmallRye, conforme a Constituição e o Plano Técnico.
- DTOs em records, entidades JPA em classes, mapeamento explícito, Jackson e Hibernate Validator estão definidos; Lombok e MapStruct não fazem parte do MVP.
- SmallRye Fault Tolerance, SmallRye OpenAPI, Quarkus OIDC, logs JSON do Quarkus, SmallRye Health, Micrometer / Prometheus, ferramentas de teste e GitHub Actions estão definidos. Parâmetros operacionais, contrato HTTP e configuração de segurança permanecem pendentes.
- H2 substitui PostgreSQL conforme a Constituição e o [ADR-002 consolidado](../adr/adr-002.md). O MVP usa H2 embarcado em arquivo persistente, com job e API no mesmo processo JVM, Docker / Compose e volume persistente. Caminho, ambiente, backup, retenção e modelo físico continuam pendentes.
- Coleta, persistência e API interna somente de leitura são as três entregas do MVP. Não haverá página web, frontend, gráficos ou acesso direto ao banco como canal de consumo. RF-007, CA-010, CA-011 e T-009 estão fora do escopo, com IDs preservados.
- O escopo cambial inicial contempla USD e EUR, com compra e venda dos boletins de abertura e encerramento, sem os boletins intermediários das 11h e 12h.
- A ingestão é assíncrona e agendada. Os consumidores consultam somente dados locais, e a ausência de dados não inicia consulta síncrona ao BACEN nem coleta sob demanda.
- Valores monetários usam `BigDecimal`. Isso não define automaticamente precisão, escala, tipos físicos no H2 ou formato de exposição.

## Dúvidas

| ID | Dúvida | Responsável | Critério de resolução | Situação |
| --- | --- | --- | --- | --- |
| D-001 | Quais entregas compõem a primeira versão? | Grupo | Coleta agendada, persistência H2 e API interna somente de leitura registradas no contexto, requisitos, critérios, plano e tarefas, conforme T-001. | Resolvida em 2026-11-13; não haverá página web. |
| D-002 | A API somente leitora descrita no ADR-002 será entregue na primeira versão? | Grupo | ADR-002 e contratos alinhados: API obrigatória e único canal de consulta do produto, conforme T-001 e T-008. | Resolvida em 2026-11-13. |
| D-003 | Qual é o contrato da API: filtros, formato, campo e valores de disponibilidade, dados parciais, erros e status HTTP? | Grupo e equipe técnica | Aprovar o contrato HTTP e o consumidor autorizado de teste antes da implementação, conforme T-008, CA-006 e CA-007. Acesso direto ao banco não é um canal de consumo. | Parcialmente resolvida; canal API definido, detalhes HTTP pendentes. |
| D-004 | Quais horários, calendário e identificador de fuso devem ser usados nas coletas, incluindo dias úteis, feriados e exceções? | Grupo e equipe técnica | Resolver a divergência entre 10h/13h e 9h/16h registrada no contexto. Aprovar horários, fuso e calendário e detalhar a verificação de independência do fuso do host, conforme T-004 e RNF-001. | Aberta. |
| D-005 | Como recuperar pendências e tempos de tentativa após reinício ou indisponibilidade do job? | Equipe técnica | Aprovar a estratégia de preservação ou reconstrução do estado no H2 em arquivo, mantendo data, moeda e tipo de boletim. Detalhar o teste de retomada sem perda ou duplicidade, conforme T-004, T-005 e RNF-006; executá-lo em T-007 e T-011. Quarkus Scheduler não persiste pendências de negócio automaticamente. | Aberta; tecnologia e armazenamento definidos, estado e retomada pendentes. |
| D-006 | Quais limites, timeouts, campos, destino e escalonamento serão usados para falhas e pendências prolongadas? Haverá limite final de tentativas? | Grupo e equipe operacional | Aprovar parâmetros do REST Client e SmallRye Fault Tolerance, campos e destino dos logs JSON, severidade e mecanismo dos avisos e eventual encerramento ou escalonamento. Definir a borda das três horas conforme T-004, RN-005, RN-007 e RNF-004, sem substituir a política por retries imediatos. | Parcialmente resolvida; stack definida, parâmetros e avisos pendentes. |
| D-007 | `CotacaoMoedaPeriodo` retorna dados completos e tipos de boletim adequados para reconciliar USD e EUR? Qual período e estratégia devem ser usados? | Equipe técnica | Executar a prova de conceito em intervalo conhecido e registrar completude, tipos e resultados, distinguindo resposta vazia de erro técnico. Submeter o fluxo à aprovação ou rejeição do grupo antes da implementação, conforme T-010, RF-008 e CA-008. | Aberta; reconciliação proposta. |
| D-008 | Como comprovar datas sem publicação sem confundir feriados com atraso ou falha? Existe fonte confiável além do recurso de período? | Grupo e equipe técnica | Validar e aprovar as fontes e os critérios de evidência aplicáveis, conforme T-003 e T-004; considerar T-010 somente se a reconciliação for aprovada. A restrição da RN-009 permanece obrigatória: resposta vazia ou falha técnica não comprova, isoladamente, ausência de publicação. | Aberta; restrição de classificação já confirmada. |
| D-009 | O critério histórico de rejeitar consultas por datas futuras será aprovado para a API obrigatória? Qual resposta deve ser apresentada? | Grupo | Aprovar ou retirar CA-009 e, se aprovado, definir a referência da data atual e a resposta observável, incluindo status e mensagem, conforme T-008. | Aberta; depende da aprovação da regra, não da seleção da API. |
| D-010 | Os diagramas C4 de níveis 1 e 2 mencionados pelo grupo estão disponíveis e incorporados à documentação? | Grupo | O ADR-001 já está localizado. Localizar, versionar e incorporar os diagramas, reconciliando-os com os ADRs aceitos e com o MVP de coleta, H2 e API, conforme T-002. | Parcialmente resolvida; ADR-001 disponível, incorporação dos C4 pendente. |
| D-011 | Quais são as URLs oficiais completas da API PTAX e dos exemplos BACEN? | Grupo e equipe técnica | Recuperar, validar e registrar referências não truncadas e as evidências das consultas de USD e EUR, conforme T-003. | Aberta. |
| D-012 | Quais limites de desempenho, disponibilidade, volume, retenção e crescimento são necessários para o MVP de coleta, persistência e API? | Grupo | Aprovar alvos e métodos de medição antes de fechar os requisitos de produção, conforme T-004 e RNF-007. As verificações dos compromissos acordados devem ser consolidadas em T-011. | Aberta; nenhum alvo numérico acordado. |
| D-013 | Qual é a versão final do Golden Path e quais orientações se aplicam ao projeto e ao uso de LLM no fluxo SDD? | Grupo e equipe técnica | Validar a referência vigente e registrar as orientações aplicáveis na Constituição e no plano antes de iniciar tarefas de implementação com LLM, conforme T-012. | Aberta. |

## Pendências complementares dos contratos oficiais

| ID | Dúvida | Responsável | Critério de resolução | Situação |
| --- | --- | --- | --- | --- |
| D-014 | Qual modelo físico, precisão, escala, restrições e estratégia de persistência no H2 assegurarão a integridade e a ausência de duplicidade? A resposta original do BACEN será armazenada? | Equipe técnica | Aprovar modelo, representação física de BigDecimal, restrições e estratégia dos repositories Panache e migrações Flyway, materializando data, moeda e tipo de boletim sem duplicidade. Registrar a decisão sobre a resposta original, conforme T-005, RN-008 e RNF-002. | Aberta; H2 em arquivo, Panache Repository, Flyway e identificação lógica definidos. |
| D-015 | Como detalhar pacotes, configuração por ambiente, arquivo e volume H2, backup, retenção e implantação da topologia escolhida? | Grupo e equipe técnica | Detalhar em T-002 e T-005 o único aplicativo Quarkus em processo JVM para job e API, com Docker / Compose e volume persistente. Não compartilhar o arquivo H2 entre processos ou réplicas; mudanças de topologia exigem revisão do ADR-002. | Parcialmente resolvida; topologia e modo H2 definidos, detalhamento operacional pendente. |
| D-016 | Qual provedor OIDC, perfis autorizados e configuração de credenciais serão usados pela API obrigatória? | Grupo e equipe técnica | Aprovar provedor, perfis e gestão de credenciais para Quarkus OIDC por bearer token, conforme T-008 e RNF-005, e verificar consumidores autorizados e não autorizados em T-011. Não há login interativo no escopo. | Parcialmente resolvida; tecnologia definida, configuração de segurança pendente. |
| D-017 | Qual versão do Maven será fixada e como validar o build com Java 25 e Quarkus 3.40 LTS? | Equipe técnica | Maven 3.9.16 escolhido e registrado na Constituição, ADR-001 e plano. Fixar Wrapper, patch do BOM, plugins e versões externas e comprovar compatibilidade no build, conforme T-006 e T-011. | Parcialmente resolvida; versão Maven definida, configuração e validação efetiva pendentes. |
| D-018 | Haverá página web, frontend ou gráficos neste projeto? | Grupo | Registrar ausência de página web e manter RF-007, CA-010 e T-009 fora do escopo, sem implementação ou testes de interface. | Resolvida em 2026-11-13; entrega fora do escopo. |
| D-019 | A proposta de incluir Selic será aprovada? Qual série seria utilizada? | Grupo | Registrar aprovação ou rejeição no contexto. Se aprovada, validar a série e detalhar requisitos, critérios, plano e tarefas antes da implementação; a série 1178 citada no material não constitui decisão aprovada. | Aberta; proposta fora do escopo cambial aprovado. |
| D-020 | É necessária carga inicial do histórico desde 01/01/2020, como proposto na segunda versão provisória? | Grupo e equipe técnica | Registrar a decisão no contexto. Se aprovada, definir período, estratégia, critérios e tarefas da carga inicial antes de implementá-la, sem tratá-la como consequência automática da coleta agendada. | Aberta; proposta de carga inicial. |

## Riscos

| ID | Risco | Probabilidade | Impacto | Mitigação ou contingência |
| --- | --- | --- | --- | --- |
| R-001 | A publicação do boletim pode atrasar ou não ocorrer na janela esperada. | A avaliar | Histórico incompleto ou atrasado para os consumidores. | Manter pendências independentes, aplicar as tentativas e a recuperação aprovadas e comunicar pendências prolongadas sem coleta sob demanda, conforme RN-005 a RN-007 e T-007. Resolver D-004 e D-006 para os parâmetros operacionais. |
| R-002 | Uma resposta vazia ou falha técnica pode ser confundida com feriado ou dia sem publicação. | A avaliar | Lacunas silenciosas ou classificação incorreta do histórico. | Aplicar RN-009 independentemente da aprovação da reconciliação e não inferir ausência sem evidência. Resolver D-008 e validar `CotacaoMoedaPeriodo` em D-007 e T-010 somente para o fluxo proposto. |
| R-003 | Reinício, perda do volume H2, recuperação entre dias ou repetição de respostas pode perder ou duplicar registros. | A avaliar | Histórico e controle de pendências inconsistentes. | Resolver D-005, D-014 e o detalhamento de D-015; preservar o arquivo H2, aplicar Flyway sem recriar dados e definir idempotência e retomada. Verificar reinício com H2 em arquivo e reprocessamento, conforme RN-008, RNF-002, RNF-006, T-005, T-007 e T-011. |
| R-004 | Reintroduzir página web, acesso direto ao banco ou API de escrita sem aprovação pode ampliar o escopo definido. | A avaliar | Atraso e escopo ambíguo para o fluxo SDD com LLM. | D-001, D-002 e D-018 estão resolvidas. T-008 implementa a API obrigatória; RF-007, CA-010, CA-011 e T-009 permanecem fora do escopo. Propostas complementares exigem aprovação e atualização dos contratos. |
| R-005 | As consultas podem apresentar histórico desatualizado ou parcial, pois dependem do job agendado. | A avaliar | O consumidor interpretar ausência local como cotação inexistente. | Resolver D-003 e definir disponibilidade e estados da API, conforme T-008. Documentar limites de atualização sem coleta sob demanda; Health e métricas não demonstram, sozinhos, completude do histórico. |
| R-006 | Diagramas, referências oficiais, orientações e compatibilidade do build podem permanecer incompletos ou não validados. | A avaliar | Implementação baseada em artefatos não verificáveis, dependências incompatíveis ou fluxos históricos superados. | Resolver D-010, D-011, D-013 e a validação de D-017, conforme T-002, T-003, T-006, T-011 e T-012. Usar os ADRs aceitos; não reabrir a stack ou seguir coleta sob demanda superada pela RN-011. |

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
