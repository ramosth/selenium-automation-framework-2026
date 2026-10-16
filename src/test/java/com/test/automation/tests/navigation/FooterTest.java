package com.test.automation.tests.navigation;

import java.time.Year;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.test.automation.base.BaseTest;
import com.test.automation.pages.CartPage;
import com.test.automation.pages.InventoryPage;
import com.test.automation.pages.LoginPage;
import com.test.automation.pages.components.FooterComponent;
import com.test.automation.reports.ExtentReportManager;
import com.test.automation.utils.Log;

/**
 * Requisito: rodapé (FooterComponent): links das redes sociais e texto de copyright,
 * iguais em todas as páginas internas.
 * Cada teste rola até o rodapé antes de validar, para o print do relatório mostrar o componente.
 */
public class FooterTest extends BaseTest {

    private static final String X_URL = "https://x.com/saucelabs";
    private static final String FACEBOOK_URL = "https://www.facebook.com/saucelabs";
    private static final String LINKEDIN_URL = "https://www.linkedin.com/company/sauce-labs/";

    /** Pré-condição: usuário padrão logado na página de produtos. */
    private InventoryPage openInventory(String testName) {
        Log.info("Iniciando " + testName);
        test = ExtentReportManager.createTest(testName);

        InventoryPage inventoryPage = new LoginPage(driver).loginAsStandardUser();
        Assert.assertTrue(inventoryPage.isLoaded(), "A página de produtos não foi exibida após o login.");
        return inventoryPage;
    }

    /** Rola até o rodapé e confere os 3 links de redes sociais. */
    private void checkSocialLinks(FooterComponent footer) {
        footer.scrollIntoView();

        Assert.assertTrue(footer.isVisibleX(), "O link do X não está visível.");
        Assert.assertTrue(footer.isVisibleFacebook(), "O link do Facebook não está visível.");
        Assert.assertTrue(footer.isVisibleLinkedIn(), "O link do LinkedIn não está visível.");

        Assert.assertEquals(footer.getXLinkUrl(), X_URL, "Link do X diferente do esperado.");
        Assert.assertEquals(footer.getFacebookLinkUrl(), FACEBOOK_URL, "Link do Facebook diferente do esperado.");
        Assert.assertEquals(footer.getLinkedInLinkUrl(), LINKEDIN_URL, "Link do LinkedIn diferente do esperado.");

        // Links externos abrem em nova aba, para o usuário não sair da loja
        Assert.assertTrue(footer.socialLinksOpenInNewTab(), "Os links das redes sociais deveriam abrir em nova aba.");
    }

    @Test
    public void testSocialLinks() {
        FooterComponent footer = openInventory("FTR01 - Links das redes sociais").footer();

        checkSocialLinks(footer);
        test.pass("Os 3 links de redes sociais estão visíveis, apontam para os endereços corretos e abrem em nova aba.");
    }

    @Test
    public void testCopyrightWithCurrentYear() {
        FooterComponent footer = openInventory("FTR02 - Copyright com o ano atual").footer();
        footer.scrollIntoView();

        // O site mostra o ano atual; montar o esperado com Year.now() evita o teste quebrar na virada do ano
        String expected = "© " + Year.now().getValue()
                + " Sauce Labs. All Rights Reserved. Terms of Service | Privacy Policy";
        Assert.assertEquals(footer.getCopyRight(), expected, "Texto de copyright diferente do esperado.");
        test.pass("Copyright correto: " + expected);
    }

    @Test
    public void testFooterOnCartPage() {
        InventoryPage inventoryPage = openInventory("FTR03 - Rodapé também aparece no carrinho");

        CartPage cartPage = inventoryPage.header().openCart();
        Assert.assertTrue(cartPage.isLoaded(), "A página do carrinho não foi exibida.");

        checkSocialLinks(cartPage.footer());
        test.pass("O rodapé do carrinho é igual ao da página de produtos.");
    }
}