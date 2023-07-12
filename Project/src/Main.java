import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws Exception {

        //PowerService Object Created
        PowerService powerServiceObject = new PowerService();

        //function to reset the db
//        powerServiceObject.resetDB();

        //adding postal codes, population, and area into PowerServicedObject's addPostalCode method
        powerServiceObject.addPostalCode("B3H2K9",4550,121);
        powerServiceObject.addPostalCode("B3I2K9",3000,152);
        powerServiceObject.addPostalCode("B3J2K9",6500, 144);
        powerServiceObject.addPostalCode("B3K2K9",4100, 79);
        powerServiceObject.addPostalCode("B3L2K9",5000,134);
        powerServiceObject.addPostalCode("B3M2K9",7000, 91);

        //creating a point object and setting value of that object
        Point p1 = new Point();
        p1.setX(2);
        p1.setY(4);

        //creating a Set of the PostalCodes and adding postal codes into that set
        Set<String> postalCodes = new HashSet<>();
        postalCodes.add("B3H2K9");
        postalCodes.add("B3I2K9");
        postalCodes.add("B3J2K9");
        postalCodes.add("B3K2K9");

        //adding the details for the distribution hub with point location object and providing postalCodes into DistributionHub
        powerServiceObject.addDistributionHub("AB1",p1, postalCodes );

        //creating a point object and setting value of that object
        Point p2 = new Point();
        p2.setX(7);
        p2.setY(2);

        //creating a Set of the PostalCodes and adding postal codes into that set
        Set<String> postalCodes2 = new HashSet<>();
        postalCodes2.add("B3I2K9");
        postalCodes2.add("B3K2K9");
        postalCodes2.add("B3L2K9");

        //adding the details for the distribution hub with point location object and providing postalCodes into DistributionHub
        powerServiceObject.addDistributionHub("MN3",p2, postalCodes2);

        //creating a point object and setting value of that object
        Point p3 = new Point();
        p3.setX(5);
        p3.setY(9);
        Set<String> postalCodes3 = new HashSet<>();
        postalCodes3.add("B3H2K9");
        postalCodes3.add("B3K2K9");
        postalCodes3.add("B3M2K9");

        //adding the details for the distribution hub with point location object and providing postalCodes into DistributionHub
        powerServiceObject.addDistributionHub("PQ2",p3, postalCodes3);

        //adding values into the hubDamage function providing hubIdentification and RepairEstimate into that
        powerServiceObject.hubDamage("AB1",(float)10);
        powerServiceObject.hubDamage("MN3",(float)6);
        powerServiceObject.hubDamage("PQ2",(float)8);

        //Adding values into hubRepair method providing hubid, Employeeid, and Inservice flag of that hub
        powerServiceObject.hubRepair("AB1","E1",(float)3,false);
        powerServiceObject.hubRepair("AB1","E2",(float)4,false);
        powerServiceObject.hubRepair("AB1","E3",(float)3,false);

        //Printing PeopleOutOfService(number of people those who do not have power)
        System.out.println("Number of people that are out of Service");
        System.out.println(powerServiceObject.peopleOutOfService());

        //calling mostDamagedPostalCodes
        //this function will return list of postal codes in descending order of repair time needed
        System.out.println("Most Damaged PostalCodes");
        for(DamagedPostalCodes d : powerServiceObject.mostDamagedPostalCodes(4)){
            System.out.println(d.getPostalCode());
            System.out.println(d.getRepairEstimate());
        }

        //fixOrder function will return the Order the hubs in descending order of the importance(number of people who regain service per hour of repair)
        System.out.println("Fix order");
        for(HubImpact h : powerServiceObject.fixOrder(2)){
            System.out.println(h.getHubIdentifier());
            System.out.println(h.getImpactValue());
        }

        // calling rateOfServiceRestoration method which will return The list of hours of repair effort
        // with the increment percentage with total number of people, basically it will Produce an estimate of the rate with which people are restored to power
        System.out.println("rate of service restoration");
        for(Integer i : powerServiceObject.rateOfServiceRestoration((float)0.05)){
            System.out.println(i);
        }

        //function underservedPostalByPopulation will return the list of postalcodes in descending order, in which,
        //number of hubs per person in postalcodes are in decreasing order, and these are the postal codes which needs more service
        System.out.println("Underserved population");
        for(String si :powerServiceObject.underservedPostalByPopulation(3)){
            System.out.println(si);
        }

        //function underservedPostalByArea will return the list of postalcodes in descending order, in which,
        //number of hubs per area are lower, and the return list is in descening order
        System.out.println("Underserved area");
        for(String st : powerServiceObject.underservedPostalByArea(4)){
           System.out.println(st);
       }

        System.out.println("repair plan");
        for(HubImpact k : powerServiceObject.repairPlan("AB1",23,(float)21.41)){
            System.out.println(k.getHubIdentifier());
            System.out.println(k.getImpactValue());
        }



    }

}