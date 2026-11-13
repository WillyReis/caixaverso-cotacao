# 06 · Plano Técnico

## Situação do plano

- A arquitetura-base segue a Constituição do Projeto e os ADRs aceitos. Detalhes pendentes e propostas não devem ser escolhidos implicitamente durante a implementação.
- Coleta agendada, persistência em H2 e API interna somente de leitura são entregas obrigatórias. Página web e acesso direto ao banco como canal de consumo estão fora do escopo; somente a reconciliação continua proposta e dependente de aprovação.
- Este plano organiza responsabilidades, fluxos e dependências. O detalhamento das tarefas pertence ao contrato 07, a consolidar a partir dessas definições e dos critérios de aceite aplicáveis.

## Stack e restrições

- Linguagem: Java 25, conforme o ADR-001 já localizado no repositório.
- Framework: Quarkus 3.40 LTS.
- Build: Maven 3.9.16 com Maven Wrapper. Fixar o patch do BOM Quarkus 3.40 e validar plugins e dependências no início da implementação; não sobrescrever versões gerenciadas pelo BOM sem justificativa.
- Persistência: H2 embarcado em arquivo persistente, substituindo PostgreSQL conforme o ADR-002. Usar Hibernate ORM com Panache no padrão Repository, não Hibernate Reactive.
- Valores monetários: `BigDecimal`. A representação física no banco e o formato de exposição ainda precisam ser definidos.
- Testes: JUnit 5, QuarkusTest, REST Assured, Mockito e WireMock, com verificações rastreáveis aos critérios de aceite aprovados e H2 nos testes de persistência.
- API: Quarkus REST, Jackson e SmallRye OpenAPI, seguindo OpenAPI 3. Não usar RESTEasy Classic ou gerar endpoints de escrita a partir das entidades.
- Cache: nenhum por enquanto.
- Processos e containers: um aplicativo Quarkus em um processo JVM para job e API; Docker com volume persistente para H2 e Docker Compose para execução local. Ambiente de implantação e parâmetros do volume permanecem pendentes.
- Integração contínua: GitHub Actions para build e testes com Maven Wrapper. Build nativo não faz parte do MVP.
- Não haverá página web, frontend ou gráficos; não há necessidade de stack de navegador ou de framework de interface.

### Extensões e bibliotecas

| Responsabilidade | Tecnologia | Extensão ou diretriz |
| --- | --- | --- |
| API REST e JSON | Quarkus REST com Jackson | `quarkus-rest-jackson` |
| Cliente BACEN | MicroProfile REST Client com Quarkus REST Client e Jackson | `quarkus-rest-client-jackson` |
| ORM e repositories | Hibernate ORM com Panache | `quarkus-hibernate-orm-panache`; usar `PanacheRepository` ou `PanacheRepositoryBase` conforme a chave aprovada |
| Conexão H2 | JDBC gerenciado pelo Quarkus | `quarkus-jdbc-h2` |
| Agendamento | Quarkus Scheduler | `quarkus-scheduler`; não usar Quartz no MVP |
| Evolução do banco | Flyway | `quarkus-flyway`; migrações versionadas e validação de esquema pelo Hibernate |
| DTOs e mapeamento | Java records e métodos explícitos | Sem Lombok ou MapStruct no MVP; entidades JPA permanecem classes |
| Validação | Jakarta Bean Validation / Hibernate Validator | `quarkus-hibernate-validator` |
| Resiliência técnica | SmallRye Fault Tolerance | `quarkus-smallrye-fault-tolerance`; parâmetros sujeitos à política aprovada |
| Documentação | SmallRye OpenAPI | `quarkus-smallrye-openapi`; Swagger UI somente em desenvolvimento e testes |
| Autenticação | Quarkus OIDC | `quarkus-oidc`; bearer token, sem página de login |
| Logs estruturados | Logging JSON do Quarkus | `quarkus-logging-json` |
| Saúde operacional | SmallRye Health | `quarkus-smallrye-health` |
| Métricas | Micrometer / Prometheus | `quarkus-micrometer-registry-prometheus` |
| Testes | JUnit 5, QuarkusTest, REST Assured, Mockito e WireMock | Fixar as versões e extensões de teste compatíveis no build; não presumir compatibilidade antes da validação |

