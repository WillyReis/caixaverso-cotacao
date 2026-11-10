# Requisitos Funcionais

**Legenda:** Confirmado = consta de decisao aceita ou definicao explicita; Proposto = depende de validacao ou selecao do grupo.

| ID | Requisito | Situacao | Observacoes |
| --- | --- | --- | --- |
| RF-001 | Executar consultas agendadas a API PTAX do BACEN para coletar cotacoes de USD e EUR. O inicio previsto e as 10h para o boletim de abertura e as 13h para o de encerramento, horario de Brasilia. | Confirmado como direcao; calendario operacional pendente | ADR-002 confirma ingestao assincrona agendada. |
| RF-002 | Reconhecer se a resposta corresponde ao boletim esperado para a moeda e a data do item coletado. A resposta de outro boletim nao conclui o item. | Confirmado | Nao identificar o boletim apenas pelo horario da consulta. |
| RF-003 | Persistir localmente em PostgreSQL os valores de compra e venda obtidos para os boletins de abertura e encerramento. | Confirmado | Os intermediarios das 11h e 12h nao integram o escopo inicial. |
| RF-004 | Controlar cada pendencia por data, moeda e tipo de boletim, executar novas tentativas e recuperar itens de dias anteriores sem bloquear as coletas do dia atual. | Confirmado como comportamento; detalhes operacionais em aberto | Consultar as regras RN-004 a RN-008. |
| RF-005 | Permitir que consumidores consultem dados ja persistidos, sem solicitar coleta ao BACEN. | Confirmado como direcao | Forma de acesso, filtros e selecao da entrega precisam ser definidos. |
| RF-006 | Se a API de consulta for selecionada, retornar registros do banco local e informar dados ausentes ou parciais por um campo de disponibilidade. | Proposto | Nome, valores, formato da resposta, filtros, autorizacao e erros pendentes. |
| RF-007 | Se a pagina web for selecionada, apresentar o historico de USD ou EUR para um periodo escolhido, usando o canal de consulta aprovado. | Proposto | Visualizacao, tecnologia e estados de ausencia/erro pendentes. |
| RF-008 | Avaliar o uso de `CotacaoMoedaPeriodo` em uma rotina agendada de reconciliacao para identificar boletins retornados e comparar pendencias com os registros locais. | Proposto, sujeito a validacao | Nao tratar ausencia de resultado como prova de feriado ou dia sem publicacao; ver RN-009. |

## Rastreabilidade

- RF-001 a RF-003: criterios CA-001 e CA-002.
- RF-004: criterios CA-003 a CA-005.
- RF-005: criterios CA-006 e CA-011.
- RF-006: criterios CA-007 e CA-009, somente se a API for selecionada.
- RF-007: criterio CA-010, somente se a pagina for selecionada.
- RF-008: criterio CA-008, somente depois da validacao tecnica e aprovacao do grupo.