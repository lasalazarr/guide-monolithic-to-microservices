package org.ecjug.hackday.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Country implements Serializable {

    @NotBlank
    private String name;
    private String alpha2Code;
    private String alpha3Code;
    private String capital;
    private String flag;
    private List<String> timezones;

}
