package com.test.automation.data;

import java.util.List;

/**
 * Um cenário do teste de cálculo do resumo do pedido (uma linha do order-scenarios.json).
 * Os nomes dos campos são iguais às chaves do JSON.
 *
 * products é uma lista: cada cenário pode ter quantos produtos precisar.
 */
public record OrderScenario(
        String testId,
        String scenario,
        List<String> products,
        double expectedItemTotal,
        double expectedTax,
        double expectedTotal) {

    /** Mostra só o id do cenário no nome do print (ex.: testOrderSummaryValues_CALC03_...). */
    @Override
    public String toString() {
        return testId;
    }
}