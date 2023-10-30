package ru.netology.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    /**
     * @JsonProperty(name) сообщает Jackson ObjectMapper сопоставить имя свойства JSON с именем аннотированного поля Java.
     *
     * //example of json that is submitted
     * "Car":{
     *   "Type":"Ferrari",
     * }
     *
     * //where it gets mapped
     * public static class Car {
     *   @JsonProperty("Type")
     *   public String type;
     *  }
     *
     *  "AuthResponse": {
     *      "auth-token" : "auth-token"
     *  }
     */

    @JsonProperty("auth-token")
    private String authToken;
}