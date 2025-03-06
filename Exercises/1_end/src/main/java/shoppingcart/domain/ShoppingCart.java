package shoppingcart.domain;

import java.util.List;

public record ShoppingCart(String cartId, List<LineItem> items, boolean checkedOut) { 

  public record LineItem(String productId, String name, int quantity) { 
    public LineItem withQuantity(int quantity) {
      return new LineItem(productId, name, quantity);
    }
  }

}