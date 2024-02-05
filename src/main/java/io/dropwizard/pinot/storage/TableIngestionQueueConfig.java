package io.dropwizard.pinot.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableIngestionQueueConfig {

    @NotBlank
    private String kafkaTopic;

}
