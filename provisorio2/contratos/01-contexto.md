# 01 · Contexto

## Problema

Consultar cotações e taxas do BACEN exige chamar APIs externas a cada necessidade, sem histórico local consolidado. Precisamos armazenar localmente o histórico de cotações (valor de abertura) de EUR e USD, e da taxa Selic, para consulta posterior.

## Usuários

- Outros sistemas
- Analistas internos
- Usuários finais

## Fluxo prioritário

Consulta do histórico de cotações do dólar:

1. O usuário solicita o histórico de cotações do dólar.
2. O backend consulta o banco de dados.
3. O backend responde `200 OK` com a lista de cotações em JSON.

Caso de ausência: se não houver dados no banco para o período solicitado, o sistema deve responder conforme definido nos critérios de aceite (ver [05-criterios-de-aceite.md](./05-criterios-de-aceite.md)).

## Resultado esperado

- Sistema que acessa as APIs do BACEN e armazena no banco de dados o histórico de cotações do euro e do dólar (valor do boletim de **abertura**) e da taxa Selic (valor diário da série 1178).
- Carga inicial do histórico desde 01/01/2020.
- Atualização automática em dois horários: na abertura (9h) e no fechamento (16h).
- Endpoints REST para consulta desse histórico, documentados em OpenAPI 3.
- Autenticação via SSO.

## Fora do escopo

- Previsões.
- Gráficos.
- Moedas além de euro e dólar (por enquanto).

## Owner do grupo

- Nome: Figura <!-- confirmar nome completo -->


