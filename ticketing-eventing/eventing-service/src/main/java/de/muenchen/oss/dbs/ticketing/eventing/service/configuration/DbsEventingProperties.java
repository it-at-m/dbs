package de.muenchen.oss.dbs.ticketing.eventing.service.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties("dbs.eventing")
@Getter
@Setter
@Validated
public class DbsEventingProperties {
    /**
     * Map for transforming trigger name into action.
     * Decouples trigger name from event action.
     */
    @NestedConfigurationProperty
    @Valid @NotNull private List<TriggerMatching> triggerMapping;

    /**
     * Maps a trigger name to the according action.
     * See {@link #triggerMapping}
     *
     * @param triggerName Name of the trigger
     * @return According action
     */
    public String mapTriggerToAction(final String triggerName) {
        return triggerMapping.stream()
                // TODO case insensitive?
                .filter(i -> i.triggerName.equals(triggerName))
                .findFirst()
                .map(TriggerMatching::action).orElse(null);
    }

    /**
     * Mapping from trigger name to action.
     *
     * @param triggerName The name of the trigger.
     * @param action The action to map to.
     */
    public record TriggerMatching(
            @NotBlank String triggerName,
            @NotBlank String action) {
    }
}
