# 07 · Tarefas

## Situação das tarefas

- O backlog segue os contratos oficiais e o Plano Técnico. Sua consolidação não autoriza a implementação de propostas ou a escolha de parâmetros pendentes por suposição.
- Pendente de decisão ou definição: tarefa destinada a esclarecer, validar e registrar as escolhas necessárias antes da implementação correspondente.
- Pendente de implementação: tarefa prevista, mas ainda não executada e dependente das aprovações e definições indicadas.
- Condicional: tarefa aplicável somente se a entrega for selecionada e seus contratos forem aprovados.
- Proposta: tarefa cuja implementação depende de validação técnica e aprovação explícita do grupo.
- Os doze IDs da primeira versão provisória foram preservados. A numeração não determina a ordem de execução; as dependências e condições de cada tarefa devem ser respeitadas.
- O ADR-001 já foi localizado e a stack está definida. T-002 trata dos artefatos e detalhes de arquitetura ainda pendentes, sem reabrir a escolha de Java 25 e Quarkus 3.40 LTS.

## Tarefas

| ID | Tarefa e resultado esperado | Requisitos e critérios relacionados | Dependências | Situação |
| --- | --- | --- | --- | --- |
| T-001 | Aprovar a base funcional e selecionar as três entregas iniciais entre as quatro opções do contexto. Reconciliar a responsabilidade da API no ADR-002 com o escopo escolhido e registrar quais canais e critérios condicionais se aplicam, atualizando os contratos correspondentes. | RF-001, RF-003, RF-005, RF-006, RF-007; CA-006, CA-007, CA-010, CA-011 | Decisão do grupo. | Pendente de decisão. |
| T-002 | Localizar e incorporar os diagramas C4 de níveis 1 e 2, alinhando-os aos ADRs já disponíveis. Definir e aprovar no plano técnico a organização dos componentes, a topologia de processos e containers, a configuração e a forma de acesso ao H2 para as entregas selecionadas, sem presumir processos separados ou um mecanismo de compartilhamento do banco. | RF-001, RF-003, RF-005; Constituição e Plano Técnico | T-001 para a definição final da arquitetura da entrega; localização dos diagramas pode ser antecipada. | Pendente de definição. |
| T-003 | Recuperar e validar as URLs oficiais completas da API PTAX e dos exemplos BACEN. Validar consultas de USD e EUR para abertura e encerramento, incluindo identificação de moeda, data e tipo de boletim e distinção entre falha técnica e ausência do boletim. Registrar as referências e evidências de integração no plano técnico. | RF-001, RF-002, RN-004, RN-009; CA-001, CA-002 | Acesso às referências oficiais e à API PTAX; não depende da definição da topologia. | Pendente de validação. |
| T-004 | Resolver a divergência de horários e aprovar fuso, calendário, exceções, borda das três horas, limites de execução, recuperação após reinício e mecanismos de registro, aviso e escalonamento. Definir também os alvos operacionais da entrega e seus métodos de medição, sem assumir valores numéricos. Atualizar regras, requisitos não funcionais, critérios e plano com as decisões aplicáveis. | RF-001, RF-004, RN-003, RN-005, RN-007, RNF-001, RNF-004, RNF-006, RNF-007; CA-003, CA-004, CA-005 | Decisão do grupo e da equipe operacional; T-001 para os alvos da entrega selecionada. | Pendente de definição. |
| T-005 | Definir e aprovar no plano técnico o modo de operação e armazenamento do H2, o modelo físico, a precisão e a escala dos valores representados com `BigDecimal`, as restrições e a estratégia de persistência sem duplicidade. Definir o estado necessário ao controle e à retomada das pendências por data, moeda e tipo de boletim. | RF-003, RF-004, RN-008, RNF-002, RNF-006; CA-001, CA-005 | T-002 para topologia e acesso ao banco; T-003 para os dados retornados; T-004 para o estado exigido pela recuperação. | Pendente de definição. |
| T-006 | Preparar a estrutura de implementação aprovada com Java 25, Quarkus 3.40 LTS, Maven e H2, fixando a versão estável do Maven no início da implementação. Implementar a coleta agendada de abertura e encerramento para USD e EUR, a validação do boletim e a persistência de compra e venda. Não armazenar boletins intermediários nem concluir itens sem o boletim esperado obtido e armazenado. Incluir as verificações aplicáveis. | RF-001, RF-002, RF-003, RN-001, RN-002, RN-003, RN-004, RNF-001, RNF-002; CA-001, CA-002 | T-001, T-002, T-003, T-004, T-005 e T-012; critérios aplicáveis aprovados. | Pendente de implementação. |
| T-007 | Implementar controles independentes de pendência, tentativas a cada cinco minutos nas primeiras três horas e tentativas horárias depois, conforme a borda e os limites aprovados. Recuperar dias anteriores sem bloquear as coletas atuais, registrar falhas e avisos e retomar após reinício preservando data, moeda e boletim. Encerrar somente o item obtido e armazenado, sem duplicidade, incluindo as verificações aplicáveis. | RF-004, RN-005, RN-006, RN-007, RN-008, RNF-003, RNF-004, RNF-006; CA-002, CA-003, CA-004, CA-005 | T-004, T-005 e T-006; critérios de tentativas, aviso e recuperação aprovados. | Pendente de implementação. |
| T-008 | Definir e aprovar o contrato do canal de consumo selecionado antes de implementá-lo, com leitura exclusivamente local e resposta explícita para ausência ou parcialidade. Se API, definir filtros, disponibilidade, formato, erros, status HTTP, autenticação e autorização e documentar em OpenAPI 3. Se acesso direto ao H2, aprovar a forma de acesso e o consumidor interno de teste. Aplicar a rejeição de datas futuras somente se CA-009 for aprovado. Implementar e verificar somente os canais selecionados. | RF-005, RF-006, RN-010, RN-011, RNF-005; CA-006, CA-007, CA-009, CA-011, conforme o canal aprovado | T-001 para selecionar o canal; T-002, T-005 e T-012 para implementá-lo; contrato e critérios aplicáveis aprovados. | Condicional; contrato pendente antes da implementação. |
| T-009 | Se a página web for selecionada, definir e aprovar interface, tecnologia, visualização e estados de ausência, parcialidade e erro. Implementar a consulta do histórico de USD ou EUR por período pelo canal aprovado, sem iniciar coleta sob demanda, e incluir as verificações aplicáveis. | RF-007, RN-010; CA-010 | T-001 para selecionar a página; T-008 para o canal de consulta; interface e critérios aprovados. | Condicional à seleção da página. |
| T-010 | Executar a prova de conceito de `CotacaoMoedaPeriodo` com USD e EUR em intervalo conhecido e registrar completude, tipos de boletim e resultados. Submeter período, estratégia e fluxo à aprovação ou rejeição do grupo. Somente se aprovado, implementar e verificar a reconciliação agendada das pendências com os registros locais por moeda, data e tipo, sem tratar resultado vazio ou falha técnica como prova de dia sem publicação. | RF-008, RN-009; CA-008 | T-003 para a prova de conceito; T-005, T-007 e T-012 somente para a implementação aprovada; aprovação do fluxo e de CA-008. | Proposta; implementação condicionada à validação e à aprovação. |
| T-011 | Consolidar os testes automatizados e de integração rastreáveis aos requisitos, regras e critérios aprovados, usando JUnit 5 conforme a Constituição. Verificar também os requisitos não funcionais aplicáveis e comprovar que consultas dos consumidores não chamam o BACEN nem iniciam coleta, inclusive quando faltam dados locais. Registrar resultados e lacunas sem declarar cenários pendentes ou não selecionados como aprovados. | RF-001 a RF-008, RN-001 a RN-011, RNF-001 a RNF-007 e CA-001 a CA-011, somente conforme o escopo e os critérios aprovados | Implementações a verificar: T-006 e T-007, se entregues; T-008, T-009 e a etapa de implementação de T-010 somente se selecionadas ou aprovadas. | Pendente; verificação incremental das entregas aplicáveis. |
| T-012 | Confirmar a versão final do Golden Path, as orientações aplicáveis e como serão observadas no fluxo SDD com LLM. Registrar as definições na Constituição e no plano técnico antes de iniciar as tarefas de implementação com LLM. | Constituição e Plano Técnico | Acesso à referência vigente e validação do grupo. | Pendente de definição. |

