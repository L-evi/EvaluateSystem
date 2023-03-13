package com.project.evaluate.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Levi
 * @description
 * @since 2023/2/26 02:33
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Major implements Serializable {
    @JsonProperty("ID")
    Integer ID;
    @JsonProperty("majorName")
    String majorName;
}
