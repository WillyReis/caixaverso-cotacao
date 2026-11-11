# 04 · Requisitos Não Funcionais

## Situação dos requisitos

- Comportamentos definidos e parâmetros ainda pendentes permanecem explicitamente diferenciados. Valores padrão de ferramentas e suposições não constituem compromissos do produto.
- Os critérios de verificação orientam os testes dos comportamentos descritos. Cenários que dependem de decisões pendentes devem ser detalhados e aprovados antes da implementação correspondente.
- A seleção de uma tecnologia não define automaticamente metas de desempenho, disponibilidade, retenção ou recuperação.
- Os IDs da primeira versão provisória foram preservados para manter a rastreabilidade entre requisitos, regras, critérios de aceite, tarefas e testes.

## Requisitos

| ID | Categoria | Requisito não funcional | Critério de verificação | Situação |
| --- | --- | --- | --- | --- |
| RNF-001 | Tempo e agendamento | O sistema deve interpretar os horários de coleta no fuso aprovado pelo grupo, sem depender do fuso configurado no host. | Verificar que o agendamento respeita os horários e o fuso aprovados mesmo quando o host utiliza outro fuso. | Independência do fuso do host definida; horários, identificador de fuso e calendário pendentes, conforme o contexto. O horário de Brasília consta como referência na primeira versão provisória. |
| RNF-002 | Integridade | O reprocessamento de um boletim não deve produzir duplicidade para a mesma data, moeda e tipo de boletim. | Reprocessar o mesmo item em teste de persistência e verificar que não são criados registros duplicados no H2. | Resultado definido; mecanismo de idempotência, chave física e estratégia de persistência pendentes no plano técnico. |
| RNF-003 | Isolamento de falhas | A falha ou indisponibilidade de um item de coleta não deve impedir o controle dos demais itens pendentes nem as coletas do dia atual. | Simular falhas independentes por moeda, data e tipo de boletim e verificar que os demais itens continuam sendo tratados. | Comportamento definido; cenários devem abranger os critérios candidatos CA-001 e CA-005. |
| RNF-004 | Observabilidade | O sistema deve registrar falhas e pendências prolongadas de modo que a equipe possa identificá-las. | Provocar falha e pendência prolongada e verificar os registros e o aviso previstos nas regras de negócio, conforme o mecanismo aprovado. | Aviso de pendência prolongada definido; formato, destino, severidade e mecanismo de registro e aviso pendentes. |
| RNF-005 | Segurança | Caso a API interna seja entregue, somente consumidores autorizados podem consultar os dados. | Verificar que consumidores autorizados conseguem consultar conforme o contrato aprovado e que consumidores não autorizados têm o acesso negado. | Intenção confirmada; aplicação condicionada à seleção da API. Autenticação, autorização, perfis e gestão de credenciais pendentes. |
| RNF-006 | Operação e recuperação | Após reinício, o job deve preservar ou reconstruir as pendências sem perder a data, a moeda e o boletim de origem. | Critério candidato: reiniciar o job com itens pendentes e verificar sua retomada preservando a identificação de cada item. | Necessidade identificada; estratégia de persistência e recuperação e critério final de teste pendentes de definição e aprovação. |
| RNF-007 | Desempenho, disponibilidade e retenção | Devem ser definidos os limites de resposta, disponibilidade, volume, retenção e crescimento para a entrega selecionada. | Registrar os alvos aprovados e os respectivos métodos de medição antes de fechar os requisitos de produção. | Pendente de definição; nenhum alvo numérico foi acordado no material do grupo. |

## Rastreabilidade

- RNF-001: RF-001 e RN-003.
- RNF-002: RF-003, RF-004 e RN-008.
- RNF-003: RF-001, RF-004, RN-006 e RN-007; critérios candidatos CA-001 e CA-005.
- RNF-004: RF-004 e RN-007.
- RNF-005: RF-006, somente se a API for selecionada.
- RNF-006: RF-004, RN-005, RN-007 e RN-008.
- RNF-007: entrega e canais selecionados pelo grupo; metas e critérios de medição ainda precisam ser aprovados.

Os IDs de aceite citados referem-se ao [contrato oficial de critérios de aceite](05-criterios-de-aceite.md), que mantém explícitos os cenários candidatos, condicionais e propostos e suas decisões pendentes.

## Pontos a confirmar

- Agendamento: resolver a divergência de horários registrada no contexto e definir o identificador de fuso, o calendário e as exceções de execução para RNF-001.
- Persistência e recuperação: definir a chave física e a estratégia de idempotência no H2, além do estado necessário à retomada após reinício e do critério final de teste, conforme RNF-002 e RNF-006.
- Observabilidade: definir formato, destino, severidade e mecanismos dos registros e avisos previstos em RNF-004.
- Segurança: confirmar a entrega da API e aprovar autenticação, autorização, perfis e gestão de credenciais antes de implementar RNF-005. A seleção de SSO não deve ser presumida.
- Metas operacionais: aprovar limites de resposta, disponibilidade, volume, retenção e crescimento e estabelecer como serão medidos, conforme RNF-007. Não assumir valores numéricos por padrão.

## Referências

- [Constituição do Projeto](00-constituicao.md)
- [Contexto](01-contexto.md)
- [Requisitos Funcionais](02-requisitos-funcionais.md)
- [Regras de Negócio](03-regras-de-negocio.md)
- [Primeira versão provisória dos requisitos não funcionais](../provisorio/contratos/04-requisitos-nao-funcionais.md)