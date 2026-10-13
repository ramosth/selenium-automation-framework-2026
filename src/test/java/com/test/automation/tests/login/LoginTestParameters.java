package com.test.automation.tests.login;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.test.automation.reports.ExtentReportManager;
import com.test.automation.utils.Log;

/**
 * Teste de login com parâmetros vindos do XML do TestNG (suites/login-parameters.xml).
 *
 * Usa os mesmos campos da planilha (testId, scenario, username, password,
 * expectedResult, expectedMessage), então o relatório e os prints saem no
 * mesmo padrão dos dados do Excel e dos fixos: "CT01 - Login com usuário padrão"
 * e testLogin_CT01_<data-hora>.png.
 *
 * Executar SEMPRE pela suíte suites/login-parameters.xml:
 *   mvn test -Dsuite=login-parameters
 * Rodar pelo botão "play" da classe/método não envia os parâmetros do XML;
 * nesse caso o teste é marcado como ignorado (SKIP) com uma mensagem explicando.
 */
public class LoginTestParameters extends BaseLoginTest {

    @Test
    @Parameters({ "testId", "scenario", "username", "password", "expectedResult", "expectedMessage" })
    public void testLogin(@Optional String testId, @Optional String scenario, @Optional String username,
            @Optional String password, @Optional String expectedResult, @Optional("") String expectedMessage) {

        // Sem testId = o teste não foi chamado pelo suites/login-parameters.xml
        if (testId == null) {
            throw new SkipException(
                    "Parâmetros não recebidos. Rode pela suíte suites/login-parameters.xml (mvn test -Dsuite=login-parameters).");
        }

        Log.info("Iniciando " + testId + " - " + scenario + " (dados do suites/login-parameters.xml)");
        test = ExtentReportManager.createTest(testId + " - " + scenario);

        executeLoginScenario(username, password, expectedResult, expectedMessage);
    }
}
