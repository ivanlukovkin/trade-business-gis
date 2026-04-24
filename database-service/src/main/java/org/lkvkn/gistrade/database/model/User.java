package org.lkvkn.gistrade.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class User {

    private Long id;
    private String fullName;
    private String username;
    private String password;

    @Builder.Default
    private String role = "USER";

    @Override
    public String toString() {
        return String.format("User(id: '%s', fullName: '%s')", id, fullName);
    }
    
}
