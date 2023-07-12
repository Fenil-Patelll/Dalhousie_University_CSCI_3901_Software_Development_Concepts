//HubImpact class is used to store the Hubid and the Impact value of the hub
//for the fixOrder method
public class HubImpact {
   String hubIdentifier;
   Float impactValue;

    public void setHubIdentifier(String hubIdentifier) {
        this.hubIdentifier = hubIdentifier;
    }

    public void setImpactValue(Float impactValue) {
        this.impactValue = impactValue;
    }

    public Float getImpactValue() {
        return impactValue;
    }

    public String getHubIdentifier() {
        return hubIdentifier;
    }
}
