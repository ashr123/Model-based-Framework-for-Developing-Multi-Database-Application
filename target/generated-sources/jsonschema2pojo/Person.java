import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * A person class
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "phone-number",
    "email-address",
    "lives-at-javaType",
    "lives-at-ref"
})
public class Person {

    @JsonProperty("name")
    private String name;
    @JsonProperty("phone-number")
    private String phoneNumber;
    @JsonProperty("email-address")
    private String emailAddress;
    @JsonProperty("lives-at-javaType")
    private Address livesAtJavaType;
    /**
     * An address class
     * 
     */
    @JsonProperty("lives-at-ref")
    @JsonPropertyDescription("An address class")
    private Address livesAtRef;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("phone-number")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @JsonProperty("phone-number")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @JsonProperty("email-address")
    public String getEmailAddress() {
        return emailAddress;
    }

    @JsonProperty("email-address")
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @JsonProperty("lives-at-javaType")
    public Address getLivesAtJavaType() {
        return livesAtJavaType;
    }

    @JsonProperty("lives-at-javaType")
    public void setLivesAtJavaType(Address livesAtJavaType) {
        this.livesAtJavaType = livesAtJavaType;
    }

    /**
     * An address class
     * 
     */
    @JsonProperty("lives-at-ref")
    public Address getLivesAtRef() {
        return livesAtRef;
    }

    /**
     * An address class
     * 
     */
    @JsonProperty("lives-at-ref")
    public void setLivesAtRef(Address livesAtRef) {
        this.livesAtRef = livesAtRef;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Person.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null)?"<null>":this.name));
        sb.append(',');
        sb.append("phoneNumber");
        sb.append('=');
        sb.append(((this.phoneNumber == null)?"<null>":this.phoneNumber));
        sb.append(',');
        sb.append("emailAddress");
        sb.append('=');
        sb.append(((this.emailAddress == null)?"<null>":this.emailAddress));
        sb.append(',');
        sb.append("livesAtJavaType");
        sb.append('=');
        sb.append(((this.livesAtJavaType == null)?"<null>":this.livesAtJavaType));
        sb.append(',');
        sb.append("livesAtRef");
        sb.append('=');
        sb.append(((this.livesAtRef == null)?"<null>":this.livesAtRef));
        sb.append(',');
        sb.append("additionalProperties");
        sb.append('=');
        sb.append(((this.additionalProperties == null)?"<null>":this.additionalProperties));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.livesAtJavaType == null)? 0 :this.livesAtJavaType.hashCode()));
        result = ((result* 31)+((this.emailAddress == null)? 0 :this.emailAddress.hashCode()));
        result = ((result* 31)+((this.phoneNumber == null)? 0 :this.phoneNumber.hashCode()));
        result = ((result* 31)+((this.name == null)? 0 :this.name.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        result = ((result* 31)+((this.livesAtRef == null)? 0 :this.livesAtRef.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Person) == false) {
            return false;
        }
        Person rhs = ((Person) other);
        return (((((((this.livesAtJavaType == rhs.livesAtJavaType)||((this.livesAtJavaType!= null)&&this.livesAtJavaType.equals(rhs.livesAtJavaType)))&&((this.emailAddress == rhs.emailAddress)||((this.emailAddress!= null)&&this.emailAddress.equals(rhs.emailAddress))))&&((this.phoneNumber == rhs.phoneNumber)||((this.phoneNumber!= null)&&this.phoneNumber.equals(rhs.phoneNumber))))&&((this.name == rhs.name)||((this.name!= null)&&this.name.equals(rhs.name))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))))&&((this.livesAtRef == rhs.livesAtRef)||((this.livesAtRef!= null)&&this.livesAtRef.equals(rhs.livesAtRef))));
    }

}