Priorizar extensões oficiais do Quarkus e implementações SmallRye adequadas. CDI, transações e configuração utilizam os mecanismos integrados do Quarkus; não adicionar frameworks paralelos para essas responsabilidades. Versões externas não gerenciadas pelo BOM devem ser fixadas na preparação do build.

## Arquitetura e responsabilidades

| Elemento | Responsabilidade | Situação |
| --- | --- | --- |
| API PTAX do BACEN | Fornecer os boletins usados como origem das cotações de USD e EUR. | Origem definida; URLs oficiais e comportamento das consultas precisam ser validados na integração. |
| APP (job) | Executar a coleta com Quarkus Scheduler e MicroProfile REST Client, identificar o boletim, persistir compra e venda e controlar pendências. | Obrigatória; horários, calendário e detalhes operacionais pendentes. |
| Banco local H2 | Armazenar cotações e o estado necessário à recuperação, com acesso por repositories Panache e esquema versionado com Flyway. | H2 embarcado em arquivo definido; modelo físico, estado das pendências, backup e retenção pendentes. |
| APP (API) | Consultar somente dados locais com Quarkus REST, DTOs próprios e controle de acesso OIDC e comunicar ausência ou parcialidade. | Obrigatória; contrato HTTP, provedor e perfis autorizados pendentes. |
| Página web | Referência histórica de visualização do histórico. | Fora do escopo; não haverá frontend ou gráficos. |

Job e API executam no mesmo aplicativo e processo JVM, com serviços e repositories separados por responsabilidade. O acesso ao H2 usa a configuração e o datasource desse processo. Não compartilhar o arquivo entre processos ou réplicas; Docker utiliza um único container da aplicação e volume persistente. Pacotes, caminho do arquivo, configuração por ambiente e implantação devem ser detalhados antes da implementação dependente dessas escolhas.

### Objetos e persistência

- DTOs do BACEN representam o payload externo; entidades JPA representam os dados persistidos; DTOs da API em records representam o contrato de consulta. Não expor entidades JPA diretamente por HTTP.
- Jackson converte JSON e objetos. Mapeamentos explícitos convertem DTOs e entidades; Hibernate Validator aplica somente as validações aprovadas, sem transformar parâmetros pendentes em restrições implícitas.
- Panache Repository concentra consultas e operações de persistência. Os serviços de coleta definem os limites transacionais da escrita; a API não executa operações de escrita de negócio.
- Flyway aplica migrações versionadas. Na operação normal, Hibernate valida o esquema; não usar geração destrutiva de tabelas. Uma mudança de esquema não deve alterar implicitamente os DTOs da API.
- Hibernate ORM e JDBC são bloqueantes e devem executar fora do event loop. Coleta assíncrona significa execução agendada independente das requisições; não exige Hibernate Reactive, fila de mensagens ou programação reativa integral.

## Fluxos

### Coleta agendada

1. Usar Quarkus Scheduler para iniciar as tentativas de abertura e encerramento nos horários, fuso e calendário aprovados, sem depender de requisições dos consumidores.
2. Consultar a API PTAX por MicroProfile REST Client para USD e EUR e identificar o tipo de boletim retornado, sem deduzi-lo apenas pelo horário da consulta.
3. Validar a moeda, a data e o tipo de boletim esperado para o item. Não armazenar os boletins intermediários das 11h e 12h no escopo inicial.
4. Persistir localmente em H2 por repositories Panache os valores de compra e venda e concluir somente o item cujo boletim foi obtido e armazenado. O reprocessamento não deve criar registros duplicados.
5. Se a consulta falhar ou não retornar o boletim esperado, manter o item pendente. Falha técnica e ausência do boletim devem permanecer distintas, sem registrar sucesso de coleta.
6. Aplicar a RN-005: novas tentativas a cada cinco minutos nas primeiras três horas desde o início das tentativas do item e, depois, de hora em hora até obter o boletim. A borda exata da transição, o calendário e os limites de execução continuam pendentes.
7. Manter controles independentes por data, moeda e tipo de boletim. Recuperar pendências de dias anteriores em fluxo agendado separado, preservando sua identificação, sem bloquear as coletas atuais, e registrar o aviso de pendência prolongada conforme o mecanismo aprovado.

