package io.dropwizard.pinot.housekeeping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RealtimeServerTaggingRequest {

    private Map<String, Integer> tenantToTaggingCountMap;

}
