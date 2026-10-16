package com.test.automation.tests.dynamiccatalog;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.test.automation.base.BaseTest;
import com.test.automation.data.Product;
import com.test.automation.pages.InventoryPage;
import com.test.automation.pages.LoginPage;
import com.test.automation.pages.SliderPage;
import com.test.automation.reports.ExtentReportManager;
import com.test.automation.utils.Log;

/**
 * Requisito: Dynamic Catalog > Slider.
 * - Um ponto para cada produto, com um único ponto ativo.
 * - O slider troca sozinho a cada 2 segundos, na ordem dos pontos.
 * - Clicar num ponto mostra o produto daquele ponto.
 * - Depois do último produto, o slider volta ao primeiro.
 *
 * A posição do ponto é o id do produto, então o enum Product dá os valores esperados.
 */
public class SliderTest extends BaseTest {

    private static final double PRICE_DELTA = 0.001;
    private static final int TOTAL_PRODUCTS = Product.values().length;

    /** Pré-condição: usuário padrão logado, na página do Slider (aberta pelo menu). */
    private SliderPage openSliderPage(String testName) {
        Log.info("Iniciando " + testName);
        test = ExtentReportManager.createTest(testName);

        InventoryPage inventoryPage = new LoginPage(driver).loginAsStandardUser();
        Assert.assertTrue(inventoryPage.isLoaded(), "A página de produtos não foi exibida após o login.");

        SliderPage sliderPage = inventoryPage.header().goToSlider();
        Assert.assertTrue(sliderPage.isLoaded(), "A página do Slider não foi exibida.");
        return sliderPage;
    }

    @Test
    public void testOneDotPerProduct() {
        SliderPage page = openSliderPage("SLD01 - Um ponto para cada produto e só um ativo");

        Assert.assertEquals(page.getDotCount(), TOTAL_PRODUCTS, "Quantidade de pontos diferente do catálogo.");
        Assert.assertEquals(page.getActiveDotCount(), 1, "Deveria haver exatamente um ponto ativo.");
        test.pass("O slider tem " + TOTAL_PRODUCTS + " pontos e um único ativo.");
    }

    @Test
    public void testSliderAdvancesAutomatically() {
        SliderPage page = openSliderPage("SLD02 - Slider troca de produto sozinho");

        int from = page.getActiveDotIndex();
        Assert.assertTrue(page.waitForSlideToChange(from), "O slider não trocou de produto sozinho.");

        // % (resto da divisão) faz a conta "dar a volta": depois do 5 vem o 0
        int expectedNext = (from + 1) % TOTAL_PRODUCTS;
        Assert.assertEquals(page.getActiveDotIndex(), expectedNext, "O slider não foi para o próximo produto.");
        test.pass("O slider trocou sozinho do ponto " + from + " para o " + expectedNext + ".");
    }

    @Test
    public void testDotShowsProduct() {
        SliderPage page = openSliderPage("SLD03 - Clicar no ponto mostra o produto certo");

        for (Product product : Product.values()) {
            SliderPage.Slide slide = page.showSlide(product.getId());

            Assert.assertEquals(slide.index(), product.getId(), "O ponto clicado não ficou ativo: " + product.getName());
            Assert.assertEquals(slide.name(), product.getName(), "Nome errado no ponto " + product.getId());
            Assert.assertEquals(slide.price(), product.getPrice(), PRICE_DELTA, "Preço errado para " + product.getName());
        }
        test.pass("Cada um dos " + TOTAL_PRODUCTS + " pontos mostra o produto certo.");
    }

    @Test
    public void testSliderWrapsFromLastToFirst() {
        SliderPage page = openSliderPage("SLD04 - Depois do último produto, volta ao primeiro");
        int last = TOTAL_PRODUCTS - 1;

        Assert.assertEquals(page.showSlide(last).index(), last, "Não foi possível ir para o último produto.");
        Assert.assertTrue(page.waitForSlideToChange(last), "O slider não saiu do último produto.");

        SliderPage.Slide slide = page.readSlide();
        Assert.assertEquals(slide.index(), 0, "Depois do último produto, o slider deveria voltar ao primeiro.");
        Assert.assertEquals(slide.name(), Product.BIKE_LIGHT.getName(), "O primeiro produto deveria ser o Bike Light.");
        test.pass("Depois do último produto, o slider voltou ao " + slide.name() + ".");
    }
}