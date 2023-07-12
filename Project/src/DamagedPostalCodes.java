//the class DamagedPostalCodes is used set the postalcode and the repairestimate time of that postalcode in a object
// the class object is used in the mostDamagedPostalCodes method
public class DamagedPostalCodes {

    private String postalCode;
    private Float repairEstimate;

    public void setPostalCode(String postalCode){
          this.postalCode = postalCode;

    }

    public void setRepairEstimate(Float repairEstimate){
        this.repairEstimate = repairEstimate;
    }

    public String  getPostalCode(){
      return this.postalCode;
    }

    public Float getRepairEstimate(){
        return this.repairEstimate;
    }

}
