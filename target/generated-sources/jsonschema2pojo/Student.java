import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * A student class
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "student-number",
    "average-mark"
})
public class Student
    extends Person
{

    @JsonProperty("student-number")
    private String studentNumber;
    @JsonProperty("average-mark")
    private Double averageMark;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("student-number")
    public String getStudentNumber() {
        return studentNumber;
    }

    @JsonProperty("student-number")
    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    @JsonProperty("average-mark")
    public Double getAverageMark() {
        return averageMark;
    }

    @JsonProperty("average-mark")
    public void setAverageMark(Double averageMark) {
        this.averageMark = averageMark;
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
        sb.append(Student.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        int baseLength = sb.length();
        String superString = super.toString();
        if (superString!= null) {
            int contentStart = superString.indexOf('[');
            int contentEnd = superString.lastIndexOf(']');
            if ((contentStart >= 0)&&(contentEnd >contentStart)) {
                sb.append(superString, (contentStart + 1), contentEnd);
            } else {
                sb.append(superString);
            }
        }
        if (sb.length()>baseLength) {
            sb.append(',');
        }
        sb.append("studentNumber");
        sb.append('=');
        sb.append(((this.studentNumber == null)?"<null>":this.studentNumber));
        sb.append(',');
        sb.append("averageMark");
        sb.append('=');
        sb.append(((this.averageMark == null)?"<null>":this.averageMark));
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
        result = ((result* 31)+((this.averageMark == null)? 0 :this.averageMark.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        result = ((result* 31)+((this.studentNumber == null)? 0 :this.studentNumber.hashCode()));
        result = ((result* 31)+ super.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Student) == false) {
            return false;
        }
        Student rhs = ((Student) other);
        return (((super.equals(rhs)&&((this.averageMark == rhs.averageMark)||((this.averageMark!= null)&&this.averageMark.equals(rhs.averageMark))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))))&&((this.studentNumber == rhs.studentNumber)||((this.studentNumber!= null)&&this.studentNumber.equals(rhs.studentNumber))));
    }

}
