package com.mike.mybusiness;

public class Bill {
    String itemName;
    String distributor;
    int quantity = 1;
    float price;

    public Bill(String name, String company, int quantityM, float priceM) {
        itemName = name;
        distributor = company;
        quantity = quantityM;
        price = priceM;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    float discount = 0;

    public boolean isDiscounted()
    {
        return discount > 0;
    }


    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public String getItemName() {
        return itemName;
    }

    public String getDistributor() {
        return distributor;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getPrice() {
        return price;
    }

    public BillContent getBillContent()
    {
        return new BillContent(getItemName(), getQuantity(), getDiscount());
    }

}