## Rastreabilidade com o plano

| Requisito funcional | Parte do Plano Técnico | Tarefas principais | Critérios de aceite |
| --- | --- | --- | --- |
| RF-001 | Coleta agendada | T-003, T-004, T-006 | CA-001 |
| RF-002 | Identificação e validação do boletim | T-003, T-006, T-007 | CA-001, CA-002 |
| RF-003 | Dados conhecidos e persistência em H2 | T-005, T-006 | CA-001 |
| RF-004 | Pendências, novas tentativas e recuperação | T-004, T-005, T-007 | CA-002, CA-003, CA-004, CA-005 |
| RF-005 | Consulta exclusivamente local | T-008 | CA-006; CA-011 somente com acesso direto selecionado e autorizado |
| RF-006 | API somente de leitura | T-008, se a API for selecionada | CA-007; CA-009 somente se a regra de datas futuras for aprovada |
| RF-007 | Página web pelo canal aprovado | T-009, se a página for selecionada | CA-010 |
| RF-008 | Reconciliação agendada proposta | T-010, com implementação somente após aprovação | CA-008 |

T-011 consolida a verificação das tarefas de implementação aplicáveis. Os requisitos não funcionais se relacionam às seguintes tarefas:

- RNF-001: T-004, T-006 e T-011.
- RNF-002: T-005, T-006, T-007 e T-011.
- RNF-003: T-007 e T-011.
- RNF-004: T-004, T-007 e T-011.
- RNF-005: T-008 e T-011, somente se a API for selecionada.
- RNF-006: T-004, T-005, T-007 e T-011.
- RNF-007: T-004 para aprovar alvos e métodos de medição e T-011 para verificar os compromissos acordados.

