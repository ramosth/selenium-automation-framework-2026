package com.test.automation.tests.unit;

import java.math.BigDecimal;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.test.automation.utils.PriceUtils;

/**
 * Testes unitários do PriceUtils (sem navegador, não estende o BaseTest).
 * Exercício do Quality Gate: cobrem as linhas novas do PR.
 */
public class PriceUtilsTest {

    // ---------- parse ----------

    @Test
    public void parseLePrecoSimples() {
        Assert.assertEquals(PriceUtils.parse("$29.99"), new BigDecimal("29.99"));
    }

    @Test
    public void parseLePrecoComRotulo() {
        Assert.assertEquals(PriceUtils.parse("Item total: $39.98"), new BigDecimal("39.98"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void parseRecusaTextoNulo() {
        PriceUtils.parse(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void parseRecusaTextoSemCifrao() {
        PriceUtils.parse("29.99");
    }

    // ---------- sum ----------

    @Test
    public void sumSomaOsPrecos() {
        List<BigDecimal> precos = List.of(new BigDecimal("29.99"), new BigDecimal("9.99"));
        Assert.assertEquals(PriceUtils.sum(precos), new BigDecimal("39.98"));
    }

    @Test
    public void sumDeListaVaziaEZero() {
        Assert.assertEquals(PriceUtils.sum(List.of()), BigDecimal.ZERO);
    }

    // ---------- tax / total ----------

    @Test
    public void taxArredondaEmDuasCasas() {
        // 39.98 x 0.08 = 3.1984 -> 3.20
        Assert.assertEquals(PriceUtils.tax(new BigDecimal("39.98"), new BigDecimal("0.08")),
                new BigDecimal("3.20"));
    }

    @Test
    public void totalSomaSubtotalEImposto() {
        Assert.assertEquals(PriceUtils.total(new BigDecimal("39.98"), new BigDecimal("0.08")),
                new BigDecimal("43.18"));
    }

    // ---------- format / sameAmount ----------

    @Test
    public void formatUsaCifraoEDuasCasas() {
        Assert.assertEquals(PriceUtils.format(new BigDecimal("29.9")), "$29.90");
    }

    @Test
    public void sameAmountConsideraIguaisEmCentavos() {
        Assert.assertTrue(PriceUtils.sameAmount(new BigDecimal("29.9"), new BigDecimal("29.90")));
    }

    @Test
    public void sameAmountDetectaValoresDiferentes() {
        Assert.assertFalse(PriceUtils.sameAmount(new BigDecimal("29.99"), new BigDecimal("29.98")));
    }
}