Os mecanismos de agendamento, persistência e recuperação devem respeitar as definições aprovadas nas [Regras de Negócio](03-regras-de-negocio.md), sem assumir parâmetros operacionais ou estratégias físicas por padrão.

SmallRye Fault Tolerance trata falhas técnicas conforme parâmetros aprovados. Não aplicar retries imediatos ou backoff padrão que substituam ou multipliquem a política da RN-005. Quarkus Scheduler dispara os fluxos, mas não preserva automaticamente o estado das pendências de negócio; sua persistência ou reconstrução deve atender RNF-006.

### Consulta de consumidores

- Consultar somente os registros persistidos localmente. A consulta permanece independente da coleta e da disponibilidade do BACEN naquele momento.
- A ausência de dados não autoriza chamada síncrona ao BACEN nem coleta sob demanda.
- A API é obrigatória e somente leitora. Definir filtros, autorização, formato da resposta, campo e valores de disponibilidade, dados parciais, erros e status HTTP antes de implementá-la.
- Autenticar consumidores com Quarkus OIDC por bearer token e aplicar os perfis autorizados. O provedor e a configuração de segurança dependem da aprovação do contrato; não implementar login interativo.
- Acesso direto ao banco não é um canal de consumo; página web e gráficos não serão implementados.
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
- O modo de operação é H2 embarcado em arquivo persistente com acesso por um único processo. Caminho do arquivo, volume por ambiente, backup, retenção e armazenamento da resposta original do BACEN ainda precisam ser definidos.

## Segurança e operação

- Configurar o agendamento no fuso aprovado, independentemente do fuso do host. O horário de Brasília é a referência da primeira versão provisória; identificador de fuso, horários, calendário, feriados e exceções ainda precisam de confirmação.
- Registrar falhas e pendências prolongadas em logs JSON do Quarkus. Campos, destino, severidade e mecanismo de aviso permanecem pendentes. SmallRye Health e Micrometer / Prometheus expõem saúde e métricas; não substituem os avisos da RN-007. Eventual encerramento ou escalonamento de pendências depende de aprovação.
- Definir como preservar ou reconstruir pendências após reinício, mantendo data, moeda e tipo de boletim. O estado necessário aos tempos ou contadores de tentativas e o critério final de retomada também precisam ser definidos, conforme RNF-006.
- A API interna obrigatória utiliza Quarkus OIDC por bearer token e autorização por perfis. Aprovar provedor, configuração, perfis e gestão de credenciais antes de implementá-la. Não presumir SSO interativo.
- Limitar Swagger UI a desenvolvimento e testes. Definir exposição e controle de acesso dos endpoints operacionais de Health e métricas no ambiente de implantação; sua adoção não autoriza exposição pública.
- Preservar o arquivo H2 no volume ao reiniciar o container e aplicar Flyway sem apagar dados. Definir backup, retenção e recuperação de falhas do armazenamento antes de assumir compromissos de produção.
- Definir os alvos de desempenho, disponibilidade, volume, retenção e crescimento e os métodos de medição antes de fechar os requisitos de produção. Nenhuma meta numérica foi acordada.

## Rastreabilidade e dependências

| Requisito | Parte do plano | Critérios de aceite | Dependências e condições |
| --- | --- | --- | --- |
| RF-001 | Coleta agendada | CA-001 | Validação das consultas PTAX e aprovação de horários, fuso e calendário. |
| RF-002 | Identificação e validação do boletim | CA-001, CA-002 | Validar na integração a identificação da moeda, data e tipo de boletim retornados. |
| RF-003 | Dados conhecidos e persistência em H2 | CA-001 | Aprovar modelo físico, precisão e escala, restrições e estratégia de persistência. |
| RF-004 | Controle de pendências, novas tentativas e recuperação | CA-002, CA-003, CA-004, CA-005 | Definir parâmetros operacionais, aviso, recuperação e persistência do estado necessário, respeitando RN-004 a RN-008. |
| RF-005 | Consulta exclusivamente local pela API | CA-006 | Aprovar o contrato HTTP e definir o consumidor autorizado de teste. CA-011 está fora do escopo. |
| RF-006 | API obrigatória somente de leitura | CA-007, CA-009 | Aprovar contrato, provedor OIDC e perfis; CA-009 depende somente da aprovação da regra de datas futuras. |
| RF-007 | Página web | CA-010 | Fora do escopo; requisito e critério históricos preservados sem implementação. |
| RF-008 | Reconciliação agendada proposta | CA-008 | Validar `CotacaoMoedaPeriodo`, definir período e estratégia e obter aprovação antes de implementar. |

