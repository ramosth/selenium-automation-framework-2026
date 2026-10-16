package com.test.automation.dataproviders;

import org.testng.annotations.DataProvider;

/**
 * Dados do Dynamic Catalog.
 *
 * Lazy Load: a lista tem 22 itens e recomeça do primeiro depois do último.
 * Em vez de conferir todos, os cenários usam Análise de Valor Limite:
 * início da lista, fim do primeiro lote, início do segundo lote (o primeiro carregado
 * pela rolagem), último item da lista e o primeiro depois dele (a lista recomeça).
 */
public class DynamicCatalogDataProvider {

    @DataProvider(name = "lazyLoadItems")
    public static Object[][] lazyLoadItems() {
        return new Object[][] {
                // testId, cenário, posição do card, nome esperado, preço esperado
                { "LZY03", "Primeiro card da lista", 0, "Sauce Labs Bike Light", 9.99 },
                { "LZY04", "Último card do primeiro lote", 5, "Sauce Labs Fleece Jacket (XS)", 49.99 },
                { "LZY05", "Primeiro card carregado pela rolagem", 6, "Sauce Labs Fleece Jacket (S)", 49.99 },
                { "LZY06", "Último item da lista", 21, "Sauce Labs Bolt T-Shirt (S)", 15.99 },
                { "LZY07", "A lista recomeça depois do último item", 22, "Sauce Labs Bike Light", 9.99 },
        };
    }
}