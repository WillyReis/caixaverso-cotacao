# Plano Tecnico

**Status:** arquitetura-base registrada; detalhes marcados como pendentes ou propostos nao devem ser escolhidos implicitamente durante a implementacao.

## Arquitetura e decisoes registradas

- **Origem:** API PTAX do BACEN.
- **APP (job):** executa a coleta agendada, busca boletins e persiste os dados no PostgreSQL.
- **APP (API):** o ADR-002 descreve uma API somente de leitura, atendendo consultas a partir do banco local.
- **Persistencia:** PostgreSQL local, conforme [ADR-002](../adr/adr-002.md).
- **Stack:** o material recebido registra Java 25 e Quarkus 3.40 LTS como padrao corporativo aceito em ADR-001. O arquivo ADR-001 nao foi localizado e precisa ser confirmado antes de fixar a versao nos arquivos de build.
- **Restricao de integracao:** o fluxo do consumidor nao chama o BACEN; ausencia no banco nao dispara coleta sob demanda.

O material descreve APP (job) e APP (API), mas a topologia final de processos, containers, deploy e compartilhamento de configuracao precisa ser confirmada. A inclusao da API como entrega da primeira versao tambem precisa ser reconciliada com as alternativas em `01-contexto.md`.

## Fluxos

### Coleta agendada

1. Iniciar tentativas de abertura as 10h e de encerramento as 13h, horario de Brasilia.
2. Consultar a API PTAX para USD e EUR e identificar o tipo de boletim retornado.
3. Persistir compra e venda somente quando o boletim esperado para a data e moeda estiver disponivel.
4. Manter pendencias, aplicar a politica de novas tentativas e recuperar datas anteriores sem bloquear o novo dia, conforme `03-regras-de-negocio.md`.

### Consulta de consumidores

Consumir somente registros persistidos. A consulta deve permanecer independente da coleta. Se a API interna for a entrega selecionada, ela e somente leitora e seu contrato precisa definir filtros, autorizacao, ausencia/parcialidade e erros.

### Reconciliacao de datas sem boletim

O uso de `CotacaoMoedaPeriodo` em uma rotina agendada e uma proposta. Antes de implementar, testar o recurso com USD e EUR em intervalo conhecido, verificar a completude e definir periodo e tratamento dos resultados. Resposta positiva pode comprovar publicacao; resposta vazia nao comprova, por si so, feriado ou dia sem publicacao. Falha tecnica deve continuar distinta de ausencia.

## Dados conhecidos

Cada registro representa compra e venda de uma moeda para uma data e tipo de boletim. A combinacao moeda + data + tipo de boletim e a chave logica candidata para identificar um item e evitar duplicidade; confirmar o modelo fisico, restricoes e estrategia de upsert antes de implementar. Nao foram definidos formato numerico, precisao, politica de retencao ou armazenamento da resposta original do BACEN.

## Seguranca e operacao

- Configurar horarios usando explicitamente o horario de Brasilia; confirmar o identificador de fuso e o calendario de execucao.
- Registrar falhas e pendencias prolongadas. Destino, formato, severidade e mecanismo de alerta ainda precisam de decisao.
- Definir como pendencias e contadores/tempos de tentativa sobrevivem a reinicios.
- Se a API interna for entregue, definir autenticacao e autorizacao para consumidores internos.

## Decisoes tecnicas pendentes

- Selecionar as tres entregas iniciais entre coleta, consumo do banco, API e pagina web.
- Especificar o contrato da API (se selecionada): filtros, resposta, campo de disponibilidade, estados de dados parciais, erros e autorizacao.
- Definir acesso por API ou diretamente ao banco e selecionar um consumidor de teste.
- Confirmar calendario/feriados, reinicio, aviso e eventual escalonamento de pendencias.
- Validar tecnicamente `CotacaoMoedaPeriodo` e recuperar URLs oficiais completas.
- Confirmar ADR-001 e incorporar os diagramas C4 de niveis 1 e 2 que o grupo informou ter produzido.
- Confirmar a versao final do Golden Path e quais orientacoes se aplicam ao projeto e ao fluxo SDD com LLM.