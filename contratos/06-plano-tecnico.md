# 06 · Plano Técnico

## Situação do plano

- A arquitetura-base segue a Constituição do Projeto e os ADRs aceitos. Detalhes pendentes e propostas não devem ser escolhidos implicitamente durante a implementação.
- As entregas condicionais só entram na implementação após a seleção pelo grupo e a aprovação dos contratos correspondentes.
- Este plano organiza responsabilidades, fluxos e dependências. O detalhamento das tarefas pertence ao contrato 07, a consolidar a partir dessas definições e dos critérios de aceite aplicáveis.

## Stack e restrições

- Linguagem: Java 25, conforme o ADR-001 já localizado no repositório.
- Framework: Quarkus 3.40 LTS.
- Build: Maven, com versão estável a fixar no início da implementação.
- Persistência: H2, substituindo PostgreSQL conforme a decisão do grupo e o ADR-002 consolidado.
- Valores monetários: `BigDecimal`. A representação física no banco e o formato de exposição ainda precisam ser definidos.
- Testes: JUnit 5, com verificações rastreáveis aos critérios de aceite aprovados.
- APIs: REST, documentadas com Swagger / OpenAPI 3, quando essa entrega for selecionada.
- Cache: nenhum por enquanto.
- Processos, containers e implantação: sem topologia definida. Não presumir que APP (job) e APP (API) serão processos ou containers separados.

## Arquitetura e responsabilidades

| Elemento | Responsabilidade | Situação |
| --- | --- | --- |
| API PTAX do BACEN | Fornecer os boletins usados como origem das cotações de USD e EUR. | Origem definida; URLs oficiais e comportamento das consultas precisam ser validados na integração. |
| APP (job) | Executar a coleta agendada, identificar o boletim esperado, persistir compra e venda e controlar pendências e novas tentativas. | Responsabilidade definida; horários, calendário e detalhes operacionais pendentes. |
| Banco local H2 | Armazenar os dados usados pelos consumidores, independentemente de chamadas ao BACEN no momento da consulta. | Banco definido; modo de operação e armazenamento, modelo físico e forma de acesso pendentes. |
| APP (API) | Atuar somente como leitor das cotações locais e comunicar ausência ou parcialidade por um campo de disponibilidade. | Responsabilidade registrada no ADR-002; entrega inicial e contrato da API pendentes. |
| Página web | Apresentar o histórico disponível pelo canal de consulta aprovado, sem iniciar coleta. | Condicional à seleção da página; interface, tecnologia e visualização pendentes. |

A topologia final, a organização dos componentes, o compartilhamento de configuração e a forma de acesso ao H2 devem ser definidos antes da implementação dependente dessas escolhas. As responsabilidades descritas não fixam módulos, processos, containers ou um mecanismo de compartilhamento do banco.

## Fluxos

### Coleta agendada

1. Iniciar as tentativas de abertura e encerramento nos horários, fuso e calendário aprovados, sem depender de requisições dos consumidores.
2. Consultar a API PTAX para USD e EUR e identificar o tipo de boletim retornado, sem deduzi-lo apenas pelo horário da consulta.
3. Validar a moeda, a data e o tipo de boletim esperado para o item. Não armazenar os boletins intermediários das 11h e 12h no escopo inicial.
4. Persistir localmente em H2 os valores de compra e venda e concluir somente o item cujo boletim foi obtido e armazenado. O reprocessamento não deve criar registros duplicados.
5. Se a consulta falhar ou não retornar o boletim esperado, manter o item pendente. Falha técnica e ausência do boletim devem permanecer distintas, sem registrar sucesso de coleta.
6. Aplicar a RN-005: novas tentativas a cada cinco minutos nas primeiras três horas desde o início das tentativas do item e, depois, de hora em hora até obter o boletim. A borda exata da transição, o calendário e os limites de execução continuam pendentes.
7. Manter controles independentes por data, moeda e tipo de boletim. Recuperar pendências de dias anteriores em fluxo agendado separado, preservando sua identificação, sem bloquear as coletas atuais, e registrar o aviso de pendência prolongada conforme o mecanismo aprovado.

