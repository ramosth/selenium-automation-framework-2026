package com.test.automation.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentReportManager {

    private static ExtentReports extent;
    private static ExtentTest test;

    public static ExtentReports getReportInstance() {
        if (extent == null) {
            // Configurações adicionais do ExtentReports podem ser feitas aqui

            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String reportFilePath = "reports/ExtentReport_" + timestamp + ".html";
            // extent.setSystemInfo("Environment", "Test");
            // extent.setSystemInfo("Browser", "Chrome");
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportFilePath);
            sparkReporter.config().setDocumentTitle("Automation Test Report");
            sparkReporter.config().setReportName("Test Execution Report");
            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
        }
        return extent;
    }

    public static ExtentTest createTest(String testName) {
        test = getReportInstance().createTest(testName);
        return test;
    }

    public static String captureScreenshot(WebDriver driver, String screenshotName, String status) {
        // Implementação para capturar a captura de tela e retornar o caminho do arquivo
        // Você pode usar a biblioteca Selenium para capturar a captura de tela
        // e salvar em um diretório específico, retornando o caminho do arquivo.
        try {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String screenshotPath = System.getProperty("user.dir") + "/screenshots/" + status + "/" + screenshotName + "_" + timestamp + ".png";
            System.out.println("Caminho da captura de tela: " + screenshotPath);
            // Criar diretório se não existir
            File screenshotDir = new File(System.getProperty("user.dir") + "/screenshots/" + status);
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(srcFile, new File(screenshotPath));
            return screenshotPath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
