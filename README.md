# Caixaverso Cotacao

Trabalho final do Grupo 2 turma 2 do CAIXAVERSO

API interna somente de leitura do historico de cotacoes USD e EUR do BACEN PTAX, alimentada por coleta agendada e persistencia local. O projeto nao tera pagina web, frontend ou graficos.

## Fluxo do MVP

BACEN PTAX -> Quarkus Scheduler / MicroProfile REST Client -> H2 -> Quarkus REST -> consumidores internos autorizados.

Consultas usam exclusivamente dados locais. Dados ausentes ou parciais nao iniciam coleta sob demanda; acesso direto ao banco nao e um canal de consumo do produto.

## Stack definida

- Java 25, Quarkus 3.40 LTS e Maven 3.9.16 com Maven Wrapper.
- Quarkus REST, MicroProfile REST Client e Jackson.
- Hibernate ORM com Panache Repository, H2 embarcado em arquivo e Flyway.
- Quarkus Scheduler e SmallRye Fault Tolerance, respeitando a politica de tentativas dos contratos.
- DTOs com Java records, entidades JPA com classes, mapeamento explicito e Hibernate Validator.
- SmallRye OpenAPI; Swagger UI somente em desenvolvimento e testes.
- Quarkus OIDC por bearer token; provedor, perfis e credenciais ainda precisam de definicao.
- Logs JSON do Quarkus, SmallRye Health e Micrometer / Prometheus.
- JUnit 5, QuarkusTest, REST Assured, Mockito e WireMock.
- Um aplicativo em processo JVM; Docker / Compose com volume persistente para H2 e CI com GitHub Actions.

Priorizar extensoes Quarkus e implementacoes SmallRye adequadas. Nao compartilhar o arquivo H2 entre processos ou replicas.

## Documentacao

- [Constituicao do Projeto](contratos/00-constituicao.md)
- [Contexto e escopo](contratos/01-contexto.md)
- [Requisitos Funcionais](contratos/02-requisitos-funcionais.md)
- [Regras de Negocio](contratos/03-regras-de-negocio.md)
- [Requisitos Nao Funcionais](contratos/04-requisitos-nao-funcionais.md)
- [Criterios de Aceite](contratos/05-criterios-de-aceite.md)
- [Plano Tecnico e extensoes](contratos/06-plano-tecnico.md)
- [Tarefas](contratos/07-tarefas.md)
- [Duvidas e Riscos](contratos/08-duvidas-e-riscos.md)
- [ADR-001: Stack](adr/adr-001.md)
- [ADR-002: Coleta, persistencia e API](adr/adr-002.md)
- [Referencia local da API BACEN](api-cotaca-bacen-swagger.json)

## Estado do projeto

O repositorio esta na fase de definicao e revisao documental; a aplicacao ainda nao foi implementada. Contrato HTTP, horarios e calendario, modelo fisico e precisao monetaria, estado de pendencias, configuracao OIDC, backup, retencao e metas operacionais continuam pendentes conforme os contratos.

RF-007, CA-010, CA-011 e T-009 permanecem somente como registros historicos fora do escopo. A stack escolhida nao aprova implicitamente os comportamentos pendentes nem substitui sua validacao.
