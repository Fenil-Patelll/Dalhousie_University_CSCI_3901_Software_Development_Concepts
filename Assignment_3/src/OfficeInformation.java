import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OfficeInformation {
    static Connection connection;

    public OfficeInformation(Connection connection)
    {
        this.connection = connection;
    }

    public Element officeInformation(String startDate, String endDate) throws ParserConfigurationException, SQLException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document document = builder.newDocument();

        String officeInformation = "select city, territory, staffNumber, sum(newCustomers) as newCustomers, sum(amount) as sales_value_for_customers from (\n" +
                "select \n" +
                "ofi.city,\n" +
                "ofi.territory,\n" +
                "t.staffNumber,\n" +
                "tp.newCustomers,\n" +
                "tp.amount\n" +
                "from offices ofi\n" +
                "left join employees e on e.officeCode = ofi.officeCode\n" +
                "left join (\n" +
                "select \n" +
                "officeCode,\n" +
                "count(employeeNumber) as staffNumber\n" +
                "from employees e\n" +
                "group by 1) t on t.officeCode = ofi.officeCode\n" +
                "left join (\n" +
                "select \n" +
                "salesRepEmployeeNumber,\n" +
                "count(c.customerName) as newCustomers,\n" +
                "sum(p.amount) as amount\n" +
                "from customers c\n" +
                "left join orders o on c.customerNumber = o.customerNumber\n" +
                "left join payments p on p.customerNumber = c.customerNumber\n" +
                "where o.orderDate >= '" + startDate + "' and o.orderDate <= '" + endDate + "'\n" +
                "group by 1) tp on tp.salesRepEmployeeNumber = e.employeeNumber) aa group by 1,2,3;";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(officeInformation);

        Element officeList = document.createElement("office_list");
        while(resultSet.next())
        {
            Element officeCity = document.createElement("office_city");
            officeCity.appendChild(document.createTextNode(resultSet.getString("city")));
            officeList.appendChild(officeCity);

            Element territory = document.createElement("territory");
            territory.appendChild(document.createTextNode(resultSet.getString("territory")));
            officeList.appendChild(territory);

            Element employeeCount = document.createElement("employee_count");
            employeeCount.appendChild(document.createTextNode(resultSet.getString("staffNumber")));
            officeList.appendChild(employeeCount);

            Element newCustomer = document.createElement("new_customer");
            officeList.appendChild(newCustomer);

            Element numberOfNewCustomers = document.createElement("customer_name");
            numberOfNewCustomers.appendChild(document.createTextNode(resultSet.getString("newCustomers")));
            newCustomer.appendChild(numberOfNewCustomers);

            Element customerSalesValue = document.createElement("customer_sales_value");
            customerSalesValue.appendChild(document.createTextNode(resultSet.getString("sales_value_for_customers")));
            newCustomer.appendChild(customerSalesValue);
        }
        return officeList;
    }
}