A implementação da coleta depende das definições aplicáveis de integração, dados e operação. O contrato HTTP pode ser detalhado sem bloquear as verificações independentes da coleta, mas a entrega do MVP exige também a API. Página web e acesso direto como canal de consumo estão fora do escopo; a reconciliação não é pré-requisito da coleta.

Os requisitos não funcionais se relacionam às seguintes partes do plano:

- RNF-001: configuração de tempo e agendamento independente do fuso do host.
- RNF-002: identificação lógica, restrições físicas e persistência sem duplicidade.
- RNF-003: controles independentes e recuperação de pendências sem bloqueio das coletas atuais.
- RNF-004: registros de falhas e avisos de pendência prolongada.
- RNF-005: segurança da API obrigatória.
- RNF-006: estado e recuperação após reinício.
- RNF-007: metas operacionais e métodos de medição ainda pendentes.

A verificação deve seguir os [Critérios de Aceite](05-criterios-de-aceite.md) e os [Requisitos Não Funcionais](04-requisitos-nao-funcionais.md), respeitando as condições e pendências de cada cenário. O contrato 07 detalha as tarefas das entregas obrigatórias e mantém os itens fora do escopo e as propostas explicitamente diferenciados.

### Verificação da stack e do escopo

- Executar testes com Maven Wrapper no GitHub Actions, usando as versões aprovadas e verificando compatibilidade com Java 25 e o BOM Quarkus escolhido.
- Usar QuarkusTest e REST Assured para a API e WireMock para o BACEN; comprovar que consultas não fazem chamadas externas mesmo quando faltam dados locais.
- Testar repositories e migrações com H2. Para reinício, preservação de dados e retomada, usar H2 em arquivo temporário; banco em memória não comprova durabilidade.
- Verificar serialização Jackson, separação entre entidades e DTOs e validação dos filtros e dados conforme o contrato aprovado.
- Testar consumidores autorizados e não autorizados quando provedor e perfis forem aprovados. Não incluir testes de frontend ou de consulta direta ao banco como canal do produto.

## Decisões técnicas pendentes

- Aprovar o contrato da API obrigatória: filtros, disponibilidade, resposta parcial, erros, status HTTP, datas futuras e consumidor de teste.
- Aprovar provedor OIDC, perfis, configuração de credenciais e controle de acesso aos endpoints operacionais. A tecnologia de segurança já está definida.
- Aprovar horários, fuso, calendário, borda das três horas, limites de execução, recuperação após reinício, aviso e escalonamento.
- Definir modelo físico, precisão e escala, restrições, estratégia de persistência e estado de pendências no H2 em arquivo, com migrações Flyway.
- Detalhar pacotes, configuração por ambiente, caminho do arquivo H2, volume, backup, retenção e ambiente de implantação, respeitando a topologia de um único processo JVM aprovada no ADR-002.
- Validar tecnicamente `CotacaoMoedaPeriodo` e recuperar e validar as URLs oficiais completas da API PTAX e dos exemplos BACEN.
- Incorporar os diagramas C4 de níveis 1 e 2 mencionados pelo grupo. O ADR-001 já foi localizado e sua decisão de stack está registrada.
- Confirmar a versão final do Golden Path e quais orientações se aplicam ao projeto e ao fluxo SDD com LLM.
- Fixar o patch do BOM, plugins e versões externas e validar as extensões e os testes no build com Maven 3.9.16. Definir as metas operacionais e respectivos métodos de medição.

## Referências

- [Constituição do Projeto](00-constituicao.md)
- [Contexto](01-contexto.md)
- [Requisitos Funcionais](02-requisitos-funcionais.md)
- [Regras de Negócio](03-regras-de-negocio.md)
- [Requisitos Não Funcionais](04-requisitos-nao-funcionais.md)
- [Critérios de Aceite](05-criterios-de-aceite.md)
- [ADR-001: Adoção de Java 25 e Quarkus 3.40 LTS](../adr/adr-001.md)
- [ADR-002: Ingestão agendada com persistência local](../adr/adr-002.md)