Os mecanismos de agendamento, persistência e recuperação devem respeitar as definições aprovadas nas [Regras de Negócio](03-regras-de-negocio.md), sem assumir parâmetros operacionais ou estratégias físicas por padrão.

### Consulta de consumidores

- Consultar somente os registros persistidos localmente. A consulta permanece independente da coleta e da disponibilidade do BACEN naquele momento.
- A ausência de dados não autoriza chamada síncrona ao BACEN nem coleta sob demanda.
- Se a API for selecionada, definir filtros, autorização, formato da resposta, campo e valores de disponibilidade, dados parciais, erros e status HTTP antes de implementá-la. A API é somente leitora.
- Se o acesso direto ao banco for selecionado e autorizado, definir a forma de consulta e o consumidor interno de teste antes de disponibilizar esse acesso.
- Se a página web for selecionada, definir o canal de consulta, a interface e os estados de ausência, parcialidade e erro antes da implementação.
- A rejeição de consultas por data futura depende da aprovação de CA-009; não deve ser incorporada como validação implícita.

### Reconciliação de pendências

O uso de `CotacaoMoedaPeriodo` em uma rotina agendada é uma proposta, não uma solução já aprovada para recuperação ou classificação de datas.

Antes de implementar esse fluxo:

1. Validar o recurso com USD e EUR em um intervalo conhecido, verificando completude e tipos de boletim retornados.
2. Definir o período, a estratégia e o tratamento dos resultados e submeter a proposta à aprovação do grupo.
3. Se aprovado, comparar os boletins retornados com os registros locais por moeda, data e tipo de boletim, conforme RF-008 e CA-008.

O retorno do boletim esperado pode comprovar sua publicação. Uma resposta vazia ou sem esse boletim não comprova, isoladamente, feriado ou dia sem publicação. Falhas técnicas devem continuar distintas da ausência de boletim, conforme RN-009, independentemente da aprovação da reconciliação.

## Dados conhecidos

- Cada registro representa os valores de compra e venda de uma moeda para uma data e um tipo de boletim.
- A identificação lógica por data, moeda e tipo de boletim orienta o controle independente de pendências e a prevenção de duplicidade. Ela não define, por si só, a chave física ou a estratégia de persistência.
- Os valores monetários devem usar `BigDecimal`, conforme a Constituição do Projeto. Precisão, escala, tipos físicos no H2 e formato de exposição permanecem pendentes.
- O modelo físico, as restrições e a estratégia de persistência ou upsert devem ser aprovados antes da implementação correspondente.
- O modo de operação e armazenamento do H2, a política de retenção e o armazenamento da resposta original do BACEN ainda não foram definidos.

## Segurança e operação

- Configurar o agendamento no fuso aprovado, independentemente do fuso do host. O horário de Brasília é a referência da primeira versão provisória; identificador de fuso, horários, calendário, feriados e exceções ainda precisam de confirmação.
- Registrar falhas e pendências prolongadas, com formato, destino, severidade e mecanismo de aviso a definir. Eventual encerramento ou escalonamento de pendências depende de aprovação.
- Definir como preservar ou reconstruir pendências após reinício, mantendo data, moeda e tipo de boletim. O estado necessário aos tempos ou contadores de tentativas e o critério final de retomada também precisam ser definidos, conforme RNF-006.
- Se a API interna for entregue, definir autenticação, autorização, perfis e gestão de credenciais para permitir somente consumidores autorizados. Não presumir SSO.
- Definir os alvos de desempenho, disponibilidade, volume, retenção e crescimento e os métodos de medição antes de fechar os requisitos de produção. Nenhuma meta numérica foi acordada.

## Rastreabilidade e dependências

