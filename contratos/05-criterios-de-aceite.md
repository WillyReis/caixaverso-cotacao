# 05 · Critérios de Aceite

## Situação dos critérios

- Os cenários usam **Dado**, **Quando** e **Então** para descrever a condição inicial, a ação e o resultado esperado.
- Candidato: cenário derivado do material do grupo para verificar os comportamentos registrados nos contratos. Parâmetros pendentes devem ser definidos e aprovados antes da implementação correspondente.
- Condicional: cenário aplicável somente após a seleção da entrega e a aprovação do contrato do canal de consumo.
- Proposto: cenário que depende de validação técnica ou da aprovação de uma regra ainda não confirmada. Seu registro não autoriza a implementação.
- Os IDs da primeira versão provisória foram preservados para manter a rastreabilidade entre critérios, requisitos, regras, tarefas e testes. A consolidação deste documento não transforma pendências em decisões aprovadas.

## Critérios

| ID | Requisitos e regras relacionados | Cenário e resultado esperado | Situação |
| --- | --- | --- | --- |
| CA-001 | RF-001, RF-002, RF-003, RN-001, RN-002, RN-003, RN-004 | **Dado** um agendamento de abertura ou encerramento para USD ou EUR, conforme horários e calendário aprovados, **quando** o BACEN retornar o boletim esperado para a moeda e a data do item, **então** persistir localmente em H2 os valores de compra e venda identificados por moeda, data e tipo de boletim, e concluir somente esse item. | Candidato de aceite da coleta; horários, fuso e calendário pendentes. |
| CA-002 | RF-002, RF-004, RN-004, RN-006 | **Dado** um item pendente, **quando** a API retornar outro tipo de boletim ou dados de moeda ou data diferentes das esperadas, **então** não marcar o item como concluído e manter suas tentativas, sem concluir os demais itens. | Candidato; identificação do boletim deve ser validada na integração. |
| CA-003 | RF-004, RN-005 | **Dado** um item pendente nas primeiras três horas desde o início de suas tentativas, **quando** uma tentativa falhar ou não retornar o boletim esperado, **então** manter o item pendente e agendar nova tentativa cinco minutos depois, sem registrar a coleta como concluída. | Candidato; a política de cinco minutos se aplica à fase inicial da RN-005. Borda exata das três horas e limites de execução pendentes. |
| CA-004 | RF-004, RN-005, RN-006 | **Dado** um item que continue pendente após a fase inicial de três horas, **quando** ocorrer a transição conforme a borda aprovada, **então** passar a tentar de hora em hora até obter o boletim esperado. Abertura e encerramento mantêm controles independentes por data, moeda e tipo de boletim. | Candidato; borda exata das três horas, calendário e limites de execução pendentes. |
| CA-005 | RF-004, RN-007, RN-008 | **Dado** um item pendente do dia anterior, **quando** iniciar o novo dia, **então** recuperar o item separadamente, preservando data, moeda e boletim originais, sem bloquear a coleta atual; registrar aviso de pendência prolongada conforme o mecanismo aprovado e, ao obter e armazenar o item, encerrar suas tentativas sem duplicar o registro. | Candidato; mecanismos de aviso e recuperação, incluindo a retomada após reinício, pendentes. |
| CA-006 | RF-005, RN-010, RN-011 | **Dado** que um consumidor solicite um dado inexistente no armazenamento local, **quando** a consulta for processada, **então** não chamar o BACEN nem iniciar coleta sob demanda. A resposta de ausência ou parcialidade deve seguir o contrato do canal selecionado. | Direção confirmada; canal de consumo e formato da resposta pendentes. |
| CA-007 | RF-006, RN-010 | **Dado** que a API tenha sido selecionada e existam registros locais, **quando** um consumidor autorizado consultar usando filtros aprovados, **então** retornar somente os dados locais correspondentes aos filtros, incluindo compra e venda e o campo de disponibilidade para dados ausentes ou parciais, sem chamar o BACEN. | Condicional; filtros, autorização, status HTTP, formato da resposta e nome e valores do campo de disponibilidade pendentes. |
| CA-008 | RF-008, RN-009 | **Dado** um período com datas pendentes e a reconciliação validada e aprovada, **quando** a rotina agendada consultar `CotacaoMoedaPeriodo`, **então** comparar os boletins retornados com os registros locais por moeda, data e tipo de boletim. Resultado vazio ou sem o boletim esperado e falha técnica devem permanecer distintos; nenhum desses resultados comprova, isoladamente, dia sem publicação. | Proposto; depende de prova de conceito, definição do período e da estratégia e aprovação do grupo. |
| CA-009 | RF-006 | **Dado** que a API tenha sido selecionada e a regra de consulta por data futura tenha sido aprovada, **quando** o consumidor informar data posterior à data atual, **então** rejeitar a consulta como data inválida conforme a resposta acordada, sem provocar coleta sob demanda. | Critério histórico preservado; aplicabilidade da regra, referência da data atual, status e mensagem pendentes. |
| CA-010 | RF-007, RN-010 | **Dado** que a página web tenha sido selecionada e existam dados para o período escolhido, **quando** o usuário selecionar USD ou EUR, **então** apresentar o histórico disponível pelo canal aprovado, sem provocar coleta sob demanda. Ausência, parcialidade e erro devem seguir o comportamento aprovado. | Condicional; interface, visualização, canal de consulta e estados de ausência, parcialidade e erro pendentes. |
| CA-011 | RF-005, RN-010 | **Dado** que o acesso direto ao banco local H2 tenha sido selecionado e autorizado para um consumidor interno, **quando** esse consumidor consultar o histórico pela forma acordada, **então** recuperar os valores armazenados de compra e venda com moeda, data e tipo de boletim, sem chamada ao BACEN. | Condicional; acesso direto, autorização, forma de consulta e consumidor de teste pendentes. |

