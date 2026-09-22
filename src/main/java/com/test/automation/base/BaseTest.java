package com.test.automation.base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.test.automation.utils.ExtentReportManager;
import com.test.automation.utils.Log;

public class BaseTest {
    
    protected WebDriver driver;
    protected static ExtentReports extent;
    protected ExtentTest test;

    @BeforeSuite 
    public void setupReport() {
        Log.info("Configurando o Extent Report antes da execução dos testes.");
        // Configurações do Extent Report podem ser feitas aqui
        extent = ExtentReportManager.getReportInstance();
    }

    @AfterSuite 
    public void tearDownReport() {
        Log.info("Finalizando o Extent Report após a execução dos testes.");
        // Finaliza o Extent Report
        extent.flush();
    }

    @BeforeMethod
    public void setUp() {

        Log.info("Iniciando Web Driver e configurando o ambiente.");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        Log.info("Navegando para a página de login.");
        driver.get("https://www.saucedemo.com/"); // Substitua pelo URL do seu aplicativo
    }

    // verificamos se o driver não é nulo antes de chamar quit() para garantir que só tentaremos fechar o navegador se ele tiver sido aberto com sucesso.
    @AfterMethod 
    public void tearDown(ITestResult result) {
        Log.info("Finalizando o teste.");

        if(result.getStatus() == ITestResult.FAILURE) {
            Log.error("O teste falhou: " + result.getName());
            String screenshotPath = ExtentReportManager.captureScreenshot(driver, result.getName(), "FAILURE");
            test.fail("Falha no teste. Captura de tela anexada.", MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            Log.info("O teste passou: " + result.getName());
            ExtentReportManager.captureScreenshot(driver, result.getName(), "SUCCESS");
            test.pass("Teste concluído com sucesso.");
        } else if (result.getStatus() == ITestResult.SKIP) {
            Log.warn("O teste foi ignorado: " + result.getName());
            String screenshotPath = ExtentReportManager.captureScreenshot(driver, result.getName(), "SKIP");
            test.skip("Teste ignorado. Captura de tela anexada.", MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
        }

        if (driver != null) {
            Log.info("Limpando o ambiente de testes e fechando o navegador.");
            driver.quit();
        }
    }
}
