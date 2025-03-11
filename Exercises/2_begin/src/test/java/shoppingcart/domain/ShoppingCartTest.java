package shoppingcart.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShoppingCartTest {

    private ShoppingCart shoppingCart;
    private final String cartId = "test-cart";

    @BeforeEach
    void setUp() {
        // Initialize a ShoppingCart with one existing item
        ShoppingCart.LineItem initialItem = new ShoppingCart.LineItem("akka-tshirt", "Akka Tshirt", 2);
        shoppingCart = new ShoppingCart(cartId, List.of(initialItem), false);
    }

    @Test
    void testOnItemAdded_NewItem() {
        // Create a new item that does not exist in the cart
        ShoppingCartEvent.ItemAdded event = new ShoppingCartEvent.ItemAdded(
            new ShoppingCart.LineItem("akka-hoodie", "Akka Hoodie", 1)
        );

        // Invoke the method
        ShoppingCart updatedCart = shoppingCart.onItemAdded(event);

        // Assertions
        assertEquals(2, updatedCart.items().size(), "Cart should now contain 2 items");
        assertTrue(updatedCart.items().stream().anyMatch(i -> i.productId().equals("akka-hoodie")),
                   "New item should be added");
    }

    @Test
    void testOnItemAdded_UpdateExistingItem() {
        // Create an event with the same product ID but a different quantity
        ShoppingCartEvent.ItemAdded event = new ShoppingCartEvent.ItemAdded(
            new ShoppingCart.LineItem("akka-tshirt", "Akka Tshirt", 3)
        );

        // Invoke the method
        ShoppingCart updatedCart = shoppingCart.onItemAdded(event);

        // Assertions
        assertEquals(1, updatedCart.items().size(), "Cart should still contain 1 item");
        assertEquals(5, updatedCart.items().get(0).quantity(), "Quantity should be updated to 5");
    }

    @Test
    void testOnItemAdded_EmptyCart() {
        // Start with an empty shopping cart
        ShoppingCart emptyCart = new ShoppingCart(cartId, List.of(), false);

        // Add a new item
        ShoppingCartEvent.ItemAdded event = new ShoppingCartEvent.ItemAdded(
            new ShoppingCart.LineItem("akka-mug", "Akka Mug", 2)
        );
        ShoppingCart updatedCart = emptyCart.onItemAdded(event);

        // Assertions
        assertEquals(1, updatedCart.items().size(), "Cart should contain 1 item");
        assertEquals("akka-mug", updatedCart.items().get(0).productId(), "Item ID should match");
        assertEquals(2, updatedCart.items().get(0).quantity(), "Item quantity should be 2");
    }

    @Test
    void testOnItemAdded_SortingOrderMaintained() {
        // Add a second item with a product ID that should appear earlier in the sorted order
        ShoppingCartEvent.ItemAdded event = new ShoppingCartEvent.ItemAdded(
            new ShoppingCart.LineItem("akka-beanie", "Akka Beanie", 1)
        );

        // Invoke the method
        ShoppingCart updatedCart = shoppingCart.onItemAdded(event);

        // Assertions
        assertEquals(2, updatedCart.items().size(), "Cart should contain 2 items");
        assertEquals("akka-beanie", updatedCart.items().get(0).productId(), "Items should be sorted by product ID");
        assertEquals("akka-tshirt", updatedCart.items().get(1).productId(), "Akka T-shirt should be second");
    }

    @Test
    void testShoppingCartImmutability() {
        // Create an event to add an item
        ShoppingCartEvent.ItemAdded event = new ShoppingCartEvent.ItemAdded(
            new ShoppingCart.LineItem("akka-cap", "Akka Cap", 2)
        );

        // Invoke the method
        ShoppingCart updatedCart = shoppingCart.onItemAdded(event);

        // Ensure original cart is unchanged
        assertEquals(1, shoppingCart.items().size(), "Original cart should remain unchanged");
        assertEquals(2, updatedCart.items().size(), "Updated cart should have 2 items");
    }
}
