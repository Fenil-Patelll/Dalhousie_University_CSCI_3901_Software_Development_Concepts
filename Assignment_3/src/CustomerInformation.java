import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CustomerInformation {
    static Connection connection;
    public CustomerInformation(Connection connection){
        this.connection = connection;
    }
    public Element customerInformation(String startDate, String endDate) throws ParserConfigurationException, SQLException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document document = builder.newDocument();

        String customerInformation = "select customerName, addressLine1,city, postalCode, country, valueOfOrders, (valueOfOrders - amount) as owingAmount from (\n" +
                "select \n" +
                "c.customerName,\n" +
                "c.addressLine1,\n" +
                "c.city,\n" +
                "c.postalCode,\n" +
                "c.country,\n" +
                "sum(od.valueOfOrders) as valueOfOrders,\n" +
                "sum(p.amount) as amount\n" +
                "from customers c\n" +
                "left join orders o on c.customerNumber = o.customerNumber\n" +
                "left join \n" +
                "(select\n" +
                "orderNumber,\n" +
                "sum((quantityOrdered * priceEach)) as valueOfOrders\n" +
                "from orderdetails group by 1) od on o.orderNumber = od.orderNumber\n" +
                "join payments p on p.customerNumber = c.customerNumber and p.paymentDate >= '" + startDate + "' and p.paymentDate<= '" + endDate + "'\n" +
                "where o.orderDate >= '" + startDate + "' and o.orderDate <= '" + endDate + "' \n" +
                "and c.customerName not in (select c.customerName from  customers c left join orders o on c.customerNumber = o.customerNumber where o.orderDate<= '" + startDate + "')\n" +
                "group by 1,2,3,4,5) aa;";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(customerInformation);

        Element customerList = document.createElement("customer_list");

        while(resultSet.next())
        {
            Element customer = document.createElement("customer");
            customerList.appendChild(customer);

            Element customerName = document.createElement("customer_name");
            customer.appendChild(customerName);

            Element address = document.createElement("address");

            // Address and its child elements
            Element streetAddress = document.createElement("street_address");
            streetAddress.appendChild(document.createTextNode(resultSet.getString("addressLine1")));
            Element city = document.createElement("city");
            city.appendChild(document.createTextNode(resultSet.getString("city")));
            Element postalCode = document.createElement("postal_code");
            postalCode.appendChild(document.createTextNode(resultSet.getString("postalCode")));
            Element country = document.createElement("country");
            country.appendChild(document.createTextNode(resultSet.getString("country")));

            address.appendChild(streetAddress);
            address.appendChild(city);
            address.appendChild(postalCode);
            address.appendChild(country);
            customer.appendChild(address);

            Element orderValue = document.createElement("order_value");
            orderValue.appendChild(document.createTextNode(resultSet.getString("valueOfOrders")));
            customer.appendChild(orderValue);

            Element owingAmount = document.createElement("owing_Amount");
            owingAmount.appendChild(document.createTextNode(resultSet.getString("owingAmount")));
            customer.appendChild(owingAmount);



            customerList.appendChild(customer);
        }
        return customerList;
    }
}
