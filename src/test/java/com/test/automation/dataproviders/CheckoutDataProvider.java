package com.test.automation.dataproviders;

import org.testng.annotations.DataProvider;

import com.test.automation.data.OrderScenario;
import com.test.automation.utils.ExcelUtils;
import com.test.automation.utils.JsonUtils;

/**
 * Fontes de dados do fluxo de compra (carrinho e checkout).
 *
 * Excel (CheckoutData.xlsx), uma aba por funcionalidade:
 * - aba "Cart":     testId | scenario | products | expectedCount   (products = nomes separados por ";")
 * - aba "Checkout": testId | scenario | firstName | lastName | postalCode | expectedResult | expectedMessage
 *
 * JSON (order-scenarios.json):
 * - cenários do cálculo do resumo do pedido (item total, taxa e total)
 */
public class CheckoutDataProvider {

    private static final String TESTDATA_DIR = System.getProperty("user.dir") + "/src/test/resources/testdata/";
    private static final String EXCEL_PATH = TESTDATA_DIR + "CheckoutData.xlsx";

    private CheckoutDataProvider() {
    }

    @DataProvider(name = "cartExcel")
    public static Object[][] cartExcel() {
        return ExcelUtils.getSheetData(EXCEL_PATH, "Cart");
    }

    @DataProvider(name = "checkoutExcel")
    public static Object[][] checkoutExcel() {
        return ExcelUtils.getSheetData(EXCEL_PATH, "Checkout");
    }

    /** Cenários do cálculo do resumo do pedido (um cenário por linha). */
    @DataProvider(name = "orderScenarios")
    public static Object[][] orderScenarios() {
        return JsonUtils.readList(TESTDATA_DIR + "order-scenarios.json", OrderScenario.class).stream()
                .map(scenario -> new Object[] { scenario })
                .toArray(Object[][]::new);
    }
}