package com.test.automation.tests.login;

import org.testng.annotations.Test;

import com.test.automation.pages.LoginPage;
import com.test.automation.reports.ExtentReportManager;
import com.test.automation.utils.Log;

/**
 * Testes de login com dados fixos, um método por cenário (versão da primeira aula).
 * A versão orientada a dados, que cobre todos os cenários, está em LoginTestData.
 */
public class LoginTest extends BaseLoginTest {

    @Test
    public void testValidLogin() {
        Log.info("Iniciando o teste de login válido.");
        test = ExtentReportManager.createTest("Teste de Login Válido");

        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("standard_user", "secret_sauce");
        validateSuccessfulLogin(loginPage);
    }

    @Test
    public void testInvalidLogin() {
        Log.info("Iniciando o teste de login inválido.");
        test = ExtentReportManager.createTest("Teste de Login Inválido");

        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("invalid_user", "invalid_password");
        validateLoginError(loginPage, "Epic sadface: Username and password do not match any user in this service");
    }

    @Test
    public void testEmptyLogin() {
        Log.info("Iniciando o teste de login com campos vazios.");
        test = ExtentReportManager.createTest("Teste de Login com Campos Vazios");

        LoginPage loginPage = new LoginPage(driver);
        loginPage.clickLoginButton();
        validateLoginError(loginPage, "Epic sadface: Username is required");
    }
}
