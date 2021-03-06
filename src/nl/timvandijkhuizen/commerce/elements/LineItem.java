package nl.timvandijkhuizen.commerce.elements;

import nl.timvandijkhuizen.commerce.base.Element;
import nl.timvandijkhuizen.commerce.base.ProductSnapshot;

public class LineItem extends Element {

    private int orderId;
    private Integer productId;
    private ProductSnapshot product;
    private int quantity;

    public LineItem(Product product, int quantity) {
        this.productId = product.getId();
        this.product = new ProductSnapshot(product);
        this.quantity = quantity;
    }

    public LineItem(int id, int orderId, Integer productId, ProductSnapshot product, int quantity) {
        this.setId(id);
        this.orderId = orderId;
        this.productId = productId;
        this.product = product;
        this.quantity = quantity;
    }

    @Override
    protected boolean validate(String scenario) {
        return true;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public int getOrderId() {
        return orderId;
    }
    
    public Integer getProductId() {
        return productId;
    }

    public ProductSnapshot getProduct() {
        return product;
    }

    public void updateProduct(Product product) {
        this.product = new ProductSnapshot(product);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getPrice() {
        return product.getPrice() * quantity;
    }

}
