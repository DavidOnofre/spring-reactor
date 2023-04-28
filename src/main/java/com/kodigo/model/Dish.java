package com.kodigo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Dish {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    @Size(min = 3)
    @Field //@Field es opcional, xq va a tomar el nombre del atributo
    private String name;

    @Min(value = 1)
    @Field
    private Double price;

    @NotNull
    @Field
    private Boolean status;

}
