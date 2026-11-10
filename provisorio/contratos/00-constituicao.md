# Constituicao do Projeto

**Status:** contrato inicial derivado do material do grupo. Itens marcados como proposta ou pendentes precisam de validacao antes da implementacao.

## Objetivo

Construir uma base local reutilizavel de historico de cotacoes cambiais do BACEN para consultas, visualizacoes e possiveis aplicacoes internas consumidoras.

## Principios

- A coleta do BACEN e agendada e independente das consultas dos consumidores.
- Consumidores leem dados persistidos localmente; uma consulta nao inicia coleta sob demanda.
- Cada requisito, regra e criterio de aceite possui identificador estavel e deve ser rastreavel ate o codigo e os testes.
- Decisoes confirmadas, propostas e duvidas devem permanecer explicitamente diferenciadas.
- Nenhuma pessoa ou LLM deve preencher uma decisao pendente por suposicao.

## Restricoes conhecidas

- Escopo inicial de dados: dolar (USD) e euro (EUR), valores de compra e venda dos boletins de abertura e encerramento.
- Os boletins intermediarios das 11h e 12h nao fazem parte do armazenamento inicial.
- Selic nao esta confirmada e nao integra o escopo inicial.
- O armazenamento local em PostgreSQL e a ingestao assincrona agendada constam como decisao aceita no [ADR-002](../adr/adr-002.md).
- O material do grupo registra Java 25 e Quarkus 3.40 LTS como padrao aceito (ADR-001), mas esse ADR ainda nao foi localizado no repositorio; confirmar e incorporar o registro.

## Desenvolvimento orientado a especificacao (SDD) com LLM

1. Estes contratos sao a referencia para planejar e implementar o projeto. O material original do grupo e os ADRs fornecem contexto e evidencias.
2. O plano e as tarefas devem referenciar os IDs de requisitos e criterios de aceite aplicaveis.
3. A LLM implementa somente decisoes aprovadas. Itens propostos ou pendentes nao autorizam escolhas de comportamento, contrato ou tecnologia.
4. Se houver conflito entre documentos, ou uma pendencia que bloqueie comportamento, registrar a duvida em `08-duvidas-e-riscos.md` e solicitar decisao antes de codificar.
5. Cada implementacao deve incluir verificacao rastreavel aos criterios de aceite. Mudancas aprovadas de escopo atualizam os contratos antes ou junto da alteracao de codigo.

## Registro de decisoes

- **Aceito:** ingestao assincrona agendada, PostgreSQL local, leitura a partir dos dados armazenados e nenhuma coleta sincrona provocada por consulta do usuario (ADR-002).
- **A confirmar:** selecao das tres entregas iniciais entre as quatro possibilidades de `01-contexto.md` e a versao final do Golden Path, incluindo quais orientacoes se aplicam a este projeto e ao fluxo SDD com LLM.
- **A confirmar:** ADR-001 completo, topologia de implantacao, contrato da API, parametros operacionais e demais itens listados em `08-duvidas-e-riscos.md`.