## Rastreabilidade

- Os vínculos da tabela seguem os [Requisitos Funcionais](02-requisitos-funcionais.md) e as [Regras de Negócio](03-regras-de-negocio.md).
- CA-001 e CA-005 também se relacionam ao isolamento de falhas previsto na RNF-003, conforme os [Requisitos Não Funcionais](04-requisitos-nao-funcionais.md).
- As verificações de fuso do host, idempotência, observabilidade, segurança, recuperação após reinício e metas operacionais devem seguir o contrato de requisitos não funcionais. Os onze cenários não substituem o detalhamento e a aprovação das verificações ainda pendentes nesse contrato.
- O plano, as tarefas e os testes devem referenciar os IDs aplicáveis e respeitar a situação e as condições de cada cenário.

## Critérios históricos atualizados

- O cenário anterior de sucesso de consulta permanece aplicável somente ao canal de consumo selecionado pelo grupo: CA-007, CA-010 ou CA-011.
- O cenário anterior de erro síncrono ao consultar o BACEN foi substituído por pendência e novas tentativas do job em CA-003; não deve reintroduzir chamada sob demanda.
- CA-009 preserva a proposta histórica de rejeição de datas futuras, sem tratá-la como comportamento aprovado.

## Pontos a confirmar

- Agendamento: resolver a divergência de horários do contexto e definir fuso, calendário e exceções antes de detalhar os testes de coleta.
- Novas tentativas: definir a borda exata das três horas, os limites de horário e calendário e eventual encerramento ou escalonamento de pendências para CA-003, CA-004 e CA-005.
- Recuperação e avisos: aprovar os mecanismos de aviso de pendência prolongada e de retomada após reinício, além da estratégia de persistência que evite perda ou duplicidade, antes de fechar os testes correspondentes.
- Canais de consumo: selecionar as entregas, aprovar filtros, autorização, disponibilidade, respostas de ausência, parcialidade e erro e definir os consumidores de teste para CA-006, CA-007, CA-010 e CA-011.
- Datas futuras: aprovar ou retirar CA-009 e, se aprovado, definir a referência da data atual e a resposta observável.
- Reconciliação: validar a completude e os tipos de boletim retornados por `CotacaoMoedaPeriodo`, definir período e estratégia e aprovar o fluxo antes de aplicar CA-008. A distinção entre ausência de boletim e falha técnica continua obrigatória.
- Verificações não funcionais: detalhar os cenários e métodos de medição pendentes no contrato 04, sem assumir metas numéricas ou mecanismos por padrão.

## Referências

- [Constituição do Projeto](00-constituicao.md)
- [Contexto](01-contexto.md)
- [Requisitos Funcionais](02-requisitos-funcionais.md)
- [Regras de Negócio](03-regras-de-negocio.md)
- [Requisitos Não Funcionais](04-requisitos-nao-funcionais.md)
- [ADR-002: Ingestão agendada com persistência local](../adr/adr-002.md)
- [Primeira versão provisória dos critérios de aceite](../provisorio/contratos/05-criterios-de-aceite.md)
