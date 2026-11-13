# 07 · Tarefas

## Situação das tarefas

- O backlog segue os contratos oficiais e o Plano Técnico. Sua consolidação não autoriza a implementação de propostas ou a escolha de parâmetros pendentes por suposição.
- Pendente de decisão ou definição: tarefa destinada a esclarecer, validar e registrar as escolhas necessárias antes da implementação correspondente.
- Pendente de implementação: tarefa prevista, mas ainda não executada e dependente das aprovações e definições indicadas.
- Fora do escopo: tarefa histórica não selecionada; preservar o ID sem executá-la no MVP.
- Proposta: tarefa cuja implementação depende de validação técnica e aprovação explícita do grupo.
- Os doze IDs da primeira versão provisória foram preservados. A numeração não determina a ordem de execução; as dependências e condições de cada tarefa devem ser respeitadas.
- O ADR-001 define Java 25, Quarkus 3.40 LTS e a stack complementar. O ADR-002 define API obrigatória, ausência de frontend e operação em um único processo JVM com H2 em arquivo. T-002 detalha os artefatos ainda pendentes, sem reabrir essas escolhas.

## Tarefas

| ID | Tarefa e resultado esperado | Requisitos e critérios relacionados | Dependências | Situação |
| --- | --- | --- | --- | --- |
| T-001 | Registrar o MVP com coleta agendada, persistência H2 e API interna obrigatória somente de leitura. Alinhar os contratos e ADRs, excluindo página web e acesso direto ao banco como canal de consumo e preservando seus IDs históricos. | RF-001, RF-003, RF-005, RF-006; RF-007, CA-010 e CA-011 somente como registros fora do escopo | Decisão do grupo de 2026-11-13. | Concluída na definição de escopo e no alinhamento documental; não equivale à implementação ou ao aceite da API. |
| T-002 | Localizar e incorporar os diagramas C4 de níveis 1 e 2 e detalhar pacotes, configuração por ambiente, caminho e volume do H2, backup, retenção e implantação. Respeitar um aplicativo Quarkus em um processo JVM, com job e API separados logicamente, Docker e Docker Compose, sem compartilhar o arquivo H2 entre processos ou réplicas. | RF-001, RF-003, RF-005; Constituição e Plano Técnico | T-001 concluída; localização dos diagramas pode ser antecipada. | Parcialmente definida; topologia aprovada, detalhamento e C4 pendentes. |
| T-003 | Recuperar e validar as URLs oficiais completas da API PTAX e dos exemplos BACEN. Validar consultas de USD e EUR por MicroProfile REST Client com Jackson, incluindo moeda, data, tipo de boletim e distinção entre falha técnica e ausência. Registrar evidências reais e payloads de referência para os testes com WireMock, sem substituir a validação do BACEN por mocks. | RF-001, RF-002, RN-004, RN-009; CA-001, CA-002 | Acesso às referências oficiais e à API PTAX; não depende do detalhamento da implantação. | Pendente de validação. |
| T-004 | Resolver a divergência de horários e aprovar fuso, calendário, exceções, borda das três horas, limites de execução e recuperação após reinício. Definir timeouts do cliente, parâmetros SmallRye Fault Tolerance, campos e destino dos logs JSON, severidade, avisos e escalonamento, sem alterar a RN-005. Definir alvos operacionais e métodos de medição sem assumir valores numéricos. Atualizar os contratos aplicáveis. | RF-001, RF-004, RN-003, RN-005, RN-007, RNF-001, RNF-004, RNF-006, RNF-007; CA-003, CA-004, CA-005 | Decisão do grupo e da equipe operacional; escopo já definido em T-001. | Pendente de definição; tecnologias de agendamento e observabilidade aprovadas. |
| T-005 | Definir e aprovar modelo físico, precisão e escala de BigDecimal, restrições e estratégia de persistência sem duplicidade no H2 em arquivo. Detalhar migrações Flyway, entidades JPA e repositories Panache e o estado necessário ao controle e à retomada das pendências por data, moeda e tipo de boletim. | RF-003, RF-004, RN-008, RNF-002, RNF-006; CA-001, CA-005 | T-002 para configuração do arquivo e volume; T-003 para os dados retornados; T-004 para o estado exigido pela recuperação. | Pendente de definição; H2 em arquivo, Panache Repository e Flyway aprovados. |
| T-006 | Preparar o único aplicativo Java 25 / Quarkus 3.40 LTS com Maven 3.9.16 e Wrapper, fixando o patch do BOM e versões externas compatíveis. Configurar H2 em arquivo, repositories Panache, Flyway e validação do esquema, DTOs em records, Jackson e Hibernate Validator. Implementar coleta por Quarkus Scheduler e MicroProfile REST Client, validação do boletim e persistência de compra e venda. Preparar Docker / Compose com volume persistente. Não armazenar boletins intermediários nem concluir itens sem obter e armazenar o boletim esperado. Incluir as verificações aplicáveis. | RF-001, RF-002, RF-003, RN-001, RN-002, RN-003, RN-004, RNF-001, RNF-002; CA-001, CA-002 | T-001, T-002, T-003, T-004, T-005 e T-012; critérios aplicáveis aprovados. | Pendente de implementação. |
| T-007 | Implementar controles independentes de pendência, tentativas a cada cinco minutos nas primeiras três horas e tentativas horárias depois, conforme a borda e os limites aprovados. Recuperar dias anteriores sem bloquear as coletas atuais, registrar falhas e avisos e retomar após reinício preservando data, moeda e boletim. Encerrar somente o item obtido e armazenado, sem duplicidade, incluindo as verificações aplicáveis. | RF-004, RN-005, RN-006, RN-007, RN-008, RNF-003, RNF-004, RNF-006; CA-002, CA-003, CA-004, CA-005 | T-004, T-005 e T-006; critérios de tentativas, aviso e recuperação aprovados. | Pendente de implementação. |
| T-008 | Definir e aprovar filtros, disponibilidade, DTOs de resposta, erros e status HTTP da API obrigatória. Aprovar provedor OIDC, perfis e credenciais; implementar Quarkus REST, Jackson, Hibernate Validator e autorização por perfis, documentando com SmallRye OpenAPI. Consultar somente dados locais, sem escrita de negócio. Selecionar consumidor HTTP autorizado de teste. Aplicar rejeição de datas futuras somente se CA-009 for aprovado. Não implementar acesso direto ao banco ou página web. | RF-005, RF-006, RN-010, RN-011, RNF-005; CA-006, CA-007 e CA-009, se a regra for aprovada | T-001 concluída; T-002, T-005, T-006 e T-012 para implementar; contrato e critérios aplicáveis aprovados. | Obrigatória; contrato e configuração de segurança pendentes antes da implementação. |
| T-009 | Tarefa histórica de página web, frontend e gráficos, preservada sem implementação. | RF-007; CA-010, ambos fora do escopo | Não aplicável ao MVP definido em T-001. | Fora do escopo; não executar. |
| T-010 | Executar a prova de conceito de `CotacaoMoedaPeriodo` com USD e EUR em intervalo conhecido e registrar completude, tipos de boletim e resultados. Submeter período, estratégia e fluxo à aprovação ou rejeição do grupo. Somente se aprovado, implementar e verificar a reconciliação agendada das pendências com os registros locais por moeda, data e tipo, sem tratar resultado vazio ou falha técnica como prova de dia sem publicação. | RF-008, RN-009; CA-008 | T-003 para a prova de conceito; T-005, T-007 e T-012 somente para a implementação aprovada; aprovação do fluxo e de CA-008. | Proposta; implementação condicionada à validação e à aprovação. |
| T-011 | Consolidar testes rastreáveis com JUnit 5, QuarkusTest, REST Assured, Mockito, WireMock e H2. Verificar migrações Flyway, idempotência, serialização, validação, acesso autorizado e durabilidade com H2 em arquivo nos testes de reinício. Comprovar ausência de chamadas ao BACEN nas consultas, inclusive sem dados locais. Verificar logs JSON, SmallRye Health e métricas e preparar GitHub Actions com Maven Wrapper. Registrar resultados e lacunas sem aprovar cenários pendentes. | RF-001 a RF-006, RN-001 a RN-011 e RNF-001 a RNF-007; CA-001 a CA-007. RF-008 / CA-008 e CA-009 somente após aprovação. RF-007, CA-010 e CA-011 fora do escopo | T-006, T-007 e T-008 para as verificações do MVP; implementação de T-010 somente se aprovada. | Pendente; verificação incremental, sem testes de frontend ou canal direto ao banco. |
| T-012 | Confirmar a versão final do Golden Path, as orientações aplicáveis e como serão observadas no fluxo SDD com LLM. Registrar as definições na Constituição e no plano técnico antes de iniciar as tarefas de implementação com LLM. | Constituição e Plano Técnico | Acesso à referência vigente e validação do grupo. | Pendente de definição. |

