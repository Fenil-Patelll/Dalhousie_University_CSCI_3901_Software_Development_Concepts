import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Pattern;

//PowerService Class
public class PowerService {

    //declaring a global object of connection
    Connection connect;

    //PowerService Constructor which will make a jdbc connection with dal database
    PowerService() throws Exception {
        Jdbc j = new Jdbc();
        connect = j.makeConnection();
    }

    //addPostalCode definition which will insert postalCode, population, area into PostalCode table of dal db
    //this function return boolean value if the unique entry is added into db or not
    boolean addPostalCode (String postalCode, int population, int area) {
        try{
            Statement st1 = connect.createStatement();
            st1.execute("INSERT INTO PostalCodes (postalCode, population, area)\n" +
                    "VALUES ('"+postalCode+"', '"+population+"', '"+area+"');");
            st1.close();
            return true;


        }
        catch (Exception e){
            System.out.printf(e.getMessage());
            return false;
        }

    }


    //addDistributionHub will take hubId, hub location in x coordinates, y coordinates and set of servicedAreas where
    //these hubs are located
    boolean addDistributionHub ( String hubIdentifier, Point location, Set<String> servicedAreas ){
        try{
            Statement st2 = connect.createStatement();
            for(String postalCode : servicedAreas){
                st2.execute("Insert into distributionhub(hubIdentifier, x_coordinates, y_coordinates, servicedAreas)" +
                        " values('"+hubIdentifier+"','"+location.getX()+"','"+location.getY()+"','"+postalCode+"') ");
            }

            return true;
        }
        catch (Exception e){
            System.out.printf(e.getMessage());
            return false;
        }

    }

    //hubDamage function will take hubId, repairEstimate time of that hub and Insert those details into
    //hubdamage table with the third column as inService which will store flag value of thw hub's working condition
    void hubDamage (String hubIdentifier, float repairEstimate){
        try {
            Statement st3 = connect.createStatement();
            st3.execute("Insert into hubdamage(hubIdentifier, repairEstimate, inService) values ('"+hubIdentifier+"',"+repairEstimate+",  (case when "+repairEstimate+" >0 then false else true end)); \n");
        }
        catch (Exception e){
            System.out.printf(e.getMessage());
        }

    }

    //hubrepair method will store hubid, employeeid , and the repairtime an employee took on that hub to repair the hub,
    //also we are storing the hub's working condtion whether the inservice or not
    //also the method will make change in the  hubdamage table if the hub in now full working condition
    void hubRepair( String hubIdentifier, String employeeId, float repairTime, boolean inService ){
      try{
          Statement st4 = connect.createStatement();
          st4.execute("Insert into hubrepair(hubIdentifier, employeeId, repairTime, inService)" +
                  " values('"+hubIdentifier+"','"+employeeId+"',"+repairTime+"7,"+inService+")");
//                  "Update hubdamge Set repairEstimate = repairEstimate - "+repairTime+" where hubIdentifier = '"+hubIdentifier+"'");
                   if(!inService){
                      st4.execute("update hubdamage set repairEstimate = repairEstimate - "+repairTime+" where hubIdentifier = '"+hubIdentifier+"'");
                      ResultSet r = st4.executeQuery(
                              "select repairEstimate from hubdamage where hubIdentifier = '"+hubIdentifier+"' ");
                      if(r.next()){
                         String Result =  r.getString(1);
                         float f =  Float.parseFloat(Result);
                         if(f<=0){
                             st4.execute("update hubrepair set inservice = true where hubIdentifier = '"+hubIdentifier+"'");
                             st4.execute("update hubdamage set inService = true where hubIdentifier = '"+hubIdentifier+"'");

                         }
                      }
                   }
      }
      catch (Exception e){
          System.out.printf(e.getMessage());
      }
    }

