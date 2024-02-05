package io.dropwizard.pinot.repository.pinot.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class InstanceTagUpdateRequest {
    private String instanceName;
    private List<String> newTags;
}
