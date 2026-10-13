package com.test.automation.listeners;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ISuiteResult;
import org.testng.ITestContext;

import com.test.automation.config.ConfigReader;
import com.test.automation.utils.EmailUtils;
import com.test.automation.reports.ExtentReportManager;
import com.test.automation.utils.Log;

/**
 * Listener do TestNG que envia o relatório por e-mail quando a suíte termina.
 *
 * Fica registrado no BaseTest com @Listeners, então vale para qualquer teste que herde dele
 * (LoginTest, LoginTestData com Excel ou fixo, LoginTestParameters) e para qualquer forma de
 * execução: mvn test, mvn test -Dsuite=login-parameters ou o botão "play" do VS Code.
 *
 * O TestNG chama o onFinish depois do @AfterSuite, ou seja, depois do extent.flush()
 * já ter gravado o relatório em disco.
 *
 * Para desligar o envio: email.enabled=false no config.properties,
 * ou numa execução específica: mvn test -Demail.enabled=false
 */
public class EmailReportListener implements ISuiteListener {

    @Override
    public void onStart(ISuite suite) {
        // Nada a fazer no início da suíte
    }

    @Override
    public void onFinish(ISuite suite) {
        if (!ConfigReader.getBoolean("email.enabled", false)) {
            Log.info("Envio de e-mail desativado (email.enabled=false).");
            return;
        }

        // Soma os resultados de todos os blocos <test> da suíte
        int passed = 0;
        int failed = 0;
        int skipped = 0;
        for (ISuiteResult result : suite.getResults().values()) {
            ITestContext context = result.getTestContext();
            passed += context.getPassedTests().size();
            failed += context.getFailedTests().size();
            skipped += context.getSkippedTests().size();
        }

        String status = failed > 0 ? "FALHOU" : "PASSOU";
        String subject = "[" + status + "] " + suite.getName() + " - " + passed + " passou, "
                + failed + " falhou, " + skipped + " ignorado(s)";

        String executedAt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        String color = failed > 0 ? "#c0392b" : "#27ae60";
        String body = "<h2 style='color:" + color + "'>Execução " + status + "</h2>"
                + "<p><b>Suíte:</b> " + suite.getName() + "<br><b>Executado em:</b> " + executedAt + "</p>"
                + "<table border='1' cellpadding='6' style='border-collapse:collapse'>"
                + "<tr><th>Passou</th><th>Falhou</th><th>Ignorado</th><th>Total</th></tr>"
                + "<tr align='center'><td>" + passed + "</td><td>" + failed + "</td><td>" + skipped + "</td><td>"
                + (passed + failed + skipped) + "</td></tr></table>"
                + "<p>O relatório completo do Extent Report está em anexo.</p>"
                + "<p>Atenciosamente,<br>Thamires, QA Team.</p>";

        Log.info("Enviando relatório por e-mail: " + subject);
        EmailUtils.sendEmail(subject, body, ExtentReportManager.reportFilePath);
    }
}
