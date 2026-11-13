# 02 · Requisitos Funcionais

## Situação dos requisitos

- Confirmado: comportamento registrado em decisão aceita ou definição explícita do grupo. Detalhes ainda pendentes permanecem indicados e não devem ser escolhidos por suposição.
- Proposto: requisito sujeito à validação técnica e à aprovação do grupo. Seu registro não autoriza a implementação antes da aprovação.
- Fora do escopo: requisito histórico não selecionado; o ID é preservado somente para rastreabilidade, sem implementação ou aceite no MVP.
- Os IDs da primeira versão provisória foram preservados para manter a rastreabilidade entre requisitos, regras, critérios de aceite, tarefas e testes.

## Requisitos

| ID | Requisito funcional | Situação | Observações |
| --- | --- | --- | --- |
| RF-001 | O sistema deve executar consultas agendadas à API PTAX do BACEN para coletar cotações de USD e EUR dos boletins de abertura e encerramento. | Confirmado; horários e calendário pendentes | Usar Quarkus Scheduler e MicroProfile REST Client. Horários, fuso e calendário devem ser confirmados conforme as divergências registradas no contexto. |
| RF-002 | O sistema deve reconhecer se a resposta corresponde ao boletim esperado para a moeda e a data do item coletado. | Confirmado | A resposta de outro tipo de boletim não conclui o item. O boletim não deve ser identificado apenas pelo horário da consulta. |
| RF-003 | O sistema deve persistir localmente em H2 os valores de compra e venda obtidos para os boletins de abertura e encerramento. | Confirmado | H2 embarcado em arquivo, com Hibernate ORM com Panache Repository e esquema versionado com Flyway. Os boletins intermediários das 11h e 12h não integram o escopo inicial. Modelo físico, precisão e escala permanecem pendentes. |
| RF-004 | O sistema deve controlar cada pendência por data, moeda e tipo de boletim, executar novas tentativas e recuperar itens de dias anteriores sem bloquear as coletas do dia atual. | Confirmado como comportamento; detalhes operacionais pendentes | O controle independente, as novas tentativas e a recuperação de pendências devem seguir as regras RN-004 a RN-008. |
| RF-005 | O sistema deve permitir que consumidores consultem dados já persistidos exclusivamente pela API interna, sem solicitar coleta ao BACEN. | Confirmado; canal API definido | Filtros e contrato HTTP precisam ser aprovados. A ausência de dados não inicia coleta sob demanda. Acesso direto ao H2 não é um canal de consumo do produto. |
| RF-006 | A API interna obrigatória do MVP deve retornar registros do banco local e informar dados ausentes ou parciais por um campo de disponibilidade. | Confirmado; contrato HTTP pendente | Usar Quarkus REST, DTOs próprios em records, Jackson e SmallRye OpenAPI. Nome e valores do campo, formato da resposta, filtros, perfis autorizados, erros e status HTTP permanecem pendentes. |
| RF-007 | Apresentação do histórico em página web. | Fora do escopo; ID histórico preservado | O projeto não terá página web, frontend ou gráficos. A consulta pertence exclusivamente à API interna. |
| RF-008 | Se a reconciliação for validada e aprovada, o sistema deve consultar `CotacaoMoedaPeriodo` em rotina agendada, identificar os boletins retornados e comparar as pendências com os registros locais. | Proposto; sujeito à validação técnica e à aprovação do grupo | Avaliar o recurso por prova de conceito antes de implementar. Ausência de resultado não comprova, isoladamente, feriado ou dia sem publicação; falhas técnicas devem permanecer distintas da ausência de boletim, conforme RN-009. |

## Rastreabilidade

Os vínculos abaixo utilizam os IDs do [contrato oficial de critérios de aceite](05-criterios-de-aceite.md). A situação e as condições de aplicação de cada cenário constam desse contrato; parâmetros pendentes e propostas continuam sujeitos à validação e à aprovação do grupo.

- RF-001: CA-001.
- RF-002: CA-001 e CA-002.
- RF-003: CA-001.
- RF-004: CA-002, CA-003, CA-004 e CA-005.
- RF-005: CA-006; CA-011 permanece somente como referência histórica fora do escopo.
- RF-006: CA-007; CA-009 depende da aprovação da regra de consulta por data futura, não da seleção da API.
- RF-007: CA-010, ambos fora do escopo e mantidos somente para rastreabilidade.
- RF-008: CA-008, somente após validação técnica e aprovação da reconciliação pelo grupo.

## Pontos a confirmar

- Agendamento: resolver a divergência de horários registrada no contexto e definir fuso, calendário e exceções de execução.
- Operação: definir os detalhes das tentativas, da recuperação de pendências e dos avisos antes de implementar os comportamentos dependentes dessas decisões.
- Consulta: aprovar filtros, configuração OIDC, perfis autorizados, representação de disponibilidade, respostas de erro e tratamento de datas futuras na API obrigatória.
- Reconciliação: validar a completude e os tipos de boletim retornados por `CotacaoMoedaPeriodo` antes de aprovar seu uso.

## Referências

- [Constituição do Projeto](00-constituicao.md)
- [Contexto](01-contexto.md)
- [ADR-002: Ingestão agendada com persistência local](../adr/adr-002.md)
- [ADR-001: Stack do projeto](../adr/adr-001.md)