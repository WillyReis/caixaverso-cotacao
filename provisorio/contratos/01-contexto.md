# Contexto

**Status:** visao e alternativas registradas para orientar a definicao do escopo; a selecao final da primeira entrega continua pendente.

## Problema e oportunidade

Aplicacoes internas precisam reutilizar um historico local de cotacoes cambiais obtidas do BACEN. O projeto organiza essa coleta e persistencia para apoiar consultas e visualizacoes sem depender de chamadas ao BACEN durante o fluxo de cada consumidor.

## Publico e atores

- Aplicacoes internas que consultam os dados armazenados.
- Usuarios interessados em consultar ou visualizar o historico.
- O BACEN, como origem dos boletins de cotacao.
- A equipe do projeto, responsavel por validar escopo, regras e operacao.

## Base funcional conhecida

- Moedas iniciais: dolar (USD) e euro (EUR).
- Dados: valores de compra e venda dos boletins de abertura e encerramento.
- Coleta: consultas agendadas a API PTAX do BACEN, com inicio previsto as 10h para abertura e as 13h para encerramento, horario de Brasilia.
- Persistencia: banco local PostgreSQL, conforme decisao do [ADR-002](../adr/adr-002.md).
- Leitura: consumidores usam dados persistidos; a falta de dado nao dispara coleta sob demanda.
- Boletins intermediarios das 11h e 12h nao fazem parte do armazenamento inicial. Selic nao esta confirmada no escopo.

## Entregas candidatas

O material manteve quatro possibilidades e solicita que o grupo selecione as tres que melhor representam a primeira entrega. Esta selecao nao deve ser inferida pela implementacao.

| Opcao | Descricao | Situacao registrada |
| --- | --- | --- |
| 1 | Coleta agendada em aplicacao Quarkus | Proposta de entrega; horarios e politica de novas tentativas estao descritos nos contratos. |
| 2 | Banco local implementado e disponivel para consumo interno | PostgreSQL foi decidido; forma de acesso e consumidor de teste ainda precisam ser definidos. |
| 3 | API de consulta no container APP, somente leitura | O ADR-002 descreve essa responsabilidade, mas sua inclusao como entrega da primeira versao deve ser reconciliada com a selecao pendente. |
| 4 | Pagina web para visualizar o historico | Proposta. Angular e Angular Material foram citados como possibilidades, nao como escolha. |

## Fora do escopo confirmado

- Consultar o BACEN sincronicamente quando um consumidor nao encontrar dados no banco.
- Armazenar os boletins intermediarios das 11h e 12h no escopo inicial.
- Incluir Selic sem decisao explicita do grupo.

Os diagramas antigos em que a API do usuario chama o BACEN quando o banco esta vazio sao historicos e foram superados pelo ADR-002; nao devem orientar a implementacao atual.

## Premissas e dependencias

- PTAX e a taxa de cambio de referencia do real em relacao ao dolar publicada pelo BACEN. O material descreve o fechamento como media das taxas apuradas nas quatro consultas diarias; a implementacao deve reconhecer o tipo de boletim retornado, nao deduzi-lo apenas pelo horario da requisicao.
- Foi localizado na documentacao o recurso `CotacaoMoedaPeriodo`, para consultar boletins de uma moeda em um periodo. Seu uso na reconciliacao de pendencias e uma proposta ainda nao validada por teste de integracao.
- Nao foi confirmada uma API separada de calendario de dias com publicacao PTAX.
- O material informa que diagramas C4 de niveis 1 e 2 foram produzidos, mas eles nao estao incorporados a este diretorio.
- As URLs de referencia recebidas no material estao truncadas; recuperar e validar os links oficiais antes de depender deles nos artefatos SDD.