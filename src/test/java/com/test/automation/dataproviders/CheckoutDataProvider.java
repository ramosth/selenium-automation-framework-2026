package com.test.automation.dataproviders;

import org.testng.annotations.DataProvider;

import com.test.automation.utils.ExcelUtils;

/**
 * Fontes de dados do fluxo de compra (carrinho e checkout).
 *
 * Um arquivo Excel por módulo (CheckoutData.xlsx) e uma aba por funcionalidade:
 * - aba "Cart":     testId | scenario | products | expectedCount
 *                   (products = nomes separados por ";")
 * - aba "Checkout": testId | scenario | firstName | lastName | postalCode | expectedResult | expectedMessage
 */
public class CheckoutDataProvider {

    private static final String EXCEL_PATH = System.getProperty("user.dir") + "/src/test/resources/testdata/CheckoutData.xlsx";

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
}
