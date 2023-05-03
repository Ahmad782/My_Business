package com.mike.mybusiness;
public class Invoice {
    String customerName;
    String date;
    String invoiceNumber;
    String total;
    String discount;
    String taxPaid;

    public String getIsCredit() {
        return isCredit;
    }

    public String getIsPercent() {
        return isPercent;
    }

    String paid;
    String isCredit;

    String isPercent;

    public Invoice(String customerName, String date, String invoiceNumber, String total, String discount, String taxPaid, String paid, String isCredit, String isPercent) {
        this.customerName = customerName;
        this.date = date;
        this.invoiceNumber = invoiceNumber;
        this.total = total;
        this.discount = discount;
        this.taxPaid = taxPaid;
        this.paid = paid;
        this.isCredit = isCredit;
        this.isPercent = isPercent;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getDate() {
        return date;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getTotal() {
        return total;
    }

    public String getDiscount() {
        return discount;
    }

    public String getTaxPaid() {
        return taxPaid;
    }

    public String getPaid() {
        return paid;
    }
}