## Rastreabilidade com o plano

| Requisito funcional | Parte do Plano Técnico | Tarefas principais | Critérios de aceite |
| --- | --- | --- | --- |
| RF-001 | Coleta agendada | T-003, T-004, T-006 | CA-001 |
| RF-002 | Identificação e validação do boletim | T-003, T-006, T-007 | CA-001, CA-002 |
| RF-003 | Dados conhecidos e persistência em H2 | T-005, T-006 | CA-001 |
| RF-004 | Pendências, novas tentativas e recuperação | T-004, T-005, T-007 | CA-002, CA-003, CA-004, CA-005 |
| RF-005 | Consulta exclusivamente local pela API | T-008 | CA-006; CA-011 fora do escopo |
| RF-006 | API obrigatória somente de leitura | T-008 | CA-007; CA-009 somente se a regra de datas futuras for aprovada |
| RF-007 | Página web fora do escopo | T-009, histórica e não aplicável | CA-010, fora do escopo |
| RF-008 | Reconciliação agendada proposta | T-010, com implementação somente após aprovação | CA-008 |

T-011 consolida a verificação das tarefas de implementação aplicáveis. Os requisitos não funcionais se relacionam às seguintes tarefas:

- RNF-001: T-004, T-006 e T-011.
- RNF-002: T-005, T-006, T-007 e T-011.
- RNF-003: T-007 e T-011.
- RNF-004: T-004, T-007 e T-011.
- RNF-005: T-008 e T-011, API obrigatória.
- RNF-006: T-004, T-005, T-007 e T-011.
- RNF-007: T-004 para aprovar alvos e métodos de medição e T-011 para verificar os compromissos acordados.

