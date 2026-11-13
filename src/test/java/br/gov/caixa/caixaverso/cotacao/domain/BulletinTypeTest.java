package br.gov.caixa.caixaverso.cotacao.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("CA-001")
@Tag("CA-002")
class BulletinTypeTest {
    @Test
    void recognizesOnlyOpeningAndPtaxClosing() {
        assertEquals(BulletinType.ABERTURA, BulletinType.fromBacen("Abertura").orElseThrow());
        assertEquals(BulletinType.ENCERRAMENTO,
                BulletinType.fromBacen("Fechamento PTAX").orElseThrow());
    }

    @Test
    void doesNotTreatIntermediateOrInterbankClosingAsPtaxClosing() {
        assertTrue(BulletinType.fromBacen("Intermedi\u00e1rio").isEmpty());
        assertTrue(BulletinType.fromBacen("Fechamento Interbanc\u00e1rio").isEmpty());
        assertTrue(BulletinType.fromBacen("Fechamento").isEmpty());
        assertTrue(BulletinType.fromBacen(null).isEmpty());
        assertTrue(BulletinType.fromBacen("unexpected").isEmpty());
    }
}