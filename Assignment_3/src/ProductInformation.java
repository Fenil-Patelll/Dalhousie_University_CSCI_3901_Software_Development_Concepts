import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProductInformation {
    static Connection connection;

    public ProductInformation(Connection connection){
        this.connection = connection;
    }

    static Element productInformation(String startDate, String endDate) throws SQLException, ParserConfigurationException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document document = builder.newDocument();

        String productInformation = "select \n" +
                "p.productName,\n" +
                "p.productLine,\n" +
                "t.productFirstSoldDate,\n" +
                "c.customerName,\n" +
                "od.quantityOrdered\n" +
                "from products p\n" +
                "left join (\n" +
                "select\n" +
                "p.productCode,\n" +
                "min(o.orderDate) as productFirstSoldDate\n" +
                "from products p\n" +
                "left join orderdetails od on p.productCode = od.productCode\n" +
                "left join orders o on o.orderNumber = od.orderNumber\n" +
                "where o.orderDate >= '" + startDate + "' and o.orderDate <= '" + endDate + "'\n" +
                "group by 1\n" +
                ") t on p.productCode = t.productCode\n" +
                "left join orderdetails od on p.productCode = od.productCode\n" +
                "left join orders o on od.orderNumber = o.orderNumber\n" +
                "left join customers c on c.customerNumber = o.customerNumber\n" +
                "where o.orderDate >= '" + startDate + "' and o.orderDate <= '" + endDate + "'\n" +
                ";";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(productInformation);

        Element productList = document.createElement("product_list");
        while(resultSet.next())
        {
            Element product = document.createElement("product");

            Element productName = document.createElement("product_name");
            product.appendChild(productName);

            Element productLineName = document.createElement("product_line_name");
            product.appendChild(productLineName);

            productName.appendChild(document.createTextNode(resultSet.getString("productName")));
            productLineName.appendChild(document.createTextNode(resultSet.getString("productLine")));

            Element productSales = document.createElement("product_sales");
            product.appendChild(productSales);

            Element introductionDate = document.createElement("introduction_date");
            productSales.appendChild(introductionDate);
            introductionDate.appendChild(document.createTextNode(resultSet.getString("productFirstSoldDate")));

            Element customerSales = document.createElement("customer_sales");
            productSales.appendChild(customerSales);

            Element customerName = document.createElement("customer_name");
            customerName.appendChild(document.createTextNode(resultSet.getString("customerName")));
            customerSales.appendChild(customerName);

            Element unitsSold = document.createElement("units_sold");
            unitsSold.appendChild(document.createTextNode(resultSet.getString("quantityOrdered")));
            customerSales.appendChild(unitsSold);

            productList.appendChild(product);
        }
        return productList;
    }
}
