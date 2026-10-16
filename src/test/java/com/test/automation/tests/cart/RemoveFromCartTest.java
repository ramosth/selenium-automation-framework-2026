package com.test.automation.tests.cart;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.test.automation.base.BaseTest;
import com.test.automation.data.Product;
import com.test.automation.pages.CartPage;
import com.test.automation.pages.InventoryPage;
import com.test.automation.pages.LoginPage;
import com.test.automation.reports.ExtentReportManager;
import com.test.automation.utils.Log;

/**
 * Requisito: remover produtos do carrinho, pela lista de produtos e pela página do carrinho.
 */
public class RemoveFromCartTest extends BaseTest {

    /** Pré-condição: usuário padrão logado, com os produtos informados no carrinho. */
    private InventoryPage openInventoryWithProducts(String testName, Product... products) {
        Log.info("Iniciando " + testName);
        test = ExtentReportManager.createTest(testName);

        InventoryPage inventoryPage = new LoginPage(driver).loginAsStandardUser();
        Assert.assertTrue(inventoryPage.isLoaded(), "A página de produtos não foi exibida após o login.");

        for (Product product : products) {
            inventoryPage.addProductToCart(product.getName());
        }
        test.info("Produtos adicionados: " + List.of(products));
        Assert.assertEquals(inventoryPage.header().getCartBadgeCount(), products.length,
                "Quantidade no carrinho diferente da esperada após adicionar.");
        return inventoryPage;
    }

    @Test
    public void testRemoveFromInventoryPage() {
        InventoryPage inventoryPage = openInventoryWithProducts("RMV01 - Remover pela lista de produtos",
                Product.BACKPACK, Product.BIKE_LIGHT, Product.FLEECE_JACKET);

        Assert.assertTrue(inventoryPage.isProductInCart(Product.BACKPACK.getName()),
                "O botão do produto deveria mostrar 'Remove' antes de remover.");

        inventoryPage.removeProductFromCart(Product.BACKPACK.getName());
        Assert.assertEquals(inventoryPage.header().getCartBadgeCount(), 2, "O contador deveria diminuir para 2.");
        Assert.assertFalse(inventoryPage.isProductInCart(Product.BACKPACK.getName()),
                "O botão do produto deveria voltar para 'Add to cart'.");

        CartPage cartPage = inventoryPage.header().openCart();
        Assert.assertTrue(cartPage.isLoaded(), "A página do carrinho não foi exibida.");
        Assert.assertEquals(cartPage.getItemNames(), List.of(Product.BIKE_LIGHT.getName(), Product.FLEECE_JACKET.getName()),
                "O carrinho deveria ter só o produto que não foi removido.");
        test.pass("Produto removido pela lista de produtos.");
    }

    @Test
    public void testRemoveFromCartPage() {
        InventoryPage inventoryPage = openInventoryWithProducts("RMV02 - Remover pela página do carrinho",
                Product.BACKPACK, Product.BIKE_LIGHT);

        CartPage cartPage = inventoryPage.header().openCart();
        Assert.assertTrue(cartPage.isLoaded(), "A página do carrinho não foi exibida.");

        cartPage.removeProductFromCart(Product.BACKPACK.getName());
        Assert.assertFalse(cartPage.isProductInCart(Product.BACKPACK.getName()),
                "O produto removido não deveria continuar no carrinho.");
        Assert.assertEquals(cartPage.getItemCount(), 1, "O carrinho deveria ter 1 item.");
        Assert.assertEquals(cartPage.header().getCartBadgeCount(), 1, "O contador deveria diminuir para 1.");

        InventoryPage backPage = cartPage.continueShopping();
        Assert.assertFalse(backPage.isProductInCart(Product.BACKPACK.getName()),
                "Na lista de produtos, o botão do produto removido deveria voltar para 'Add to cart'.");
        test.pass("Produto removido pela página do carrinho.");
    }

    @Test
    public void testRemoveAllProducts() {
        InventoryPage inventoryPage = openInventoryWithProducts("RMV03 - Remover todos os produtos",
                Product.BACKPACK, Product.BIKE_LIGHT);

        CartPage cartPage = inventoryPage.header().openCart();
        Assert.assertTrue(cartPage.isLoaded(), "A página do carrinho não foi exibida.");

        cartPage.removeProductFromCart(Product.BACKPACK.getName());
        cartPage.removeProductFromCart(Product.BIKE_LIGHT.getName());

        Assert.assertEquals(cartPage.getItemCount(), 0, "O carrinho deveria estar vazio.");
        Assert.assertEquals(cartPage.header().getCartBadgeCount(), 0, "O contador deveria desaparecer (0).");
        test.pass("Todos os produtos removidos: carrinho vazio e sem contador.");
    }
}