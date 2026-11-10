# Regras de Negocio

| ID | Regra | Condicao e resultado esperado | Situacao/origem |
| --- | --- | --- | --- |
| RN-001 | Escopo de moedas e valores | No escopo inicial, coletar USD e EUR e armazenar valores de compra e venda. | Confirmado no material do grupo e no ADR-002. |
| RN-002 | Escopo dos boletins | Armazenar boletins de abertura e encerramento. Nao armazenar os boletins intermediarios das 11h e 12h no escopo inicial. | Confirmado. |
| RN-003 | Inicio da coleta | Iniciar as tentativas do boletim de abertura as 10h e do encerramento as 13h, horario de Brasilia. A coleta e agendada, nao iniciada por requisicao de consumidor. | Horarios confirmados; calendario de execucao pendente. |
| RN-004 | Identificacao do boletim | A coleta so considera concluido o item quando identifica o tipo de boletim esperado para sua moeda e data. Outro tipo de boletim nao satisfaz a pendencia. | Confirmado. |
| RN-005 | Novas tentativas | Enquanto o boletim esperado nao for obtido, tentar novamente a cada cinco minutos nas primeiras tres horas desde o inicio das tentativas do item; depois, tentar de hora em hora ate obter os dados. | Confirmado como politica; limites de horario/calendario pendentes. |
| RN-006 | Controles independentes | Manter controles de pendencia independentes para abertura e encerramento e para cada combinacao de data, moeda e tipo de boletim. Uma falha em um item nao conclui os demais. | Confirmado. |
| RN-007 | Pendencias que atravessam o dia | Preservar a data, moeda e boletim originais em fluxo agendado de recuperacao separado; nao bloquear a coleta do novo dia. Registrar aviso de pendencia prolongada. | Confirmado; mecanismo de aviso e escalonamento pendentes. |
| RN-008 | Conclusao e duplicidade | Encerrar as tentativas do item apos obter e armazenar o boletim. Reprocessamento nao deve criar registros duplicados para o mesmo item logico. | Confirmado como resultado esperado; chave/estrategia tecnica pendente. |
| RN-009 | Ausencia de publicacao versus falha | Uma resposta positiva de `CotacaoMoedaPeriodo` pode confirmar publicacao. Uma consulta bem-sucedida sem o boletim esperado nao prova, isoladamente, feriado ou ausencia de publicacao; falha tecnica tambem nao pode ser classificada como ausencia de boletim. | Regra de classificacao confirmada; processo de reconciliacao proposto e pendente de validacao. |
| RN-010 | Leitura sem coleta sob demanda | Consultas dos consumidores usam somente dados locais. Ausencia de dado nao autoriza chamada sincrona ao BACEN nem atualizacao sob demanda. | Confirmado pelo ADR-002. |
| RN-011 | Fluxos historicos superados | Diagramas e criterios antigos que chamam o BACEN quando o banco esta vazio sao referencias historicas, nao regras da versao atual. | Superado pelo ADR-002; nao implementar. |