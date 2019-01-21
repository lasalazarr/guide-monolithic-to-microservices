package org.ecjug.hackday.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event implements Serializable {

    private ObjectId id;
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotBlank
    private LocalDate date;

    private List<Member> memberList;

    public void addMember(Member member) {
        if (memberList == null) {
            memberList = new ArrayList<>();
        }
        if (!memberList.contains(member)) {
            memberList.add(member);
        }
    }

}
