package com.kodigo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // para comparar 2 objetos y no sean comparados por espacios en memoria.
@Document(collection = "dishes")
public class Dish {

    @Id
    private String id;

    @Field //@Field es opcional, xq va a tomar el nombre del atributo
    private String name;

    @Field
    private Double price;

    @Field
    private Boolean status;


}