    // peopleOutOfService method will return the total number of people those who are out of service in given province
    int peopleOutOfService(){
      try{
          Statement st5 = connect.createStatement();
          ResultSet rs = st5.executeQuery("select sum(t1.population * t2.c2) as sum from (select  pop/c1 as population , sa from (select count(d.hubIdentifier) as c1, d.hubIdentifier as hub ,d.servicedAreas as sa , p.population pop from distributionhub d  join PostalCodes p on p.postalCode = d.servicedAreas group by d.servicedAreas) as temp) as t1\n" +
                  "join \n" +
                  "(select d.servicedAreas as service,count(d.hubIdentifier) as c2  from distributionhub as d join hubdamage as h on d.hubIdentifier = h.hubIdentifier where h.inService = false group by d.servicedAreas) as t2 on t1.sa= t2.service;");
          if(rs.next()){
            String result = rs.getString(1);
            int i = Integer.valueOf(Math.round(Float.parseFloat(result)));
            return i;
          }
      }
      catch (Exception e){
          System.out.printf(e.getMessage());
          return 0;
      }

        return 0;
    }



    // mostDamagedPostalCodes will perform the query execution and will return the list of postalCodes which most damaged
    //accordance to the hubs out service,and the returned list will be in descending order of most damaged postal codes
    List<DamagedPostalCodes> mostDamagedPostalCodes (int limit ){
          try {
              Statement st6 = connect.createStatement();
              ResultSet rs = st6.executeQuery("select d.servicedAreas, sum(h.repairEstimate) as s from distributionhub d join hubdamage h on h.hubIdentifier = d.hubIdentifier where h.inService = false group by d.servicedAreas order by  s desc; ");
              List <DamagedPostalCodes> l = new ArrayList<>();

              while(rs.next() && limit>0){
                  DamagedPostalCodes dp = new DamagedPostalCodes();
                  dp.setPostalCode(rs.getString("servicedAreas"));
                  dp.setRepairEstimate(rs.getFloat("s"));
                  l.add(dp);
                  limit--;
              }
              return l;
          }
          catch (Exception e){
              System.out.printf(e.getMessage());
              return null;
          }

    }

    //fixOrder method takes the limit and return the order of hubs by which people will regain the service per hour of repair
    //also this function will make use of the HubImpact class and store the hubid and the impact using the object
    List<HubImpact> fixOrder ( int limit ){
         try{
             Statement st7 = connect.createStatement();
             ResultSet rs = st7.executeQuery("select hb.hubIdentifier as hubi ,sum(population)/ hb.repairEstimate as impact from (select population,service,hubIdentifier from (select population, service  from (select  pop/c1 as population , sa from (select count(d.hubIdentifier) as c1, d.hubIdentifier as hub ,d.servicedAreas as sa , p.population pop from distributionhub d  join PostalCodes p on p.postalCode = d.servicedAreas group by d.servicedAreas) as temp) as t1\n" +
                     "join \n" +
                     "(select d.servicedAreas as service,count(d.hubIdentifier) as c2  from distributionhub as d join hubdamage as h on d.hubIdentifier = h.hubIdentifier where h.inService = false group by d.servicedAreas) as t2 on t1.sa= t2.service) as m1 join distributionhub d on d.servicedAreas = m1.service) as m2 join hubdamage hb on hb.hubIdentifier=m2.hubIdentifier where hb.inService =false group by hubi order by impact desc;\n");
             List<HubImpact> li = new ArrayList<>();
             while(rs.next() && limit>0){
                 HubImpact hi = new HubImpact();
                 hi.setHubIdentifier(rs.getString("hubi"));
                 hi.setImpactValue(rs.getFloat("impact"));
                 limit--;
                 li.add(hi);
             }
             return li;
         }
         catch (Exception e){
             System.out.printf(e.getMessage());
             return null;
         }
    }

