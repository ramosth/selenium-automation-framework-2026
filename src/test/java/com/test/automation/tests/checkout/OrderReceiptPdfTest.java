package com.test.automation.tests.checkout;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.test.automation.base.BaseTest;
import com.test.automation.data.OrderScenario;
import com.test.automation.dataproviders.CheckoutDataProvider;
import com.test.automation.pages.CheckoutCompletePage;
import com.test.automation.pages.CheckoutInformationPage;
import com.test.automation.pages.CheckoutOverviewPage;
import com.test.automation.pages.InventoryPage;
import com.test.automation.pages.LoginPage;
import com.test.automation.reports.ExtentReportManager;
import com.test.automation.utils.DownloadUtils;
import com.test.automation.utils.Log;
import com.test.automation.utils.PdfUtils;

/**
 * Requisito: recibo do pedido em PDF ("Generate PDF order", na tela de pedido concluído).
 * Dados: os mesmos pedidos do order-scenarios.json usados no cálculo do resumo.
 *
 * Datas:
 *   - nome do arquivo: data e hora do pedido em UTC (ex.: swag-labs-order-2026-10-15_21-52-47.pdf)
 *   - conteúdo do PDF: data e hora no horário local, em inglês (ex.: October 15, 2026 at 6:52 PM)
 */
public class OrderReceiptPdfTest extends BaseTest {

    private static final String FIRST_NAME = "Maria";
    private static final String LAST_NAME = "Silva";
    private static final String POSTAL_CODE = "20000-000";

    private static final String FILE_PREFIX = "swag-labs-order-";
    private static final String FILE_EXTENSION = ".pdf";
    private static final int DOWNLOAD_TIMEOUT_SECONDS = 20;

    // Formato da data no nome do arquivo (UTC) e dentro do PDF (horário local, em inglês)
    private static final DateTimeFormatter FILE_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final DateTimeFormatter RECEIPT_DATE = DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a", Locale.US);

    private static final String FOOTER_TEXT =
            "Thank you for your order! It has been dispatched, and will arrive just as fast as the pony can get there.";

