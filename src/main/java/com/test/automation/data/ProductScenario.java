package com.test.automation.data;

/**
 * Um cenário do teste de detalhes do produto (uma linha dos arquivos product-scenarios*.json).
 * Os nomes dos campos são iguais às chaves do JSON.
 *
 * O campo "user" no JSON usa o nome da constante do enum User (ex.: "PROBLEM"),
 * e o Jackson converte o texto para o enum automaticamente.
 */
public record ProductScenario(
        String testId,
        String scenario,
        User user,
        String productName,
        int expectedId,
        double expectedPrice,
        String expectedDescription) {

    /** Mostra só o id do cenário no nome do print (ex.: testProductDetails_PD07_...). */
    @Override
    public String toString() {
        return testId;
    }
}