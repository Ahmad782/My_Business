package com.mike.mybusiness;

public class BillContent {
    String itemName, qty, discount;

    public BillContent(String itemName, int quantity, float discount) {
        this.itemName = itemName;
        qty = "" + quantity;
        this.discount = discount + "";
    }

    public String getItemName() {
        return itemName;
    }

    public String getQty() {
        return qty;
    }

    public String getDiscount() {
        return discount;
    }

}