    @Test(dataProvider = "orderScenarios", dataProviderClass = CheckoutDataProvider.class)
    public void testOrderReceiptPdf(OrderScenario scenario) {
        Log.info("Iniciando " + scenario.testId() + " - Recibo em PDF - " + scenario.scenario());
        test = ExtentReportManager.createTest(scenario.testId() + " - Recibo em PDF - " + scenario.scenario());
        DownloadUtils.cleanDownloadDir();

        // Pré-condição: pedido montado até a tela de resumo
        InventoryPage inventoryPage = new LoginPage(driver).loginAsStandardUser();
        Assert.assertTrue(inventoryPage.isLoaded(), "A página de produtos não foi exibida após o login.");
        for (String product : scenario.products()) {
            inventoryPage.addProductToCart(product);
        }

        CheckoutInformationPage informationPage = inventoryPage.header().openCart().goToCheckout();
        Assert.assertTrue(informationPage.isLoaded(), "A tela de dados do checkout não foi exibida.");
        informationPage.fillInformation(FIRST_NAME, LAST_NAME, POSTAL_CODE);

        CheckoutOverviewPage overviewPage = informationPage.continueToOverview();
        Assert.assertTrue(overviewPage.isLoaded(), "A tela de resumo do pedido não foi exibida.");

        // Guarda o que a tela mostrou: o PDF tem que trazer os mesmos itens e preços
        List<String> screenNames = overviewPage.getItemNames();
        List<Double> screenPrices = overviewPage.getItemPrices();

        // A data do pedido é registrada no Finish: guardamos o horário desse momento
        ZonedDateTime orderTime = ZonedDateTime.now();
        CheckoutCompletePage completePage = overviewPage.finish();
        Assert.assertTrue(completePage.isLoaded(), "A tela de pedido concluído não foi exibida.");
        Assert.assertTrue(completePage.isGeneratePdfButtonDisplayed(), "O botão 'Generate PDF order' não apareceu.");

        // Gera e espera o download
        completePage.clickGeneratePdfOrder();
        File pdf = DownloadUtils.waitForFile(FILE_PREFIX, FILE_EXTENSION, DOWNLOAD_TIMEOUT_SECONDS);
        Assert.assertNotNull(pdf, "O PDF não foi baixado em " + DOWNLOAD_TIMEOUT_SECONDS + " segundos.");
        test.info("PDF baixado: " + pdf.getName());

        // 1. Nome do arquivo: data do pedido em UTC
        validateFileName(pdf.getName(), orderTime);

        // 2. Conteúdo do PDF
        String text = PdfUtils.readText(pdf);
        test.info("Texto do PDF: " + text);

        // Textos fixos
        for (String fixedText : List.of("Swag Labs", "Order Receipt", "ORDER DETAILS", "Order Date",
                "SHIP TO", "ITEMS", "DESCRIPTION", "PRICE", FOOTER_TEXT)) {
            assertContains(text, fixedText, "Texto fixo ausente no PDF");
        }

        // Comprador
        assertContains(text, FIRST_NAME + " " + LAST_NAME, "Nome do comprador ausente no PDF");
        assertContains(text, POSTAL_CODE, "CEP ausente no PDF");

        // Itens: mesmos nomes e preços da tela de resumo
        for (int i = 0; i < screenNames.size(); i++) {
            assertContains(text, screenNames.get(i) + " " + money(screenPrices.get(i)), "Item ausente no PDF");
        }

        // Valores do pedido
        assertContains(text, "Item total " + money(scenario.expectedItemTotal()), "Item total incorreto no PDF");
        assertContains(text, "Tax " + money(scenario.expectedTax()), "Taxa incorreta no PDF");
        assertContains(text, "Total " + money(scenario.expectedTotal()), "Total incorreto no PDF");

        // Data do pedido no horário local, com tolerância de 1 minuto para mais ou para menos
        Assert.assertTrue(containsOrderDate(text, orderTime),
                "Data do pedido ausente ou fora da tolerância. Esperado perto de: " + orderTime.format(RECEIPT_DATE));

        test.pass("Recibo em PDF conferido: nome do arquivo, textos, comprador, itens, valores e data.");
    }

    /** O nome do arquivo traz a data do pedido em UTC; ela deve estar a até 2 minutos do Finish. */
    private void validateFileName(String fileName, ZonedDateTime orderTime) {
        Assert.assertTrue(fileName.startsWith(FILE_PREFIX) && fileName.endsWith(FILE_EXTENSION),
                "Nome do arquivo fora do padrão: " + fileName);

        String timestamp = fileName.substring(FILE_PREFIX.length(), fileName.length() - FILE_EXTENSION.length());
        Instant fileInstant = LocalDateTime.parse(timestamp, FILE_DATE).atZone(ZoneOffset.UTC).toInstant();
        long differenceSeconds = Math.abs(Duration.between(orderTime.toInstant(), fileInstant).getSeconds());

        test.info("Data no nome do arquivo (UTC): " + timestamp + " | diferença do Finish: " + differenceSeconds + "s");
        Assert.assertTrue(differenceSeconds <= 120,
                "A data no nome do arquivo (" + timestamp + " UTC) está longe do horário do pedido.");
    }

    /** Procura a data do pedido no texto, aceitando 1 minuto antes ou depois. */
    private boolean containsOrderDate(String text, ZonedDateTime orderTime) {
        for (int minutes = -1; minutes <= 1; minutes++) {
            if (text.contains(orderTime.plusMinutes(minutes).format(RECEIPT_DATE))) {
                return true;
            }
        }
        return false;
    }

    /** Valor no formato do recibo: 29.99 -> "$29.99" (sempre com ponto e 2 casas). */
    private String money(double value) {
        return String.format(Locale.US, "$%.2f", value);
    }

    private void assertContains(String text, String expected, String message) {
        Assert.assertTrue(text.contains(expected), message + ": '" + expected + "'");
    }
}