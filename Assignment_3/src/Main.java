import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    // Date validation in  YYYY/MM/DD Format
    public static void DateValidation(String Date)
    {
        if (Date.trim().equals(""))
        {
            System.out.printf("Null date provided");
            System.exit(0);
        }
        else
        {
            SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd");
            dformat.setLenient(false);
            try
            {
                Date datek = dformat.parse(Date);
            }
            catch (ParseException e)
            {
                System.out.println("Date provided is Invalid");
                System.exit(0);
            }
        }
    }

    public static void main(String[] args) throws ParserConfigurationException, SQLException, ClassNotFoundException, TransformerException {

        // getting input from customers
        Scanner sc = new Scanner(System.in);
        System.out.println("Start Date: ");
        String startDate = sc.nextLine();
        DateValidation(startDate);
        System.out.println("End Date");
        String endDate = sc.nextLine();
        DateValidation(endDate);
        System.out.println("File Name: ");
        String file = sc.nextLine();




        // making a jdbc connection
        Connection connection = DriverManager.getConnection("jdbc:mysql://db.cs.dal.ca:3306/csci3901", "fenilp", "B00917151");
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Creating XML structure
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element timePeriodSummary = document.createElement("time_period_summary");
        document.appendChild(timePeriodSummary);

        Element year = document.createElement("year");

        Element start_date = document.createElement("start_date");
        start_date.appendChild(document.createTextNode(startDate));
        year.appendChild(start_date);

        Element end_date = document.createElement("end_date");
        end_date.appendChild(document.createTextNode(endDate));
        year.appendChild(end_date);

        timePeriodSummary.appendChild(year);

        // Appending CustomerInformation to timePeriodSummary
        CustomerInformation customerInformationObject = new CustomerInformation(connection);
        Element customer_list = customerInformationObject.customerInformation(startDate, endDate);
        document.adoptNode(customer_list);
        timePeriodSummary.appendChild(customer_list);

        // Appending ProductInformation to timePeriodSummary
        ProductInformation productInformationObject = new ProductInformation(connection);
        Element product_list = productInformationObject.productInformation(startDate, endDate);
        document.adoptNode(product_list);
        timePeriodSummary.appendChild(product_list);

        // Appending officeInformation to timePeriodSummary
        OfficeInformation officeInformationObject = new OfficeInformation(connection);
        Element office_list =officeInformationObject.officeInformation(startDate, endDate);
        document.adoptNode(office_list);
        timePeriodSummary.appendChild(office_list);

        // XML document building transformers
        DOMSource source = new DOMSource(document);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StreamResult streamResult = new StreamResult(new File("C:\\Users\\fenil\\OneDrive\\Desktop\\CSCI 3901\\Assignment 5\\"+file+".xml"));
        transformer.transform(source, streamResult);
        sc.close();
    }
}