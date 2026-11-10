# Tarefas

Backlog inicial derivado dos contratos. As tarefas de produto estao pendentes; itens condicionais so entram na implementacao se forem selecionados pelo grupo.

| ID | Tarefa | Requisitos relacionados | Dependencias | Status |
| --- | --- | --- | --- | --- |
| T-001 | Aprovar a base funcional e escolher as tres entregas iniciais entre as quatro opcoes registradas. Resolver a situacao da API descrita no ADR-002 versus a selecao de escopo. | RF-005 a RF-007, CA-007, CA-010 | Decisao do grupo | Pendente de decisao |
| T-002 | Confirmar/registrar ADR-001 (Java 25 e Quarkus 3.40 LTS) e incorporar os diagramas C4 de niveis 1 e 2 mencionados pelo grupo. | RNF-001; plano tecnico | Localizar artefatos existentes | Pendente |
| T-003 | Recuperar links oficiais completos e validar consultas da API PTAX para USD, EUR, abertura e encerramento, incluindo identificacao do tipo de boletim. | RF-001, RF-002, CA-001, CA-002 | T-002 | Pendente |
| T-004 | Definir calendario, fuso, comportamento apos reinicio, duracao/escalonamento de pendencias e mecanismo de aviso. | RF-004, RN-003 a RN-007, RNF-001, RNF-004, RNF-006, CA-004, CA-005 | Decisao operacional | Pendente de definicao |
| T-005 | Definir modelo de dados, precisao dos valores, chave unica logica e estrategia de persistencia/upsert no PostgreSQL. | RF-003, RN-008, RNF-002, CA-001, CA-005 | T-003 | Pendente de definicao |
| T-006 | Implementar job de coleta agendada de abertura e encerramento para USD e EUR, validacao do tipo de boletim e persistencia de compra/venda. | RF-001 a RF-003, RN-001 a RN-004, CA-001, CA-002 | T-001 a T-005 | Pendente |
| T-007 | Implementar controle independente de pendencias, tentativas a cada cinco minutos nas primeiras tres horas, tentativas horarias depois e recuperacao de dias anteriores. | RF-004, RN-005 a RN-008, RNF-003, RNF-004, RNF-006, CA-003 a CA-005 | T-004 a T-006 | Pendente |
| T-008 | Definir e implementar o canal de consumo selecionado, com leitura exclusivamente local; se API, definir filtros, autorizacao, campo de disponibilidade e erros. | RF-005, RF-006, RN-010, RNF-005, CA-006, CA-007 | T-001, T-005 | Pendente / condicional |
| T-009 | Se selecionada, definir e implementar a pagina de historico para USD/EUR e periodo, incluindo estados de dados ausentes ou parciais. | RF-007, CA-010 | T-001, T-008 | Pendente / condicional |
| T-010 | Executar prova de conceito de `CotacaoMoedaPeriodo` e aprovar ou rejeitar o fluxo de reconciliacao de pendencias. | RF-008, RN-009, CA-008 | T-003 | Pendente / condicional |
| T-011 | Consolidar testes automatizados e de integracao rastreados aos criterios aprovados, incluindo ausencia de chamadas sincrona ao BACEN no fluxo de consumidores. | Todos os RF/RN/RNF selecionados; CA aplicaveis | T-006 a T-010 | Pendente |
| T-012 | Confirmar a versao final do Golden Path, as orientacoes aplicaveis e como serao observadas no fluxo SDD com LLM deste projeto. | Constituicao, plano tecnico e tarefas | Validacao do grupo | Pendente |