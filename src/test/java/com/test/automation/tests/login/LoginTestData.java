package com.test.automation.tests.login;

import org.testng.annotations.Test;

import com.test.automation.dataproviders.LoginDataProvider;
import com.test.automation.reports.ExtentReportManager;
import com.test.automation.utils.Log;

/**
 * Teste de login orientado a dados (data-driven).
 *
 * Um único método de teste roda uma vez para cada linha de dados.
 * - dataProvider = "loginExcel": lê src/test/resources/testdata/LoginData.xlsx (aba "Login").
 *   Para incluir um cenário novo, basta adicionar uma linha no Excel, sem mexer no código.
 * - dataProvider = "loginFixo": usa os mesmos cenários escritos no LoginDataProvider.
 */
public class LoginTestData extends BaseLoginTest {

    @Test(dataProvider = "loginFixo", dataProviderClass = LoginDataProvider.class)
    public void testLogin(String testId, String scenario, String username, String password,
            String expectedResult, String expectedMessage) {

        Log.info("Iniciando " + testId + " - " + scenario);
        test = ExtentReportManager.createTest(testId + " - " + scenario);

        executeLoginScenario(username, password, expectedResult, expectedMessage);
    }
}
