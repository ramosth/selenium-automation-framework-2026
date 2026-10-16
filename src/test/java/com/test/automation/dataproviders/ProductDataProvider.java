package com.test.automation.dataproviders;

import org.testng.annotations.DataProvider;

import com.test.automation.data.ProductScenario;
import com.test.automation.utils.JsonUtils;

/**
 * Cenários do teste de detalhes do produto, lidos de JSON (um cenário por linha).
 */
public class ProductDataProvider {

    private static final String TESTDATA_DIR = System.getProperty("user.dir") + "/src/test/resources/testdata/";

    private ProductDataProvider() {
    }

    /** Cenários do usuário padrão (product-scenarios.json). Devem passar sempre: entram na regressão. */
    @DataProvider(name = "standardUserScenarios")
    public static Object[][] standardUserScenarios() {
        return readScenarios("product-scenarios.json");
    }

    /** Cenários dos outros usuários (product-scenarios-users.json). Expõem defeitos conhecidos. */
    @DataProvider(name = "otherUsersScenarios")
    public static Object[][] otherUsersScenarios() {
        return readScenarios("product-scenarios-users.json");
    }

    /** Lê o arquivo e entrega um cenário por linha, no formato que o TestNG exige. */
    private static Object[][] readScenarios(String fileName) {
        return JsonUtils.readList(TESTDATA_DIR + fileName, ProductScenario.class).stream()
                .map(scenario -> new Object[] { scenario })
                .toArray(Object[][]::new);
    }
}