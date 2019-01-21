package org.ecjug.hackday.mongo.test;

import lombok.*;
import org.bson.types.ObjectId;


@Builder(toBuilder = true)
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JUG {

    private ObjectId id;
    private String jugName;
    private String country;
    private int members;

}