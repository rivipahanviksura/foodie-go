package lk.foodie.foodiego.models;

import java.util.List;

public class Order {
    private String orderId;
    private String customerName;
    private List<String> items;
    private String orderStatus;
    private String totalAmount;
    private String orderTimestamp;

    public Order() {

    }

    public Order(String orderId, String customerName,List<String> items, String orderStatus, String totalAmount, String orderTimestamp) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.items = items;
        this.orderStatus = orderStatus;
        this.totalAmount = totalAmount;
        this.orderTimestamp = orderTimestamp;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOrderTimestamp() {
        return orderTimestamp;
    }

    public void setOrderTimestamp(String orderTimestamp) {
        this.orderTimestamp = orderTimestamp;
    }
}


