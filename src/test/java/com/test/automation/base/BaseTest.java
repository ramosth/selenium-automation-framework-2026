package com.test.automation.base;

import java.util.Arrays;

import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.test.automation.config.ConfigReader;
import com.test.automation.driver.DriverFactory;
import com.test.automation.listeners.EmailReportListener;
import com.test.automation.reports.ExtentReportManager;
import com.test.automation.utils.Log;
import com.test.automation.utils.ScreenshotUtils;

/**
 * Classe base de todos os testes.
 *
 * Ciclo de vida:
 * 
 * @BeforeSuite -> cria o Extent Report (uma vez por execução)
 * @BeforeMethod -> abre o navegador na URL da aplicação (antes de cada teste)
 * @AfterMethod -> registra o resultado no relatório, tira o print e fecha o
 *              navegador
 * @AfterSuite -> grava o relatório em disco
 *             EmailReportListener -> envia o relatório por e-mail ao final da
 *             suíte
 */
@Listeners(EmailReportListener.class)
public class BaseTest {

    protected WebDriver driver;
    protected static ExtentReports extent;
    protected ExtentTest test;

    @BeforeSuite
    public void setupReport() {
        Log.info("Configurando o Extent Report antes da execução dos testes.");
        extent = ExtentReportManager.getReportInstance();
    }

    @AfterSuite
    public void tearDownReport() {
        Log.info("Finalizando o Extent Report após a execução dos testes.");
        // Grava todos os dados no arquivo de saída
        extent.flush();
        Log.info("Relatório final gerado em: " + ExtentReportManager.reportFilePath);
        // O envio por e-mail acontece logo depois, no EmailReportListener
    }

    @BeforeMethod
    public void setUp() {
        driver = DriverFactory.createDriver();
        String baseUrl = ConfigReader.get("base.url", "https://www.saucedemo.com/");
        Log.info("Navegando para: " + baseUrl);
        driver.get(baseUrl);
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        Log.info("Finalizando o teste.");

        // Se o teste quebrar antes de criar o registro no relatório, cria aqui para não
        // dar NullPointerException
        if (test == null) {
            test = ExtentReportManager.createTest(result.getName());
        }

        // No data-driven o nome do método é o mesmo em todas as linhas (ex.:
        // testLogin),
        // então o primeiro parâmetro (ex.: CT07) entra no nome do print para não
        // confundir
        String screenshotName = buildScreenshotName(result);
        if (result.getParameters().length > 0) {
            Log.info("Parâmetros do teste: " + Arrays.toString(result.getParameters()));
        }

        if (result.getStatus() == ITestResult.FAILURE) {
            Log.error("O teste falhou: " + screenshotName + " - " + result.getThrowable().getMessage());
            test.fail(result.getThrowable()); // mostra no relatório o motivo da falha (mensagem do Assert)
            String screenshotPath = ScreenshotUtils.capture(driver, screenshotName, "FAILURE");
            test.fail("Falha no teste. Captura de tela anexada.",
                    MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            Log.info("O teste passou: " + screenshotName);
            ScreenshotUtils.capture(driver, screenshotName, "SUCCESS");
            test.pass("Teste concluído com sucesso.");
        } else if (result.getStatus() == ITestResult.SKIP) {
            Log.warn("O teste foi ignorado: " + screenshotName);
            String screenshotPath = ScreenshotUtils.capture(driver, screenshotName, "SKIP");
            test.skip("Teste ignorado. Captura de tela anexada.",
                    MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
        }

        // Só fecha o navegador se ele tiver sido aberto com sucesso
        if (driver != null) {
            Log.info("Limpando o ambiente de testes e fechando o navegador.");
            driver.quit();
        }
        test = null; // limpa para a próxima execução (importante no data-driven, que reaproveita a
                     // mesma instância)
    }

    /**
     * Nome do print: método + id do cenário + 1º parâmetro (quando houver).
     * O id (ex.: FTR01, CT07) é o começo do nome do teste no relatório, antes do "
     * - ".
     * Exemplos: testSocialLinks_FTR01 | testLogin_CT07 |
     * testSortByNameZToA_ORD02_problem_user
     */
    private String buildScreenshotName(ITestResult result) {
        String name = result.getName();

        String id = test.getModel().getName().split(" - ")[0];
        if (id.matches("[A-Z]+\\d+")) {
            name += "_" + id;
        }

        if (result.getParameters().length > 0) {
            String firstParameter = String.valueOf(result.getParameters()[0]);
            if (!firstParameter.equals(id)) {
                name += "_" + firstParameter;
            }
        }
        return name;
    }
}
