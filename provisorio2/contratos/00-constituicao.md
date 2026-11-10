# 00 · Constituição do Projeto

## Stack

- Linguagem: Java 25 (JDK 25)
- Framework: Quarkus (versão mais recente estável no início da implementação)
- Build: Maven (versão mais recente estável no início da implementação)
- Banco de dados: H2 (desenvolvimento e testes) e PostgreSQL (produção)
- Cache: nenhum por enquanto
- Testes: JUnit 5
- Containers: sem definição no momento

## Padrões obrigatórios

- Todos os valores monetários devem ser representados com `BigDecimal`.
- APIs seguem o padrão REST e são documentadas com SWAGGER / OpenAPI 3.

## Proibições

- Nenhuma por enquanto.
