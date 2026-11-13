<!--
Relatorio de sincronizacao
Versao: sem versionamento explicito -> 1.0.0 (primeira versao documental).
Alteracoes: stack complementar, API obrigatoria, ausencia de pagina web e operacao do MVP.
Secao adicionada: Governanca.
Propagacao para ADRs, contexto, requisitos, criterios, plano, tarefas, riscos e README: concluida.
IDs historicos preservados; frontend e acesso direto ao banco fora do escopo.
Verificacao: definicoes, links locais, tabelas, rastreabilidade e estados de tarefas.
Templates externos de migracao: nao aplicaveis ao projeto novo; estrutura local preservada.
Template externo de tarefas citado pelo skill ausente; backlog local revisado sem dependencia dele.
Pendente: TODO(RATIFICATION_DATE), confirmar a data original de ratificacao da constituicao.
-->

# 00 · Constituição do Projeto

Versão documental: 1.0.0. Última alteração: 2026-11-13.
Ratificação original: TODO(RATIFICATION_DATE): confirmar a data de adoção inicial pelo grupo.

## Objetivo

Disponibilizar uma base local reutilizável de histórico de cotações cambiais do BACEN por uma API interna somente de leitura, alimentada por coleta agendada. O projeto não terá página web.

## Stack

- Linguagem: Java 25 (JDK 25)
- Framework: Quarkus 3.40 LTS; fixar o patch do BOM e validar a compatibilidade das dependências ao preparar o build
- Build: Maven 3.9.16, fixado com Maven Wrapper
- Implementação REST: Quarkus REST, sem RESTEasy Classic
- Persistência: Hibernate ORM com Panache no padrão Repository, por `PanacheRepository` ou `PanacheRepositoryBase` conforme a chave aprovada
- Banco de dados: H2 embarcado em arquivo persistente, acessado pelo único processo da aplicação
- Evolução do esquema: Flyway; Hibernate valida o esquema, sem recriá-lo na operação normal
- Cliente HTTP BACEN: MicroProfile REST Client, com a implementação Quarkus REST Client
- Agendamento: Quarkus Scheduler, sem Quartz no MVP
- Objetos: DTOs com `records` do Java; entidades JPA com classes; mapeamentos explícitos, sem Lombok ou MapStruct no MVP
- JSON: Jackson, integrado ao Quarkus REST e ao REST Client
- Validação: Jakarta Bean Validation com Hibernate Validator
- Resiliência: SmallRye Fault Tolerance, sem substituir a política de tentativas agendadas das regras de negócio
- Documentação da API: SmallRye OpenAPI, seguindo OpenAPI 3; Swagger UI somente em desenvolvimento e testes
- Segurança: Quarkus OIDC para autenticação por bearer token e autorização por perfis; provedor, perfis e credenciais a aprovar no contrato da API
- Observabilidade: logs JSON do Quarkus, SmallRye Health e Micrometer com exportação Prometheus; destino dos registros e alertas operacionais a definir
- Cache: nenhum por enquanto
- Testes: JUnit 5, QuarkusTest, REST Assured, Mockito e WireMock; usar H2 nos testes de persistência
- Execução: JVM; um único aplicativo Quarkus contém o job e a API, com responsabilidades lógicas separadas
- Containers: Docker, com volume persistente para o arquivo H2; Docker Compose para execução local
- Integração contínua: GitHub Actions, executando o build e os testes pelo Maven Wrapper

Priorizar extensões oficiais do Quarkus e implementações SmallRye quando houver opção adequada. Bibliotecas complementares atendem somente responsabilidades não cobertas por essas opções. Versões gerenciadas pelo BOM não devem ser sobrescritas sem justificativa e validação; versões externas devem ser fixadas no build.

## Padrões obrigatórios

