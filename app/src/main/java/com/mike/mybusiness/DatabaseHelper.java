package com.mike.mybusiness;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "myBusiness";
    private static final int DB_VERSION = 1;
    private static final String TABLE_BILLS = "billingTable";
    private static final String TABLE_INVENTORY = "inventoryTable";

    private static final String TABLE_CUSTOMERS = "customersTable";
    private static final String ID_COL = "id";
    private static final String PRODUCT_NAME = "name";
    private static final String PRODUCT_PRICE = "price";
    private static final String PRODUCT_BARCODE = "barcode";
    private static final String PRODUCT_COMPANY = "company";
    private static final String PRODUCT_STOCK = "stock";
    private static final String PRODUCT_STOCK_DATE = "stockDate";
    private static final String PRODUCT_EXPIRY = "expiry";

    private static final String CUSTOMER_NAME = "name";
    private static final String CUSTOMER_NUMBER = "number";
    private static final String CUSTOMER_ADDRESS = "address";
    private static final String CUSTOMER_DUES = "dues";

    private static final String CUSTOMER_EMAIL = "email";
    private static final String INVOICE_DATE = "date";
    private static final String INVOICE_NUMBER = "invoiceNumber";
    private static final String TOTAL = "total";
    private static final String DISCOUNT = "discount";
    private static final String TAX = "tax";
    private static final String PAID = "paid";
    private static final String IS_CREDIT = "credit";
    private static final String IS_PERCENT = "percent";
    private static final String QTY = "qty";
    private static final String PRODUCT_DISCOUNT = "discount";



    public DatabaseHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_BILLS + "(" + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CUSTOMER_NAME + " TEXT, "
                + INVOICE_DATE + " TEXT, "
                + INVOICE_NUMBER + " TEXT UNIQUE, "
                + TOTAL + " TEXT, "
                + DISCOUNT + " TEXT, "
                + TAX + " TEXT, "
                + PAID + " TEXT," +
                IS_PERCENT + " TEXT," +
                IS_CREDIT + " TEXT)";

        String query1 = "CREATE TABLE " + TABLE_CUSTOMERS + "(" + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CUSTOMER_NAME + " TEXT, "
                + CUSTOMER_NUMBER + " TEXT, "
                + CUSTOMER_ADDRESS + " TEXT, "
                + CUSTOMER_DUES + " TEXT, "
                + CUSTOMER_EMAIL + " TEXT)";

        String query3 = "CREATE TABLE " + TABLE_INVENTORY + "(" + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PRODUCT_NAME + " TEXT UNIQUE, "
                + PRODUCT_PRICE + " TEXT, "
                + PRODUCT_BARCODE + " TEXT UNIQUE, "
                + PRODUCT_COMPANY + " TEXT, "
                + PRODUCT_STOCK + " TEXT, "
                + PRODUCT_STOCK_DATE + " TEXT, "
                + PRODUCT_EXPIRY + " TEXT)";
        sqLiteDatabase.execSQL(query);
        sqLiteDatabase.execSQL(query1);
        sqLiteDatabase.execSQL(query3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean newProduct(String name, int price, String barcode, String company, String stock,
                           String stockDate, String expiry)
    {
        SQLiteDatabase sqLiteDatabase1 = getReadableDatabase();
        Cursor cursor = sqLiteDatabase1.rawQuery("SELECT * FROM " + TABLE_INVENTORY + " WHERE " + PRODUCT_NAME +
                " = '" + name + "' || " +
                PRODUCT_BARCODE + " = '" + barcode + "'" , null);
        if (cursor.moveToFirst())
        {
            cursor.close();
            return false;
        }
        else {
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(PRODUCT_NAME, name);
            contentValues.put(PRODUCT_PRICE, price);
            contentValues.put(PRODUCT_BARCODE, barcode);
            contentValues.put(PRODUCT_COMPANY, company);
            contentValues.put(PRODUCT_STOCK, stock);
            contentValues.put(PRODUCT_STOCK_DATE, stockDate);
            contentValues.put(PRODUCT_EXPIRY, expiry);
            sqLiteDatabase.insert(TABLE_INVENTORY, null, contentValues);
            sqLiteDatabase.close();
            return true;
        }
    }

    public void newInvoiceTable(Context context, String invoiceNumber, ArrayList<BillContent> billContents)
    {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "a" + invoiceNumber);
        String query = "CREATE TABLE " +  "a" +  invoiceNumber + "("
                + PRODUCT_NAME + " TEXT, "
                + QTY + " INTEGER, "
                + DISCOUNT + " INTEGER)";
        sqLiteDatabase.execSQL(query);
        for (BillContent billContent : billContents)
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(PRODUCT_NAME, billContent.getItemName());
            contentValues.put(QTY, billContent.getQty());
            contentValues.put(DISCOUNT, billContent.discount);
            sqLiteDatabase.insert("a" + invoiceNumber, null, contentValues);
            sqLiteDatabase.close();
        }
    }

    public ArrayList<BillContent> findInvoiceNumber(String invoiceNumber)
    {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " +  "a" +  invoiceNumber , null);
        ArrayList<BillContent> billContents = new ArrayList<>();
        if (cursor.moveToFirst())
        {
            do {
                billContents.add(new BillContent(cursor.getString(0), cursor.getInt(1),
                        cursor.getInt(2)));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return billContents;
    }

    public boolean saveBill(Invoice invoice)
    {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_BILLS + " WHERE " + INVOICE_NUMBER +
                " = '" + invoice.getInvoiceNumber() + "'", null);
        if (cursor.moveToFirst())
        {
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(CUSTOMER_NAME, invoice.getCustomerName());
        contentValues.put(INVOICE_DATE, invoice.getDate());
        contentValues.put(INVOICE_NUMBER, invoice.getInvoiceNumber());
        contentValues.put(TOTAL, invoice.getTotal());
        contentValues.put(DISCOUNT, invoice.getDiscount());
        contentValues.put(TAX, invoice.getTaxPaid());
        contentValues.put(PAID, invoice.getPaid());
        contentValues.put(IS_PERCENT, invoice.getIsPercent());
        contentValues.put(IS_CREDIT, invoice.getIsCredit());
        sqLiteDatabase.insert(TABLE_BILLS, null, contentValues);
        sqLiteDatabase.close();
        return true;
    }

    public String autoInvoiceNumber()
    {
        int x = 1;
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_BILLS, null);
        if (cursor.moveToFirst())
        {
           x = cursor.getCount() + 1;
        }
        return x + "";
    }
    public ArrayList<Invoice> fetchAllBill()
    {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_BILLS, null);
        ArrayList<Invoice> invoices = new ArrayList<>();
        if (cursor.moveToFirst())
        {
            do {
                invoices.add(new Invoice(cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4).substring(0), cursor.getString(5),
                        cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9)));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return invoices;
    }

    public Invoice getBillByInvoice(String invoiceNumber)
    {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_BILLS + " WHERE "
                + INVOICE_NUMBER  + " LIKE '%"
                + invoiceNumber + "%'", null);
        if (cursor.moveToFirst())
        {
            return new Invoice(cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4).substring(0), cursor.getString(5),
                    cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9));
        }
        cursor.close();
        return null;
    }

    public Invoice getBillByCustomerName(String customerName)
    {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_BILLS + " WHERE "
                + CUSTOMER_NAME  + " LIKE '%"
                + customerName + "%'", null);
        if (cursor.moveToFirst())
        {
            return new Invoice(cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4).substring(0), cursor.getString(5),
                    cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9));
        }
        cursor.close();
        return null;
    }

    public Invoice getBillByDate(String date)
    {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_BILLS + " WHERE "
                + INVOICE_DATE  + " LIKE '%"
                + date + "%'", null);
        if (cursor.moveToFirst())
        {
            return new Invoice(cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4).substring(0), cursor.getString(5),
                    cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9));
        }
        cursor.close();
        return null;
    }

    public ArrayList<Product> fetchAllProducts()
    {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_INVENTORY, null);
        ArrayList<Product> products = new ArrayList<>();
        if (cursor.moveToFirst())
        {
            do {
                products.add(new Product(cursor.getString(1), cursor.getString(3),
                        cursor.getString(5), Float.parseFloat(cursor.getString(2)), cursor.getString(6),
                        cursor.getString(4), cursor.getString(7)));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return products;
    }

    public ArrayList<Product> findProducts(String query)
    {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_INVENTORY + " WHERE "
                + PRODUCT_NAME + " || " + PRODUCT_COMPANY + " LIKE "
                + "'%" + query + "%'", null);
        ArrayList<Product> products = new ArrayList<>();
        if (cursor.moveToFirst())
        {
            do {
                products.add(new Product(cursor.getString(1), cursor.getString(3),
                        cursor.getString(5), Integer.parseInt(cursor.getString(2)), cursor.getString(6),
                        cursor.getString(4), cursor.getString(7)));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return products;
    }

    public Bill findProductByBarCode(String barcode)
    {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_INVENTORY + " WHERE "
                + PRODUCT_BARCODE  + " LIKE " + "'%"
                + barcode + "%'" , null);
        if (cursor.moveToFirst())
        {
                return new Bill(cursor.getString(1), cursor.getString(4),
                        1, Integer.parseInt(cursor.getString(2)));
        }
        cursor.close();
        return null;
    }

    public void newCustomer(String name, String number, String address, String dues, String email)
    {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CUSTOMER_NAME, name);
        contentValues.put(CUSTOMER_NUMBER, number);
        contentValues.put(CUSTOMER_ADDRESS, address);
        contentValues.put(CUSTOMER_DUES, dues);
        contentValues.put(CUSTOMER_EMAIL, email);
        sqLiteDatabase.insert(TABLE_CUSTOMERS, null, contentValues);
        sqLiteDatabase.close();
    }

    public ArrayList<Customer> fetchAllCustomers()
    {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_CUSTOMERS, null);
        ArrayList<Customer> customers = new ArrayList<>();
        if (cursor.moveToFirst())
        {
            do {
                customers.add(new Customer(cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getString(5)));
            }
            while (cursor.moveToNext());
        }
        else {
            return null;
        }
        cursor.close();
        return customers;
    }

    public ArrayList<Customer> searchCustomerByName(String name)
    {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_CUSTOMERS + " WHERE "
                + CUSTOMER_NAME + " = "
                + "'" + name + "'", null);
        ArrayList<Customer> customers = new ArrayList<>();
        if (cursor.moveToFirst())
        {
            do {
                customers.add(new Customer(cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getString(5)));
            }
            while (cursor.moveToNext());
        }
        else {
            return null;
        }
        cursor.close();
        return customers;
    }

    void updateCustomerDues(Customer customer, int id)
    {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CUSTOMER_NAME, customer.getName());
        contentValues.put(CUSTOMER_NUMBER, customer.getNumber());
        contentValues.put(CUSTOMER_ADDRESS, customer.getAddress());
        contentValues.put(CUSTOMER_DUES, customer.getDues());
        contentValues.put(CUSTOMER_EMAIL, customer.getEmail());
        sqLiteDatabase.update(TABLE_CUSTOMERS, contentValues, "id=" + id, null);
        sqLiteDatabase.close();
    }

    void updateProductStock(Product product, int id)
    {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PRODUCT_NAME, product.getName());
        contentValues.put(PRODUCT_PRICE, String.valueOf(product.getPrice()));
        contentValues.put(PRODUCT_BARCODE, product.getBarcode());
        contentValues.put(PRODUCT_COMPANY, product.getCompany());
        contentValues.put(PRODUCT_STOCK, product.getStock());
        contentValues.put(PRODUCT_STOCK_DATE, product.getStockDate());
        contentValues.put(PRODUCT_EXPIRY, product.getExpiry());
        sqLiteDatabase.update(TABLE_INVENTORY, contentValues, "id=" + id, null);
        sqLiteDatabase.close();
    }


}
