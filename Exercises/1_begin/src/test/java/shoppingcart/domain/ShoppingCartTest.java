package shoppingcart.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class ShoppingCartTest {

    @Test
    void testAddItemToCart() {
        // Create an empty shopping cart
        ShoppingCart cart = new ShoppingCart("cart-123", List.of(), false);

        // Create a new line item
        ShoppingCart.LineItem item = new ShoppingCart.LineItem("akka-tshirt", "Akka T-Shirt", 2);

        // Add item to the cart (simulating business logic)
        List<ShoppingCart.LineItem> updatedItems = List.of(item);
        ShoppingCart updatedCart = new ShoppingCart(cart.cartId(), updatedItems, cart.checkedOut());

        // Verify the cart contains the item
        assertEquals(1, updatedCart.items().size());
        assertEquals("akka-tshirt", updatedCart.items().get(0).productId());
        assertEquals(2, updatedCart.items().get(0).quantity());
    }

    @Test
    void testUpdateItemQuantity() {
        // Create an existing line item
        ShoppingCart.LineItem item = new ShoppingCart.LineItem("blue-jeans", "Blue Jeans", 1);

        // Increase the quantity
        ShoppingCart.LineItem updatedItem = item.withQuantity(3);

        // Verify the quantity is updated
        assertEquals(3, updatedItem.quantity());
        assertEquals("blue-jeans", updatedItem.productId());
    }

    @Test
    void testCartCheckoutState() {
        // Create a shopping cart with items
        ShoppingCart cart = new ShoppingCart("cart-456", List.of(
                new ShoppingCart.LineItem("hat", "Akka Hat", 1)
        ), false);

        // Simulate checkout
        ShoppingCart checkedOutCart = new ShoppingCart(cart.cartId(), cart.items(), true);

        // Verify checkout state
        assertTrue(checkedOutCart.checkedOut());
    }
}
