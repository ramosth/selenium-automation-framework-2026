package com.test.automation.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Utilitário de preços do Sauce Demo ("$29.99", subtotal, imposto, total).
 *
 * EXERCÍCIO DO QUALITY GATE: esta classe entra SEM nenhum teste que a use,
 * para o SonarQube Cloud reprovar a condição de cobertura do código novo.
 */
public final class PriceUtils {

    private PriceUtils() {
    }

    /** "$29.99" ou "Item total: $29.99" -> 29.99 */
    public static BigDecimal parse(String texto) {
        if (texto == null || !texto.contains("$")) {
            throw new IllegalArgumentException("Texto sem preço: " + texto);
        }
        String valor = texto.substring(texto.indexOf('$') + 1).trim();
        return new BigDecimal(valor);
    }

    /** Soma uma lista de preços. */
    public static BigDecimal sum(List<BigDecimal> precos) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal preco : precos) {
            total = total.add(preco);
        }
        return total;
    }

    /** Imposto arredondado em 2 casas (taxa 0.08 = 8%). */
    public static BigDecimal tax(BigDecimal subtotal, BigDecimal taxa) {
        return subtotal.multiply(taxa).setScale(2, RoundingMode.HALF_UP);
    }

    /** Total = subtotal + imposto. */
    public static BigDecimal total(BigDecimal subtotal, BigDecimal taxa) {
        return subtotal.add(tax(subtotal, taxa));
    }

    /** 29.9 -> "$29.90" */
    public static String format(BigDecimal valor) {
        return "$" + valor.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    /** Compara dois preços em centavos (29.9 e 29.90 são iguais). */
    public static boolean sameAmount(BigDecimal a, BigDecimal b) {
        return a.setScale(2, RoundingMode.HALF_UP)
                .compareTo(b.setScale(2, RoundingMode.HALF_UP)) == 0;
    }
}