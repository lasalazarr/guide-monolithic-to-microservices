package org.ecjug.hackday.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class Member implements Serializable {

    @JsonIgnore
    private ObjectId id;
    @NotBlank
    private String name;
    private String country;
    private String city;
    private String comments;

    private String memberId;


    public String getMemberId() {
        if (id != null) {
            memberId = id.toString();
        }
        return memberId;
    }


}
