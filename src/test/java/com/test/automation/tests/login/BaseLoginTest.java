package com.test.automation.tests.login;

import org.testng.Assert;

import com.test.automation.base.BaseTest;
import com.test.automation.pages.LoginPage;
import com.test.automation.utils.Log;

/**
 * Base comum para os testes de login.
 *
 * Concentra as validações (asserts) de "login com sucesso" e "login com erro",
 * para que LoginTest, LoginTestData e LoginTestParameters usem exatamente a mesma regra.
 * Classe abstrata: não tem testes próprios, só é herdada.
 */
public abstract class BaseLoginTest extends BaseTest {

    protected static final String RESULT_SUCCESS = "SUCCESS";
    protected static final String RESULT_ERROR = "ERROR";

    /**
     * Faz o login e decide qual validação aplicar a partir do resultado esperado.
     * É aqui que fica o "if/else": SUCCESS valida a entrada no sistema; ERROR valida a mensagem.
     */
    protected void executeLoginScenario(String username, String password, String expectedResult,
            String expectedMessage) {

        LoginPage loginPage = new LoginPage(driver);
        test.info("Usuário: '" + username + "' | Senha: '" + password + "'");
        loginPage.login(username, password);

        switch (expectedResult.trim().toUpperCase()) {
            case RESULT_SUCCESS -> validateSuccessfulLogin(loginPage);
            case RESULT_ERROR -> validateLoginError(loginPage, expectedMessage);
            default -> throw new IllegalArgumentException(
                    "Valor inválido para expectedResult: '" + expectedResult + "'. Use SUCCESS ou ERROR.");
        }
    }

    /** Login válido: o usuário sai da tela de login e vê a página "Products". */
    protected void validateSuccessfulLogin(LoginPage loginPage) {
        test.info("Resultado esperado: acessar a tela de produtos.");
        Assert.assertTrue(loginPage.isLoginSuccessful(),
                "Era esperado entrar no sistema, mas a tela 'Products' não foi exibida.");
        Log.info("Login realizado com sucesso, tela de produtos exibida.");
        test.pass("Tela de produtos exibida.");
    }

    /** Login inválido: o usuário continua na tela de login e vê a mensagem de erro específica. */
    protected void validateLoginError(LoginPage loginPage, String expectedMessage) {
        test.info("Resultado esperado: permanecer no login com a mensagem '" + expectedMessage + "'.");
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Era esperada uma mensagem de erro, mas nenhuma foi exibida.");
        Assert.assertEquals(loginPage.getErrorMessage(), expectedMessage,
                "A mensagem de erro exibida é diferente da esperada.");
        Assert.assertTrue(loginPage.isOnLoginPage(),
                "Era esperado continuar na tela de login.");
        Log.info("Mensagem de erro validada: " + expectedMessage);
        test.pass("Mensagem de erro correta e usuário mantido na tela de login.");
    }
}