    // rateOfServiceRestoration function will take an increment and will return the list containing the rate of hours of repair effort
    // with the increment percentage with total number of people, basically it will Produce an estimate of the rate with which people are restored to power
    // lot of percentage population and the linkedHashMap creation will also be done here.
    List<Integer> rateOfServiceRestoration ( float increment ) {
        List<Integer> repairEfforts = null;
        try {
            Float temp = (float) 0;
            List<Float> hubPopulation = new ArrayList<>();
            Float totalHubPopulation = (float) 0;
            List<Float> repairEstimate = new ArrayList<>();
            Float totalPeopleInService = (float) 0;
            int totalPopulation = 0;
            Statement st8 = connect.createStatement();
            ResultSet rs = st8.executeQuery("select sum(population) as sum from PostalCodes");
            if (rs.next()) {
                totalPopulation = rs.getInt("sum");
            }
            Statement st9 = connect.createStatement();
            ResultSet rs2 = st9.executeQuery("select q1.hubi, q2.sum, q2.re from (select hb.hubIdentifier as hubi ,sum(population)/ hb.repairEstimate as impact from (select population,service,hubIdentifier from (select population, service  from (select  pop/c1 as population , sa from (select count(d.hubIdentifier) as c1, d.hubIdentifier as hub ,d.servicedAreas as sa , p.population pop from distributionhub d  join PostalCodes p on p.postalCode = d.servicedAreas group by d.servicedAreas) as temp) as t1\n" +
                    "                     join\n" +
                    "                     (select d.servicedAreas as service,count(d.hubIdentifier) as c2  from distributionhub as d join hubdamage as h on d.hubIdentifier = h.hubIdentifier where h.inService = false group by d.servicedAreas) as t2 on t1.sa= t2.service) as m1 join distributionhub d on d.servicedAreas = m1.service) as m2 join hubdamage hb on hb.hubIdentifier=m2.hubIdentifier where hb.inService =false group by hubi order by impact desc) as q1 join (select sum(t1.population) as sum, t1.hubIdentifier, hubdamage.repairEstimate as re from (select population, sa , hubIdentifier from (select  pop/c1 as population , sa from (select count(d.hubIdentifier) as c1, d.hubIdentifier as hub ,d.servicedAreas as sa , p.population pop from distributionhub d  join PostalCodes p on p.postalCode = d.servicedAreas group by d.servicedAreas) as temp) as pt join distributionhub dh on dh.servicedAreas = pt.sa) as t1 join hubdamage on hubdamage.hubIdentifier = t1.hubIdentifier where hubdamage.inService = false group by t1.hubIdentifier) as q2 on q2.hubIdentifier = q1.hubi order by q1.impact desc;\n" +
                    ";");

            Statement st10 = connect.createStatement();
            ResultSet rs3 = st10.executeQuery("select sum(t1.population) as sum, t1.hubIdentifier, hubdamage.repairEstimate as re from (select population, sa , hubIdentifier from (select  pop/c1 as population , sa from (select count(d.hubIdentifier) as c1, d.hubIdentifier as hub ,d.servicedAreas as sa , p.population pop from distributionhub d  join PostalCodes p on p.postalCode = d.servicedAreas group by d.servicedAreas) as temp) as pt join distributionhub dh on dh.servicedAreas = pt.sa) as t1 join hubdamage on hubdamage.hubIdentifier = t1.hubIdentifier where hubdamage.inService = false;\n");
            if(rs3.next()){
                totalHubPopulation = rs3.getFloat("sum");
            }
            LinkedHashMap<String, Float[]> lh = new LinkedHashMap<>();
            totalPeopleInService = (float) totalPopulation - totalHubPopulation;
            if (totalPeopleInService > 0) {
                lh.put("InService", new Float[]{(totalPeopleInService / totalPopulation) * 100, (float) 0});
                temp += totalPeopleInService;
            }
            while (rs2.next()) {
                Float hubPop = rs2.getFloat("sum");
                System.out.println(hubPop);
                hubPopulation.add(hubPop);
                String hubIdentifier = rs2.getString("hubi");
                System.out.println(hubIdentifier);
                Float rE = rs2.getFloat("re");
                repairEstimate.add(rE);
                System.out.println(rE);
                temp += hubPop;
                lh.put(hubIdentifier, new Float[]{temp / totalPopulation * 100, rE});

            }

            Float increment_sum = (float) 0;
            repairEfforts = new ArrayList<>();
            Float effortHrs = (float) 0;
            for (Map.Entry<String, Float[]> entry : lh.entrySet()) {
                effortHrs+=entry.getValue()[1];
                while (increment_sum <= 100) {
                    if (increment_sum <= entry.getValue()[0]) {
                        repairEfforts.add(Integer.valueOf((int) Math.ceil(effortHrs)));
                        increment_sum += increment*100;
                    } else {

                        break;
                    }

                }
            }


            return repairEfforts;


        } catch (Exception e) {
            System.out.printf(e.getMessage());


        }

     return repairEfforts;
    }

