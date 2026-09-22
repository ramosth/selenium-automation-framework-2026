package com.test.automation.tests;

import org.testng.annotations.Test;

import com.test.automation.base.BaseTest;
import com.test.automation.pages.LoginPage;
import com.test.automation.utils.ExtentReportManager;
import com.test.automation.utils.Log;

public class LoginTest extends BaseTest {
    // Aqui você pode adicionar métodos de teste específicos para a página de login
    
    @Test 
    public void testValidLogin() {
        Log.info("Iniciando os testes de login válido.");
        // Exemplo de teste de login
        // Você pode usar a classe LoginPage para interagir com os elementos da página de login
        test = ExtentReportManager.createTest("Teste de Login Válido");

        LoginPage loginPage = new LoginPage(driver);
        test.info("Navegando para a página de login.");
        loginPage.login("standard_user", "secret_sauce");
        test.info("Entrando com credenciais válidas e clicando no botão de login.");

        test.info("Verificando o título da página");
        loginPage.isLoginSuccessful(); // Aqui você pode verificar se o login foi bem-sucedido
        Log.info("Teste de login válido finalizado com sucesso.");
        test.pass("Login válido realizado com sucesso.");
    }

    @Test
    public void testInvalidLogin() {
        Log.info("Iniciando os testes de login inválido.");
        // Exemplo de teste de login inválido
        test = ExtentReportManager.createTest("Teste de Login Inválido");
        LoginPage loginPage = new LoginPage(driver);
        test.info("Iniciando o teste de login inválido.");
        loginPage.login("invalid_user", "invalid_password");
        test.info("Entrando com credenciais inválidas e clicando no botão de login.");
        loginPage.isErrorMessageDisplayed(); // Aqui você pode verificar se a mensagem de erro está visível
        loginPage.getErrorMessage(); // Aqui você pode verificar o texto da mensagem de erro
        loginPage.getErrorMessageText(); // Aqui você pode verificar o texto da mensagem de erro
        loginPage.errorMessageContainsText(loginPage.errorMessage, "Epic sadface: Username and password do not match any user in this service"); // Aqui você pode verificar se a mensagem de erro contém o texto esperado
        Log.info("Teste de login inválido finalizado com sucesso.");
        test.pass("Login inválido realizado com sucesso.");
    }
    
    @Test 
    public void testEmptyLogin() {
        Log.info("Iniciando os testes de login com campos vazios.");
        // Exemplo de teste de login com campos vazios
        test = ExtentReportManager.createTest("Teste de Login com Campos Vazios");
        LoginPage loginPage = new LoginPage(driver);
        test.info("Iniciando o teste de login com campos vazios.");

        loginPage.clickLoginButton();
        loginPage.getErrorMessageText(); // Aqui você pode verificar a mensagem de erro exibida
        test.info("Verificando a mensagem de erro exibida.");
        loginPage.errorMessageContainsText(loginPage.errorMessage, "Epic sadface: Username is required"); // Aqui você pode verificar se a mensagem de erro contém o texto esperado
        Log.info("Teste de login com campos vazios finalizado com sucesso.");
        test.pass("Login com campos vazios realizado com sucesso.");
    }
}
