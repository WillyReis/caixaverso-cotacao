# 03 · Regras de Negócio

## Situação das regras

- Confirmada: regra registrada em decisão aceita ou definição explícita do grupo. Parâmetros ainda pendentes permanecem indicados e não devem ser escolhidos por suposição.
- Proposta: comportamento ou solução sujeito à validação e à aprovação do grupo. Seu registro não autoriza a implementação antes da aprovação.
- Os IDs da primeira versão provisória foram preservados para manter a rastreabilidade entre regras, requisitos, critérios de aceite, tarefas e testes.
- Os fluxos históricos superados pelo ADR-002 não constituem regras válidas para a implementação atual.

## Regras

| ID | Regra | Condição e resultado esperado | Requisitos relacionados | Situação |
| --- | --- | --- | --- | --- |
| RN-001 | Escopo de moedas e valores | No escopo inicial, coletar USD e EUR e armazenar os valores de compra e venda. | RF-001, RF-003 | Confirmada pela Constituição do Projeto e pelo ADR-002. |
| RN-002 | Escopo dos boletins | Armazenar os boletins de abertura e encerramento. Os boletins intermediários das 11h e 12h não devem ser armazenados no escopo inicial. | RF-001, RF-003 | Confirmada pela Constituição do Projeto e pelo ADR-002. |
| RN-003 | Início da coleta | Iniciar as tentativas dos boletins de abertura e encerramento por agendamento, nos horários e no calendário aprovados pelo grupo. A coleta não deve ser iniciada por requisição de consumidor. | RF-001 | Ingestão agendada confirmada; horários, fuso e calendário pendentes, conforme o contexto. |
| RN-004 | Identificação do boletim | Um item de coleta só pode ser concluído quando o tipo de boletim esperado for identificado para sua moeda e data. A resposta de outro tipo de boletim não satisfaz a pendência. | RF-002, RF-004 | Confirmada. A identificação não deve depender apenas do horário da consulta. |
| RN-005 | Novas tentativas | Enquanto o boletim esperado não for obtido, tentar novamente a cada cinco minutos nas primeiras três horas desde o início das tentativas do item; depois, tentar de hora em hora até obter os dados. | RF-004 | Confirmada como política; borda exata das três horas e limites de horário e calendário pendentes. |
| RN-006 | Controles independentes | Manter controles de pendência independentes para abertura e encerramento e para cada combinação de data, moeda e tipo de boletim. Uma falha em um item não conclui os demais. | RF-004 | Confirmada. |
| RN-007 | Pendências que atravessam o dia | Preservar a data, a moeda e o boletim originais em fluxo agendado de recuperação separado, sem bloquear a coleta do novo dia. Registrar aviso de pendência prolongada. | RF-004 | Comportamento confirmado; mecanismo de aviso e escalonamento pendentes. |
| RN-008 | Conclusão e duplicidade | Encerrar as tentativas do item após obter e armazenar o boletim. O reprocessamento não deve criar registros duplicados para o mesmo item lógico. | RF-003, RF-004 | Resultado confirmado; modelo físico e estratégia de persistência pendentes. |
| RN-009 | Ausência de publicação versus falha | O retorno do boletim esperado em `CotacaoMoedaPeriodo` pode confirmar sua publicação. Uma consulta bem-sucedida sem esse boletim não prova, isoladamente, feriado ou ausência de publicação; falhas técnicas também não podem ser classificadas como ausência de boletim. | RF-002, RF-004, RF-008 | Regra de classificação confirmada; reconciliação por esse recurso proposta e sujeita à validação técnica e à aprovação do grupo. |
| RN-010 | Leitura sem coleta sob demanda | Consultas dos consumidores devem usar somente dados locais pela API interna. A ausência de dados não autoriza chamada síncrona ao BACEN nem atualização sob demanda. | RF-005, RF-006 | Confirmada pelo ADR-002. A API é o único canal de consumo; RF-007 está fora do escopo. |
| RN-011 | Fluxos históricos superados | Diagramas e critérios antigos que chamam o BACEN quando o banco está vazio são referências históricas e não devem orientar a implementação atual. | RF-005, RF-006 | Restrição confirmada pelo ADR-002; os fluxos antigos foram superados. |

## Pontos a confirmar

- Agendamento: resolver a divergência de horários registrada no contexto e definir fuso, calendário e exceções de execução antes de implementar a RN-003.
- Novas tentativas: definir a borda exata da transição entre as três horas iniciais e as tentativas horárias, os limites de execução e eventual encerramento ou escalonamento de pendências, conforme RN-005 e RN-007.
- Avisos: definir o mecanismo e o escalonamento dos avisos de pendência prolongada previstos na RN-007.
- Integridade: definir o modelo físico, as restrições e a estratégia de persistência que assegurem a ausência de duplicidade exigida pela RN-008.
- Reconciliação: validar a completude e os tipos de boletim retornados por `CotacaoMoedaPeriodo` e aprovar seu uso antes de implementar o fluxo proposto. A distinção entre ausência e falha da RN-009 permanece obrigatória.

## Aplicação da stack

- Quarkus Scheduler executa os fluxos agendados. A política por item da RN-005 deve controlar a próxima tentativa; não substituí-la por retries imediatos ou backoff padrão de biblioteca.
- MicroProfile REST Client e SmallRye Fault Tolerance tratam a integração e as falhas técnicas. Timeouts e eventual limite de uma tentativa técnica precisam ser aprovados sem multiplicar silenciosamente as chamadas previstas na RN-005.
- Hibernate ORM com Panache Repository e Flyway implementam a persistência. Sua adoção não define automaticamente a chave física, o upsert, a precisão monetária ou a estratégia de recuperação.

## Referências

- [Constituição do Projeto](00-constituicao.md)
- [Contexto](01-contexto.md)
- [Requisitos Funcionais](02-requisitos-funcionais.md)
- [ADR-002: Ingestão agendada com persistência local](../adr/adr-002.md)
- [Plano Técnico](06-plano-tecnico.md)