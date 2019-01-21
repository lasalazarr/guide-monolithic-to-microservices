package org.ecjug.hackday.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Group {

    @JsonIgnore
    private ObjectId id;
    @NotBlank
    private String name;
    private String description;
    private String link;
    @NotBlank
    private String urlname;
    @NotBlank
    private String country;
    private Country countryInformation;
    private List<Member> membersList;

    private String groupId;


    public void addMember(Member member) {
        if (membersList == null) {
            membersList = new ArrayList<>();
        }
        if (!membersList.contains(member)) {
            membersList.add(member);
        }
    }

    public String getGroupId() {
        if (id != null) {
            this.groupId = id.toString();
        }
        return this.groupId;
    }
}