    //core logic of  how/in which order employees will perform the repair of the hub is written in this repairPlan function
    // maximum impact of the hubs are taken into consideration while choosing the right path.
    //the function will compare all X - monotones and will return the most impacted sum path which has increasing/decreasing order of x monotones or y monotones

    List<HubImpact> repairPlan ( String startHub, int maxDistance, float maxTime ){
      try{
          Statement s20 = connect.createStatement();
          ResultSet rs = s20.executeQuery("select hb.hubIdentifier as hubi ,sum(population)/ hb.repairEstimate as impact from (select population,service,hubIdentifier from (select population, service  from (select  pop/c1 as population , sa from (select count(d.hubIdentifier) as c1, d.hubIdentifier as hub ,d.servicedAreas as sa , p.population pop from distributionhub d  join PostalCodes p on p.postalCode = d.servicedAreas group by d.servicedAreas) as temp) as t1\n" +
                  "join \n" +
                  "(select d.servicedAreas as service,count(d.hubIdentifier) as c2  from distributionhub as d join hubdamage as h on d.hubIdentifier = h.hubIdentifier where h.inService = false group by d.servicedAreas) as t2 on t1.sa= t2.service) as m1 join distributionhub d on d.servicedAreas = m1.service) as m2 join hubdamage hb on hb.hubIdentifier=m2.hubIdentifier where hb.inService =false group by hubi order by impact desc;\n");

          HashSet<HubImpact> li = new HashSet<>();

          while(rs.next()){
              HubImpact hi = new HubImpact();
              hi.setHubIdentifier(rs.getString("hubi"));
              hi.setImpactValue(rs.getFloat("impact"));
              li.add(hi);
          }
          LinkedHashMap<String,Float> kj = new LinkedHashMap<>();
          for(HubImpact l : li){
              kj.put(l.getHubIdentifier(),l.getImpactValue());
          }
          int totalPopulation = 0;
          if (rs.next()) {
              totalPopulation = rs.getInt("sum");
          }
          Statement st9 = connect.createStatement();
          ResultSet rs2 = st9.executeQuery("select q1.hubi, q2.sum, q2.re from (select hb.hubIdentifier as hubi ,sum(population)/ hb.repairEstimate as impact from (select population,service,hubIdentifier from (select population, service  from (select  pop/c1 as population , sa from (select count(d.hubIdentifier) as c1, d.hubIdentifier as hub ,d.servicedAreas as sa , p.population pop from distributionhub d  join PostalCodes p on p.postalCode = d.servicedAreas group by d.servicedAreas) as temp) as t1\n" +
                  "                     join\n" +
                  "                     (select d.servicedAreas as service,count(d.hubIdentifier) as c2  from distributionhub as d join hubdamage as h on d.hubIdentifier = h.hubIdentifier where h.inService = false group by d.servicedAreas) as t2 on t1.sa= t2.service) as m1 join distributionhub d on d.servicedAreas = m1.service) as m2 join hubdamage hb on hb.hubIdentifier=m2.hubIdentifier where hb.inService =false group by hubi order by impact desc) as q1 join (select sum(t1.population) as sum, t1.hubIdentifier, hubdamage.repairEstimate as re from (select population, sa , hubIdentifier from (select  pop/c1 as population , sa from (select count(d.hubIdentifier) as c1, d.hubIdentifier as hub ,d.servicedAreas as sa , p.population pop from distributionhub d  join PostalCodes p on p.postalCode = d.servicedAreas group by d.servicedAreas) as temp) as pt join distributionhub dh on dh.servicedAreas = pt.sa) as t1 join hubdamage on hubdamage.hubIdentifier = t1.hubIdentifier where hubdamage.inService = false group by t1.hubIdentifier) as q2 on q2.hubIdentifier = q1.hubi order by q1.impact desc;\n" +
                  ";");

          Statement st10 = connect.createStatement();
          ResultSet rs3 = st10.executeQuery("select sum(t1.population) as sum, t1.hubIdentifier, hubdamage.repairEstimate as re from (select population, sa , hubIdentifier from (select  pop/c1 as population , sa from (select count(d.hubIdentifier) as c1, d.hubIdentifier as hub ,d.servicedAreas as sa , p.population pop from distributionhub d  join PostalCodes p on p.postalCode = d.servicedAreas group by d.servicedAreas) as temp) as pt join distributionhub dh on dh.servicedAreas = pt.sa) as t1 join hubdamage on hubdamage.hubIdentifier = t1.hubIdentifier where hubdamage.inService = false;\n");
          float totalHubPopulation = 0;
          if(rs3.next()){
              totalHubPopulation = rs3.getFloat("sum");
          }
          LinkedHashMap<String, Float[]> lh = new LinkedHashMap<>();
          float totalPeopleInService = (float) totalPopulation - totalHubPopulation;
          if (totalPeopleInService > 0) {
              lh.put("InService", new Float[]{(totalPeopleInService / totalPopulation) * 100, (float) 0});
              float temp = totalPeopleInService;
          }
          


          List<HubImpact> hj = new ArrayList<>(li);
          return hj;
      }
      catch (Exception e){
          System.out.println(e.getMessage());
          return null;
      }


    }

