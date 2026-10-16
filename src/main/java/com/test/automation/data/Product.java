package com.test.automation.data;

/**
 * Produtos do catálogo do Sauce Demo.
 * Use quando o teste precisa de um produto qualquer pelo nome, sem ser dirigido por massa de dados.
 * Uso: inventoryPage.addProductToCart(Product.BACKPACK.getName())
 */
public enum Product {

    BACKPACK("Sauce Labs Backpack", 4, 29.99),
    BIKE_LIGHT("Sauce Labs Bike Light", 0, 9.99),
    BOLT_T_SHIRT("Sauce Labs Bolt T-Shirt", 1, 15.99),
    FLEECE_JACKET("Sauce Labs Fleece Jacket", 5, 49.99),
    ONESIE("Sauce Labs Onesie", 2, 7.99),
    RED_T_SHIRT("Test.allTheThings() T-Shirt (Red)", 3, 15.99);

    private final String name;
    private final int id;
    private final double price;

    Product(String name, int id, double price) {
        this.name = name;
        this.id = id;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    /** Id que aparece na URL da página de detalhes (inventory-item.html?id=4). */
    public int getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return name;
    }
}