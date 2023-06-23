package fr.recia.paramuseretab.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person implements Serializable{

    // Bean uid 
    @NonNull
    protected String uid;

    // current structure 
    @NonNull
    protected String currentStruct;

    private List<Map<String, String>> isMemberOf;

    
}