- Todos os valores monetários devem ser representados com `BigDecimal`.
- APIs seguem o padrão REST e são documentadas com Swagger / OpenAPI 3.
- A ingestão do BACEN deve ser assíncrona, agendada e independente das consultas dos consumidores.
- Consumidores consultam somente os dados persistidos localmente, por meio da API interna obrigatória do MVP. Acesso direto ao banco não é um canal de consumo do produto.
- DTOs do BACEN, entidades persistidas e DTOs da API possuem responsabilidades distintas; entidades JPA não são expostas diretamente como contrato HTTP.
- O agendamento independente não exige persistência reativa. Hibernate ORM com Panache e JDBC executam fora do event loop.
- A recuperação das pendências de negócio deve preservar ou reconstruir seu estado no H2; o Scheduler não fornece persistência automática dessas pendências.
- Requisitos, regras e critérios de aceite devem possuir IDs estáveis e ser rastreáveis até o código e os testes.

## Escopo inicial

- Moedas: dólar (USD) e euro (EUR), com valores de compra e venda dos boletins de abertura e encerramento.
- Entregas: coleta agendada, persistência local em H2 e API REST interna somente de leitura.
- Não haverá página web, frontend ou gráficos. A API é o único canal de consulta do produto.
- Os boletins intermediários das 11h e 12h não fazem parte do armazenamento inicial.

## Proibições

- Consultar o BACEN sincronamente ou iniciar coleta sob demanda em resposta a uma consulta de consumidor, inclusive quando não houver dados locais.
- Implementar página web, gerar endpoints CRUD de escrita ou disponibilizar o banco como alternativa à API de consulta.
- Misturar extensões Quarkus REST com RESTEasy Classic ou adotar Hibernate Reactive para o H2 escolhido.
- Apagar ou recriar o esquema e os dados do H2 na inicialização normal da aplicação.
- Implementar propostas ou pendências sem aprovação, ou preencher decisões de comportamento, contrato ou tecnologia por suposição.

## Desenvolvimento orientado a especificação (SDD) com LLM

1. Os contratos aprovados são a referência para planejar e implementar o projeto. O material original do grupo e os ADRs fornecem contexto e evidências.
2. O plano e as tarefas devem referenciar os IDs dos requisitos e critérios de aceite aplicáveis.
3. Decisões aprovadas, propostas e dúvidas devem permanecer explicitamente diferenciadas. A LLM implementa somente decisões aprovadas.
4. Conflitos entre documentos e dúvidas bloqueantes devem ser registrados no documento de dúvidas e riscos e submetidos à decisão do grupo antes de codificar.
5. Cada implementação deve incluir verificação rastreável aos critérios de aceite. Mudanças aprovadas devem atualizar os contratos antes ou junto da alteração de código.

## Governança

- As escolhas de stack de 2026-11-13 incorporam as definições explícitas do grupo e as escolhas complementares delegadas à IA. Essa delegação não aprova parâmetros operacionais, regras de negócio ou contratos HTTP ainda pendentes.
- Alterações exigem atualização dos ADRs e contratos afetados, preservação dos IDs e verificação de consistência e rastreabilidade.
- A versão documental segue versionamento semântico: major para mudanças incompatíveis de princípios, minor para novas diretrizes e patch para esclarecimentos sem mudança de comportamento.
- Revisões e implementações devem verificar aderência à Constituição, aos ADRs aceitos e aos critérios de aceite aplicáveis.

## Decisões e referências

- Java 25, Quarkus 3.40 LTS e a stack complementar seguem o [ADR-001](../adr/adr-001.md).
- H2, coleta agendada, API obrigatória somente de leitura, ausência de frontend e operação em um único processo seguem o [ADR-002](../adr/adr-002.md).
- O patch do BOM, a versão aplicável do Golden Path, o ambiente de implantação, o contrato da API, a configuração de segurança, o modelo físico e os parâmetros operacionais permanecem pendentes nos documentos correspondentes. A stack escolhida não define esses comportamentos por padrão.