## Condições de execução e conclusão

- As decisões devem atualizar os contratos e ADRs correspondentes antes ou junto da implementação. Dúvidas bloqueantes devem ser registradas no contrato de [Dúvidas e Riscos](08-duvidas-e-riscos.md) e submetidas ao grupo.
- Levantamentos e validações podem ser antecipados quando não dependem de decisões pendentes. A ausência de dependência entre tarefas não elimina a necessidade de aprovação do resultado.
- Antes de executar uma tarefa de código, detalhar os arquivos e componentes afetados conforme a organização aprovada em T-002 e os dados definidos em T-005. Não inventar pacotes, classes, contratos de API ou tecnologias para preencher as lacunas do backlog.
- As verificações aplicáveis devem acompanhar cada implementação. T-011 consolida as evidências; não adia todos os testes para o final.
- O detalhamento do contrato HTTP não bloqueia verificações independentes da coleta, mas a entrega do MVP exige também a API de T-008. Página web e acesso direto ao banco como canal de consumo estão fora do escopo; a reconciliação não é pré-requisito da coleta.
- A prova de conceito de T-010 não equivale à aprovação do fluxo nem autoriza automaticamente sua implementação.
- Uma tarefa só deve ser marcada como concluída após produzir seu resultado esperado e as evidências de verificação aplicáveis. Itens não selecionados devem ser registrados como não aplicáveis ao escopo, sem reutilizar ou renumerar seus IDs.

## Pontos a confirmar

- Aprovações: o escopo e a stack estão definidos; aprovar os detalhes de contrato HTTP, regras e critérios ainda pendentes antes de liberar as implementações dependentes.
- Arquitetura e dados: detalhar pacotes, ambiente, arquivo e volume H2, backup, retenção, modelo físico e precisão monetária, sem reabrir a topologia aprovada ou assumir padrões de ferramentas.
- Operação: resolver agendamento, recuperação, avisos, escalonamento e metas de produção, incluindo os métodos de medição.
- API e reconciliação: aprovar contrato HTTP, provedor OIDC, perfis, aplicabilidade de datas futuras e resultado da validação de `CotacaoMoedaPeriodo`.
- Execução: atribuir responsáveis e detalhar as tarefas de código após as decisões bloqueantes, preservando os vínculos com requisitos, critérios e plano.

## Referências

- [Constituição do Projeto](00-constituicao.md)
- [Contexto](01-contexto.md)
- [Requisitos Funcionais](02-requisitos-funcionais.md)
- [Regras de Negócio](03-regras-de-negocio.md)
- [Requisitos Não Funcionais](04-requisitos-nao-funcionais.md)
- [Critérios de Aceite](05-criterios-de-aceite.md)
- [Plano Técnico](06-plano-tecnico.md)
- [Dúvidas e Riscos](08-duvidas-e-riscos.md)
- [ADR-001: Adoção de Java 25 e Quarkus 3.40 LTS](../adr/adr-001.md)
- [ADR-002: Ingestão agendada com persistência local](../adr/adr-002.md)