| Requisito | Parte do plano | Critérios de aceite | Dependências e condições |
| --- | --- | --- | --- |
| RF-001 | Coleta agendada | CA-001 | Seleção da entrega, validação das consultas PTAX e aprovação de horários, fuso e calendário. |
| RF-002 | Identificação e validação do boletim | CA-001, CA-002 | Validar na integração a identificação da moeda, data e tipo de boletim retornados. |
| RF-003 | Dados conhecidos e persistência em H2 | CA-001 | Aprovar modelo físico, precisão e escala, restrições e estratégia de persistência. |
| RF-004 | Controle de pendências, novas tentativas e recuperação | CA-002, CA-003, CA-004, CA-005 | Definir parâmetros operacionais, aviso, recuperação e persistência do estado necessário, respeitando RN-004 a RN-008. |
| RF-005 | Consulta exclusivamente local | CA-006, CA-011 | Selecionar o canal e aprovar seu contrato; CA-011 depende da seleção e autorização do acesso direto ao banco. |
| RF-006 | API somente de leitura | CA-007, CA-009 | Selecionar a API e aprovar seu contrato e segurança; CA-009 também depende da aprovação da regra de datas futuras. |
| RF-007 | Página web pelo canal aprovado | CA-010 | Selecionar a página e aprovar canal, interface, visualização e estados de consulta. |
| RF-008 | Reconciliação agendada proposta | CA-008 | Validar `CotacaoMoedaPeriodo`, definir período e estratégia e obter aprovação antes de implementar. |

A implementação da coleta depende das definições aplicáveis de integração, dados e operação. API, acesso direto ao banco, página web e reconciliação não se tornam pré-requisitos da coleta apenas por constarem deste plano.

Os requisitos não funcionais se relacionam às seguintes partes do plano:

- RNF-001: configuração de tempo e agendamento independente do fuso do host.
- RNF-002: identificação lógica, restrições físicas e persistência sem duplicidade.
- RNF-003: controles independentes e recuperação de pendências sem bloqueio das coletas atuais.
- RNF-004: registros de falhas e avisos de pendência prolongada.
- RNF-005: segurança da API, se selecionada.
- RNF-006: estado e recuperação após reinício.
- RNF-007: metas operacionais e métodos de medição ainda pendentes.

A verificação deve seguir os [Critérios de Aceite](05-criterios-de-aceite.md) e os [Requisitos Não Funcionais](04-requisitos-nao-funcionais.md), respeitando as condições e pendências de cada cenário. O contrato 07 deverá detalhar as tarefas e suas dependências sem tornar entregas não selecionadas obrigatórias.

## Decisões técnicas pendentes

- Selecionar as três entregas iniciais e reconciliar a responsabilidade da API no ADR-002 com o escopo escolhido.
- Definir os contratos dos canais de consumo, incluindo acesso direto ao banco, API ou página, quando selecionados.
- Aprovar horários, fuso, calendário, borda das três horas, limites de execução, recuperação após reinício, aviso e escalonamento.
- Definir modo de operação e armazenamento do H2, modelo físico, precisão e escala, restrições, estratégia de persistência e estado de pendências.
- Definir a topologia de processos e containers, implantação e compartilhamento de configuração e acesso aos dados.
- Validar tecnicamente `CotacaoMoedaPeriodo` e recuperar e validar as URLs oficiais completas da API PTAX e dos exemplos BACEN.
- Incorporar os diagramas C4 de níveis 1 e 2 mencionados pelo grupo. O ADR-001 já foi localizado e sua decisão de stack está registrada.
- Confirmar a versão final do Golden Path e quais orientações se aplicam ao projeto e ao fluxo SDD com LLM.
- Fixar a versão estável do Maven no início da implementação e definir as metas operacionais e respectivos métodos de medição.

## Referências

- [Constituição do Projeto](00-constituicao.md)
- [Contexto](01-contexto.md)
- [Requisitos Funcionais](02-requisitos-funcionais.md)
- [Regras de Negócio](03-regras-de-negocio.md)
- [Requisitos Não Funcionais](04-requisitos-nao-funcionais.md)
- [Critérios de Aceite](05-criterios-de-aceite.md)
- [ADR-001: Adoção de Java 25 e Quarkus 3.40 LTS](../adr/adr-001.md)
- [ADR-002: Ingestão agendada com persistência local](../adr/adr-002.md)
- [Primeira versão provisória do plano técnico](../provisorio/contratos/06-plano-tecnico.md)
