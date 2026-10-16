package com.test.automation.tests.inventory;

import org.testng.annotations.Test;

import com.test.automation.data.ProductScenario;
import com.test.automation.dataproviders.ProductDataProvider;

/**
 * Detalhes do produto com o usuário padrão (product-scenarios.json).
 * Deve passar sempre: faz parte da regressão.
 */
public class ProductDetailsTest extends BaseProductDetailsTest {

    @Test(dataProvider = "standardUserScenarios", dataProviderClass = ProductDataProvider.class)
    public void testProductDetails(ProductScenario scenario) {
        validateProductDetails(scenario);
    }

    @Test(dataProvider = "standardUserScenarios", dataProviderClass = ProductDataProvider.class)
    public void testAddAndRemoveFromDetails(ProductScenario scenario) {
        validateAddAndRemove(scenario);
    }
}