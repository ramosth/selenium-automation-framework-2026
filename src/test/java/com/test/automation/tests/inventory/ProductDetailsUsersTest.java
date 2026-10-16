package com.test.automation.tests.inventory;

import org.testng.annotations.Test;

import com.test.automation.data.ProductScenario;
import com.test.automation.dataproviders.ProductDataProvider;

/**
 * Detalhes do produto com os outros usuários (product-scenarios-users.json).
 * Cada usuário testa o primeiro e o último produto (limites da faixa de ids).
 *
 * Falhas esperadas (defeitos de propósito do site): problem_user, error_user e visual_user.
 * Por isso fica fora da regressão: roda na suíte product-users.xml.
 */
public class ProductDetailsUsersTest extends BaseProductDetailsTest {

    @Test(dataProvider = "otherUsersScenarios", dataProviderClass = ProductDataProvider.class)
    public void testProductDetails(ProductScenario scenario) {
        validateProductDetails(scenario);
    }

    @Test(dataProvider = "otherUsersScenarios", dataProviderClass = ProductDataProvider.class)
    public void testAddAndRemoveFromDetails(ProductScenario scenario) {
        validateAddAndRemove(scenario);
    }
}