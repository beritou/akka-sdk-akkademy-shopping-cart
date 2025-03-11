package shoppingcart.application;

import akka.Done;
import akka.javasdk.testkit.EventSourcedTestKit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import shoppingcart.domain.ShoppingCart;

import java.util.List;

import static shoppingcart.domain.ShoppingCartEvent.ItemAdded;
import static org.junit.jupiter.api.Assertions.*;

public class ShoppingCartTest {

  private final ShoppingCart.LineItem akkaTshirt = new ShoppingCart.LineItem("akka-tshirt", "Akka Tshirt", 10);
  private ShoppingCart shoppingCart;
  private final String cartId = "testkit-entity-id";

  @BeforeEach
  void setUp() {
    shoppingCart = new ShoppingCart(cartId, List.of(akkaTshirt), false);
  }

  @Test
  public void testAddLineItem() {
    var testKit = EventSourcedTestKit.of(ShoppingCartEntity::new);

    {
      var result = testKit.call(e -> e.addItem(akkaTshirt));
      assertEquals(Done.getInstance(), result.getReply());

      var itemAdded = result.getNextEventOfType(ItemAdded.class);
      assertEquals(10, itemAdded.item().quantity());
    }

    // actually we want more akka tshirts
    {
      var result = testKit.call(e -> e.addItem(akkaTshirt.withQuantity(5)));
      assertEquals(Done.getInstance(), result.getReply());

      var itemAdded = result.getNextEventOfType(ItemAdded.class);
      assertEquals(5, itemAdded.item().quantity());
    }

    {
      assertEquals(2, testKit.getAllEvents().size());
      var result = testKit.call(ShoppingCartEntity::getCart);
      assertEquals(
          new ShoppingCart(cartId, List.of(akkaTshirt.withQuantity(15)), false),
          result.getReply());
    }
  }

  @Test
  public void testOnItemAdded_NewItem() {
    // Create a new item that does not exist in the cart
    ShoppingCartEvent.ItemAdded event = new ItemAdded(new ShoppingCart.Item("akka-hoodie", "Akka Hoodie", 1));

    // Invoke the method
    ShoppingCart updatedCart = shoppingCart.onItemAdded(event);

    // Assertions
    assertEquals(2, updatedCart.items().size(), "Cart should now contain 2 items");
    assertTrue(updatedCart.items().stream().anyMatch(i -> i.productId().equals("akka-hoodie")),
        "New item should be added");
  }

  @Test
  public void testOnItemAdded_UpdateExistingItem() {
    // Create an item with the same product ID but different quantity
    ShoppingCartEvent.ItemAdded event = new ItemAdded(new ShoppingCart.Item("akka-tshirt", "Akka Tshirt", 5));

    // Invoke the method
    ShoppingCart updatedCart = shoppingCart.onItemAdded(event);

    // Assertions
    assertEquals(1, updatedCart.items().size(), "Cart should still contain 1 item");
    assertEquals(5, updatedCart.items().get(0).quantity(), "Quantity should be updated to 5");
  }
}