## Condições de execução e conclusão

- As decisões devem atualizar os contratos e ADRs correspondentes antes ou junto da implementação. Dúvidas bloqueantes devem ser registradas no contrato de [Dúvidas e Riscos](08-duvidas-e-riscos.md) e submetidas ao grupo.
- Levantamentos e validações podem ser antecipados quando não dependem de decisões pendentes. A ausência de dependência entre tarefas não elimina a necessidade de aprovação do resultado.
- Antes de executar uma tarefa de código, detalhar os arquivos e componentes afetados conforme a organização aprovada em T-002 e os dados definidos em T-005. Não inventar pacotes, classes, contratos de API ou tecnologias para preencher as lacunas do backlog.
- As verificações aplicáveis devem acompanhar cada implementação. T-011 consolida as evidências; não adia todos os testes para o final.
- API, acesso direto ao banco, página web e reconciliação não são pré-requisitos da coleta apenas por constarem deste backlog. Suas dependências condicionais não bloqueiam a validação das entregas que não as utilizam.
- A prova de conceito de T-010 não equivale à aprovação do fluxo nem autoriza automaticamente sua implementação.
- Uma tarefa só deve ser marcada como concluída após produzir seu resultado esperado e as evidências de verificação aplicáveis. Itens não selecionados devem ser registrados como não aplicáveis ao escopo, sem reutilizar ou renumerar seus IDs.

## Pontos a confirmar

- Aprovações: validar o escopo, os contratos e os critérios aplicáveis antes de liberar as tarefas de implementação.
- Arquitetura e dados: concluir a organização dos componentes, a topologia e as definições de operação, armazenamento e acesso ao H2, sem substituir as decisões por padrões de ferramentas.
- Operação: resolver agendamento, recuperação, avisos, escalonamento e metas de produção, incluindo os métodos de medição.
- Canais e reconciliação: registrar a seleção dos canais, a aplicabilidade de datas futuras e o resultado da validação de `CotacaoMoedaPeriodo`.
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
- [Primeira versão provisória das tarefas](../provisorio/contratos/07-tarefas.md)
