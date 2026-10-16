package com.test.automation.tests.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.test.automation.base.BaseTest;
import com.test.automation.data.User;
import com.test.automation.dataproviders.UserDataProvider;
import com.test.automation.pages.InventoryPage;
import com.test.automation.pages.LoginPage;
import com.test.automation.reports.ExtentReportManager;
import com.test.automation.utils.Log;

/**
 * Requisito: ordenação da lista de produtos, verificada para cada usuário do Sauce Demo.
 *
 * Estratégia: depois de ordenar na tela, compara a lista exibida com uma cópia dela
 * ordenada pelo próprio Java. Se forem iguais, a tela está na ordem certa.
 *
 * Os testes verificam o comportamento CORRETO. Falhas esperadas (defeitos de propósito do site):
 * - problem_user: a ordenação não muda nada
 * - error_user: abre um alerta e não ordena
 * - visual_user: os preços exibidos são aleatórios (falha nas ordenações por preço)
 *
 * Por isso este teste roda na suíte inventory.xml e fica fora da regression.xml.
 */
public class ProductSortTest extends BaseTest {

    /** Pré-condição: usuário logado na página de produtos. */
    private InventoryPage openInventory(String testName, User user) {
        Log.info("Iniciando " + testName + " - " + user);
        test = ExtentReportManager.createTest(testName + " - " + user);
        test.info("Usuário: " + user + " (" + user.getDescription() + ")");

        InventoryPage inventoryPage = new LoginPage(driver).loginAs(user);
        Assert.assertTrue(inventoryPage.isLoaded(), "A página de produtos não foi exibida após o login.");
        return inventoryPage;
    }

    /** Ordena e confere que a opção foi aplicada sem alerta de erro. */
    private void sortAndCheckSelected(InventoryPage inventoryPage, String sortOption) {
        inventoryPage.sortBy(sortOption);
        test.info("Ordenação escolhida: " + sortOption);

        String alert = inventoryPage.acceptAlertIfPresent();
        Assert.assertNull(alert, "Apareceu um alerta de erro ao ordenar: " + alert);
        Assert.assertEquals(inventoryPage.getSelectedSort(), sortOption,
                "A opção de ordenação selecionada não mudou.");
    }

    @Test(dataProvider = "loginUsers", dataProviderClass = UserDataProvider.class)
    public void testDefaultSortIsNameAToZ(User user) {
        InventoryPage inventoryPage = openInventory("ORD01 - Ordenação padrão é por nome (A a Z)", user);

        Assert.assertEquals(inventoryPage.getSelectedSort(), InventoryPage.SORT_NAME_A_TO_Z,
                "A ordenação padrão deveria ser 'Name (A to Z)'.");

        List<String> names = inventoryPage.getProductNames();
        List<String> expected = new ArrayList<>(names);
        Collections.sort(expected);
        Assert.assertEquals(names, expected, "Os produtos não estão em ordem alfabética.");
    }

    @Test(dataProvider = "loginUsers", dataProviderClass = UserDataProvider.class)
    public void testSortByNameZToA(User user) {
        InventoryPage inventoryPage = openInventory("ORD02 - Ordenar por nome (Z a A)", user);
        sortAndCheckSelected(inventoryPage, InventoryPage.SORT_NAME_Z_TO_A);

        List<String> names = inventoryPage.getProductNames();
        List<String> expected = new ArrayList<>(names);
        expected.sort(Comparator.reverseOrder());
        Assert.assertEquals(names, expected, "Os produtos não estão em ordem alfabética decrescente.");
    }

    @Test(dataProvider = "loginUsers", dataProviderClass = UserDataProvider.class)
    public void testSortByPriceLowToHigh(User user) {
        InventoryPage inventoryPage = openInventory("ORD03 - Ordenar por preço (menor para maior)", user);
        sortAndCheckSelected(inventoryPage, InventoryPage.SORT_PRICE_LOW_TO_HIGH);

        List<Double> prices = inventoryPage.getProductPrices();
        test.info("Preços exibidos: " + prices);
        List<Double> expected = new ArrayList<>(prices);
        Collections.sort(expected);
        Assert.assertEquals(prices, expected, "Os preços não estão em ordem crescente.");
    }

    @Test(dataProvider = "loginUsers", dataProviderClass = UserDataProvider.class)
    public void testSortByPriceHighToLow(User user) {
        InventoryPage inventoryPage = openInventory("ORD04 - Ordenar por preço (maior para menor)", user);
        sortAndCheckSelected(inventoryPage, InventoryPage.SORT_PRICE_HIGH_TO_LOW);

        List<Double> prices = inventoryPage.getProductPrices();
        test.info("Preços exibidos: " + prices);
        List<Double> expected = new ArrayList<>(prices);
        expected.sort(Comparator.reverseOrder());
        Assert.assertEquals(prices, expected, "Os preços não estão em ordem decrescente.");
    }
}