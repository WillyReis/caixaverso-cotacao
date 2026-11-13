# 01 · Contexto

## Problema

Aplicações internas precisam consultar o histórico de cotações cambiais do BACEN sem realizar chamadas externas a cada necessidade. O projeto organiza a coleta, a persistência local e uma API interna somente de leitura, mantendo as consultas independentes da disponibilidade do BACEN no momento do consumo. O projeto será apenas backend, sem página web.

## Usuários

- Sistemas internos autorizados que consomem o histórico pela API.
- Analistas internos que utilizam consumidores autorizados da API. A interface desses consumidores não faz parte deste projeto.

## Fluxo prioritário

Consulta do histórico de cotações do dólar:

1. O consumidor solicita o histórico de cotações do dólar para um período.
2. A API interna recupera os registros do banco local H2, sem consultar o BACEN.
3. O consumidor recebe o histórico disponível e a indicação de dados ausentes ou parciais, conforme o contrato da API.

## Resultado esperado

- Coleta assíncrona agendada de cotações de dólar (USD) e euro (EUR) na API PTAX do BACEN.
- Armazenamento dos valores de compra e venda dos boletins de abertura e encerramento.
- Persistência local em H2, conforme a Constituição do Projeto e o ADR-002.
- Consulta dos dados armazenados sem dependência de chamadas síncronas ao BACEN.
- Disponibilização do histórico exclusivamente pela API interna, com tratamento explícito de dados ausentes ou parciais.

## Entregas definidas

As três entregas do MVP estão definidas:

1. Coleta agendada com Quarkus Scheduler e MicroProfile REST Client.
2. Persistência local em H2 embarcado em arquivo, por Hibernate ORM com Panache Repository e esquema versionado com Flyway.
3. API interna somente de leitura com Quarkus REST, documentada em OpenAPI 3 por SmallRye OpenAPI.

O ADR-002 torna a API obrigatória e define um único aplicativo Quarkus em processo JVM para job e API, com responsabilidades lógicas separadas. Docker e Docker Compose atendem a execução local, com volume persistente para o H2. O ambiente de implantação e os parâmetros operacionais permanecem pendentes.

## Fora do escopo inicial

- Consultas síncronas ao BACEN ou coleta sob demanda provocada por consultas dos consumidores.
- Armazenamento dos boletins intermediários das 11h e 12h.
- Moedas além de dólar e euro.
- Previsões de cotações.
- Página web, frontend e gráficos.
- Acesso direto ao H2 como canal de consumo e endpoints de escrita para consumidores.

Diagramas antigos que consultam o BACEN quando o banco está vazio foram superados pelo ADR-002 e não devem orientar a implementação.

## Pontos a confirmar

- Horários e calendário de coleta: a primeira versão provisória registra 10h para abertura e 13h para encerramento, no horário de Brasília; a segunda registra 9h e 16h. O grupo deve resolver a divergência e confirmar fuso, dias de execução e feriados antes de fixar o agendamento.
- Selic: a segunda versão provisória propõe sua inclusão e cita a série 1178. A inclusão e a série de referência dependem de aprovação; não devem ser inferidas do escopo cambial definido no documento 00.
- Carga inicial: confirmar a necessidade de importar o histórico desde 01/01/2020, data proposta na segunda versão provisória.
- Segurança: a tecnologia é Quarkus OIDC com bearer token e autorização por perfis. Aprovar provedor, perfis, credenciais e critérios de acesso; SSO interativo não é uma entrega presumida.
- Contrato de consulta: definir filtros, formato de resposta, status HTTP e representação de dados ausentes ou parciais. O retorno `200 OK` com lista JSON é uma proposta da segunda versão provisória, não um contrato aprovado.
- Consumidor de teste: selecionar uma aplicação ou cliente HTTP autorizado para verificar o contrato da API; não haverá canal de acesso direto ao banco ou página web.
- Operação do H2: definir caminho do arquivo, volume, backup e retenção no ambiente de implantação, respeitando o acesso por um único processo.
- Responsável pelo grupo: Figura Carrijo Viana Figur.

## Premissas e dependências

- O BACEN é a origem dos boletins. A coleta deve reconhecer o tipo de boletim retornado, sem deduzi-lo apenas pelo horário da requisição.
- O uso de `CotacaoMoedaPeriodo` para reconciliação de pendências é uma proposta sujeita a validação por integração. Uma resposta vazia não comprova, isoladamente, feriado ou ausência de publicação; falhas técnicas devem permanecer distintas da ausência de boletim.
- Não foi confirmada uma fonte separada de calendário de dias com publicação PTAX.
- Os diagramas C4 de níveis 1 e 2 mencionados pelo grupo devem ser localizados e incorporados à documentação.
- As URLs oficiais da API PTAX e dos exemplos BACEN devem ser recuperadas e validadas antes de serem utilizadas como referência técnica.

## Referências

- [Constituição do Projeto](00-constituicao.md)
- [Plano Técnico](06-plano-tecnico.md)
- [ADR-001: Stack do projeto](../adr/adr-001.md)
- [ADR-002: Ingestão agendada com persistência local](../adr/adr-002.md)