    //function underservedPostalByPopulation will return the list of postalcodes in descending order, in which,
    //number of hubs per person in postalcodes are in decreasing order, and these are the postal codes which needs more service
    List<String> underservedPostalByPopulation ( int limit ){
         try {
             Statement s13 = connect.createStatement();
             ResultSet rs =s13.executeQuery("select t1.servicedAreas from (select servicedAreas, count(hubIdentifier) as c from distributionhub group by servicedAreas) as t1 join PostalCodes pc on pc.postalCode = t1.servicedAreas order by pc.population/c desc limit "+limit+";");
             List<String> postalCodes = new ArrayList<>();
             while(rs.next()){
                 postalCodes.add(rs.getString("servicedAreas"));
             }
             return postalCodes;
         }
         catch (Exception e){
             System.out.println(e.getMessage());
             return null;
         }

    }

    //function underservedPostalByArea will return the list of postalcodes in descending order, in which,
    //number of hubs per area are lower, and the return list is in descening order
    List<String> underservedPostalByArea ( int limit ){
       try{
           Statement s14 = connect.createStatement();
           ResultSet rs = s14.executeQuery("select t1.servicedAreas from (select servicedAreas, count(hubIdentifier) as c from distributionhub group by servicedAreas) as t1 join PostalCodes pc on pc.postalCode = t1.servicedAreas order by pc.area/c desc limit "+limit+";");
           List<String> area = new ArrayList<>();
           while (rs.next()){
               area.add(rs.getString("servicedAreas"));
           }
           return area;
       }
       catch (Exception e){
           System.out.println(e.getMessage());
           return null;
       }
    }

    public void resetDB(){
        try {
            Statement s15 = connect.createStatement();
            s15.execute("truncate table distributionhub;");
            Statement s16 = connect.createStatement();
            s16.execute("truncate table hubdamage;");
            Statement s17 = connect.createStatement();
            s17.execute("truncate table hubrepair;");
            Statement s18 = connect.createStatement();
            s18.execute("truncate table PostalCodes;");


        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}
