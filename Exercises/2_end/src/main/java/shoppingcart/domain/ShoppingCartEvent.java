package shoppingcart.domain;

import akka.javasdk.annotations.TypeName;

public sealed interface ShoppingCartEvent {

  @TypeName("item-added")
  record ItemAdded(ShoppingCart.LineItem item) implements ShoppingCartEvent {
  }

}