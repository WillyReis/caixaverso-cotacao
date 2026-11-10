# 01 · Contexto

## Problema

Aplicações internas e usuários precisam consultar o histórico de cotações cambiais do BACEN sem realizar chamadas externas a cada necessidade. O projeto organiza a coleta e a persistência local desses dados para permitir sua reutilização, mantendo as consultas independentes da disponibilidade do BACEN no momento do consumo.

## Usuários

- Sistemas internos que consomem o histórico armazenado.
- Analistas internos que consultam os dados.
- Usuários finais, conforme os canais de consulta selecionados pelo grupo.

## Fluxo prioritário

Consulta do histórico de cotações do dólar:

1. O consumidor solicita o histórico de cotações do dólar para um período.
2. O canal de consulta aprovado recupera os registros do banco local H2, sem consultar o BACEN.
3. O consumidor recebe o histórico disponível e a indicação de dados ausentes ou parciais, conforme o contrato do canal selecionado.

## Resultado esperado

- Coleta assíncrona agendada de cotações de dólar (USD) e euro (EUR) na API PTAX do BACEN.
- Armazenamento dos valores de compra e venda dos boletins de abertura e encerramento.
- Persistência local em H2, conforme a Constituição do Projeto e o ADR-002.
- Consulta dos dados armazenados sem dependência de chamadas síncronas ao BACEN.
- Disponibilização do histórico pelos canais selecionados pelo grupo, com tratamento explícito de dados ausentes ou parciais.

## Entregas candidatas

A seleção das três entregas iniciais entre as quatro possibilidades permanece pendente:

1. Coleta agendada em aplicação Quarkus.
2. Banco local H2 disponível para consumo interno, com forma de acesso e consumidor de teste a definir.
3. API REST somente de leitura, documentada em OpenAPI 3.
4. Página web para consulta e visualização do histórico, com interface e tecnologia a definir.

O ADR-002 estabelece as responsabilidades de coleta e leitura, mas a inclusão da API na primeira entrega ainda precisa ser reconciliada com essa seleção. A topologia de processos e containers permanece pendente de definição.

## Fora do escopo inicial

- Consultas síncronas ao BACEN ou coleta sob demanda provocada por consultas dos consumidores.
- Armazenamento dos boletins intermediários das 11h e 12h.
- Moedas além de dólar e euro.
- Previsões de cotações.

Diagramas antigos que consultam o BACEN quando o banco está vazio foram superados pelo ADR-002 e não devem orientar a implementação.

## Pontos a confirmar

- Horários e calendário de coleta: a primeira versão provisória registra 10h para abertura e 13h para encerramento, no horário de Brasília; a segunda registra 9h e 16h. O grupo deve resolver a divergência e confirmar fuso, dias de execução e feriados antes de fixar o agendamento.
- Selic: a segunda versão provisória propõe sua inclusão e cita a série 1178. A inclusão e a série de referência dependem de aprovação; não devem ser inferidas do escopo cambial definido no documento 00.
- Carga inicial: confirmar a necessidade de importar o histórico desde 01/01/2020, data proposta na segunda versão provisória.
- Segurança: confirmar autenticação e autorização. SSO consta apenas na segunda versão provisória e depende de validação.
- Contrato de consulta: definir filtros, formato de resposta, status HTTP e representação de dados ausentes ou parciais. O retorno `200 OK` com lista JSON é uma proposta da segunda versão provisória, não um contrato aprovado.
- Canais de consumo: definir acesso por API ou diretamente ao banco e selecionar um consumidor de teste.
- Visualização: confirmar a entrega de página web e o uso de gráficos, excluídos pela segunda versão provisória. Angular e Angular Material foram citados como possibilidades, não como tecnologias aprovadas.
- Responsável pelo grupo: confirmar o nome completo; a segunda versão provisória informa apenas "Figura".

## Premissas e dependências

- O BACEN é a origem dos boletins. A coleta deve reconhecer o tipo de boletim retornado, sem deduzi-lo apenas pelo horário da requisição.
- O uso de `CotacaoMoedaPeriodo` para reconciliação de pendências é uma proposta sujeita a validação por integração. Uma resposta vazia não comprova, isoladamente, feriado ou ausência de publicação; falhas técnicas devem permanecer distintas da ausência de boletim.
- Não foi confirmada uma fonte separada de calendário de dias com publicação PTAX.
- Os diagramas C4 de níveis 1 e 2 mencionados pelo grupo devem ser localizados e incorporados à documentação.
- As URLs oficiais da API PTAX e dos exemplos BACEN devem ser recuperadas e validadas antes de serem utilizadas como referência técnica.

## Referências

- [Constituição do Projeto](00-constituicao.md)
- [ADR-002: Ingestão agendada com persistência local](../adr/adr-002.md)
- [Primeira versão provisória do contexto](../provisorio/contratos/01-contexto.md)
- [Segunda versão provisória do contexto](../provisorio2/contratos/01-contexto.md)