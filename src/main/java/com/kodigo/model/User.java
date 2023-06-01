package com.kodigo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private String username;

    private String password;

    private Boolean status;

    private List<Role> roles;
}
