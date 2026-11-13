package br.gov.caixa.caixaverso.cotacao.persistence;

import br.gov.caixa.caixaverso.cotacao.domain.CollectionKey;
import br.gov.caixa.caixaverso.cotacao.domain.Currency;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class QuotationRepository implements PanacheRepository<QuotationEntity> {
    public Optional<QuotationEntity> findByKey(CollectionKey key) {
        return find("quotationDate = ?1 and currency = ?2 and bulletin = ?3",
                key.date(), key.currency(), key.bulletin()).firstResultOptional();
    }

    public List<QuotationEntity> history(Currency currency, LocalDate start, LocalDate end) {
        return find("currency = ?1 and quotationDate between ?2 and ?3 order by quotationDate, bulletin",
                currency, start, end).list();
    }
}