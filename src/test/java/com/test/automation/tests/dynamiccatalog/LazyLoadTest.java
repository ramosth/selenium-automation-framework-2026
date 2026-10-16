package com.test.automation.tests.dynamiccatalog;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.test.automation.base.BaseTest;
import com.test.automation.dataproviders.DynamicCatalogDataProvider;
import com.test.automation.pages.InventoryPage;
import com.test.automation.pages.LazyLoadPage;
import com.test.automation.pages.LoginPage;
import com.test.automation.reports.ExtentReportManager;
import com.test.automation.utils.Log;

/**
 * Requisito: Dynamic Catalog > Lazy Load.
 * - Rolar até o fim da lista carrega mais 6 cards (lista infinita).
 * - Um card só mostra o produto quando aparece na tela; antes disso mostra "Loading…".
 * - Os cards seguem a lista do Lazy Load (22 itens, com tamanhos) e recomeçam no fim.
 *
 * A quantidade inicial de cards depende do tamanho da janela, por isso os testes
 * não supõem um número fixo ao abrir a página.
 */
public class LazyLoadTest extends BaseTest {

    private static final double PRICE_DELTA = 0.001;
    // O site usa o caractere de reticências (…), e não três pontos
    private static final String PLACEHOLDER_TEXT = "Loading\u2026";

    /** Pré-condição: usuário padrão logado, na página do Lazy Load (aberta pelo menu). */
    private LazyLoadPage openLazyLoadPage(String testName) {
        Log.info("Iniciando " + testName);
        test = ExtentReportManager.createTest(testName);

        InventoryPage inventoryPage = new LoginPage(driver).loginAsStandardUser();
        Assert.assertTrue(inventoryPage.isLoaded(), "A página de produtos não foi exibida após o login.");

        LazyLoadPage lazyLoadPage = inventoryPage.header().goToLazyLoad();
        Assert.assertTrue(lazyLoadPage.isLoaded(), "A página do Lazy Load não foi exibida.");
        return lazyLoadPage;
    }

    @Test
    public void testMoreCardsLoadedOnScroll() {
        LazyLoadPage page = openLazyLoadPage("LZY01 - Mais cards são carregados ao rolar até o fim");

        int before = page.getCardCount();
        Assert.assertTrue(before >= LazyLoadPage.CARDS_PER_LOAD,
                "A página deveria abrir com pelo menos " + LazyLoadPage.CARDS_PER_LOAD + " cards, mas abriu com " + before);

        Assert.assertTrue(page.loadMore(), "Nenhum card novo foi carregado ao rolar até o fim.");

        int after = page.getCardCount();
        Assert.assertTrue(after >= before + LazyLoadPage.CARDS_PER_LOAD,
                "Deveriam entrar pelo menos " + LazyLoadPage.CARDS_PER_LOAD + " cards. Antes: " + before + ", depois: " + after);
        Assert.assertEquals(after % LazyLoadPage.CARDS_PER_LOAD, 0,
                "Os cards deveriam chegar em lotes de " + LazyLoadPage.CARDS_PER_LOAD + ". Total: " + after);
        test.pass("A lista passou de " + before + " para " + after + " cards ao rolar até o fim.");
    }

    @Test
    public void testCardLoadsOnlyWhenVisible() {
        LazyLoadPage page = openLazyLoadPage("LZY02 - Card só carrega quando aparece na tela");

        int index = page.loadUntilLastCardIsBelowScreen();
        Assert.assertTrue(index >= 0, "Não foi possível deixar um card abaixo da tela para o teste.");

        // Antes de aparecer: placeholder
        Assert.assertTrue(page.isPlaceholderShown(index), "O card " + index + " está fora da tela, mas não mostra o placeholder.");
        Assert.assertEquals(page.getPlaceholderText(index), PLACEHOLDER_TEXT, "Texto do placeholder diferente.");

        // Depois de aparecer: produto
        page.scrollToCard(index);
        Assert.assertTrue(page.waitForCardToLoad(index), "O card " + index + " não carregou ao aparecer na tela.");
        Assert.assertFalse(page.isPlaceholderShown(index), "O card " + index + " carregou, mas o placeholder continua.");
        test.pass("O card " + index + " mostrava '" + PLACEHOLDER_TEXT + "' fora da tela e carregou ao aparecer.");
    }

    @Test(dataProvider = "lazyLoadItems", dataProviderClass = DynamicCatalogDataProvider.class)
    public void testCardContent(String testId, String scenario, int index, String expectedName, double expectedPrice) {
        LazyLoadPage page = openLazyLoadPage(testId + " - " + scenario);

        Assert.assertTrue(page.loadUntilCardExists(index), "O card " + index + " não foi criado mesmo rolando a lista.");
        page.scrollToCard(index);
        Assert.assertTrue(page.waitForCardToLoad(index), "O card " + index + " não carregou.");

        Assert.assertEquals(page.getCardName(index), expectedName, "Nome errado no card " + index);
        Assert.assertEquals(page.getCardPrice(index), expectedPrice, PRICE_DELTA, "Preço errado no card " + index);
        test.pass("Card " + index + ": " + expectedName + " por $" + expectedPrice);
    }
}