# 00 · Constituição do Projeto

## Objetivo

Disponibilizar uma base local reutilizável de histórico de cotações cambiais do BACEN para consultas e consumo por aplicações internas.

## Stack

- Linguagem: Java 25 (JDK 25)
- Framework: Quarkus 3.40 LTS
- Build: Maven (versão estável a fixar no início da implementação)
- Banco de dados: H2
- Cache: nenhum por enquanto
- Testes: JUnit 5
- Containers: sem definição no momento

## Padrões obrigatórios

- Todos os valores monetários devem ser representados com `BigDecimal`.
- APIs seguem o padrão REST e são documentadas com Swagger / OpenAPI 3.
- A ingestão do BACEN deve ser assíncrona, agendada e independente das consultas dos consumidores.
- Consumidores consultam somente os dados persistidos localmente.
- Requisitos, regras e critérios de aceite devem possuir IDs estáveis e ser rastreáveis até o código e os testes.

## Escopo inicial

- Moedas: dólar (USD) e euro (EUR), com valores de compra e venda dos boletins de abertura e encerramento.
- Os boletins intermediários das 11h e 12h não fazem parte do armazenamento inicial.

## Proibições

- Consultar o BACEN sincronamente ou iniciar coleta sob demanda em resposta a uma consulta de consumidor, inclusive quando não houver dados locais.
- Implementar propostas ou pendências sem aprovação, ou preencher decisões de comportamento, contrato ou tecnologia por suposição.

## Desenvolvimento orientado a especificação (SDD) com LLM

1. Os contratos aprovados são a referência para planejar e implementar o projeto. O material original do grupo e os ADRs fornecem contexto e evidências.
2. O plano e as tarefas devem referenciar os IDs dos requisitos e critérios de aceite aplicáveis.
3. Decisões aprovadas, propostas e dúvidas devem permanecer explicitamente diferenciadas. A LLM implementa somente decisões aprovadas.
4. Conflitos entre documentos e dúvidas bloqueantes devem ser registrados no documento de dúvidas e riscos e submetidos à decisão do grupo antes de codificar.
5. Cada implementação deve incluir verificação rastreável aos critérios de aceite. Mudanças aprovadas devem atualizar os contratos antes ou junto da alteração de código.

## Decisões e referências

- Java 25 e Quarkus 3.40 LTS seguem o [ADR-001](../provisorio/adr/adr-001.md), já localizado no repositório.
- H2 é o banco definido pelo grupo e substitui a escolha de PostgreSQL registrada no [ADR-002](../provisorio/adr/adr-002.md). Permanecem válidas as decisões de ingestão assíncrona agendada, leitura local e ausência de coleta sob demanda. A referência ao banco deverá ser atualizada na consolidação desse ADR.
- A seleção das entregas iniciais, a versão aplicável do Golden Path, a topologia de implantação, o contrato da API e os parâmetros operacionais permanecem pendentes de validação nos documentos correspondentes.