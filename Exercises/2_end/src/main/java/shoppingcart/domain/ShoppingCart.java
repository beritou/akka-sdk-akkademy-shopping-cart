package shoppingcart.domain;

import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import java.util.stream.Collectors;

import shoppingcart.domain.ShoppingCart;
import shoppingcart.domain.ShoppingCartEvent;
import shoppingcart.domain.ShoppingCart.LineItem;

public record ShoppingCart(String cartId, List<LineItem> items, boolean checkedOut) {

  public record LineItem(String productId, String name, int quantity) {
    public LineItem withQuantity(int quantity) {
      return new LineItem(productId, name, quantity);
    }
  }

  public ShoppingCart onItemAdded(ShoppingCartEvent.ItemAdded itemAdded) {
    var item = itemAdded.item();

    // Find the existing item and update its quantity, or use the new item
    LineItem updatedItem = items().stream()
        .filter(lineItem -> lineItem.productId().equals(item.productId()))
        .findFirst()
        .map(existingItem -> existingItem.withQuantity(existingItem.quantity() + item.quantity()))
        .orElse(item);

    // Remove any existing item with the same product ID, then add the updated item
    List<LineItem> updatedItems = items().stream()
        .filter(lineItem -> !lineItem.productId().equals(item.productId()))
        .collect(Collectors.toList());

    updatedItems.add(updatedItem);
    updatedItems.sort(Comparator.comparing(LineItem::productId));

    return new ShoppingCart(cartId, updatedItems, checkedOut);
  }

}