package de.muenchen.oss.dbs.ticketing.eventing.dms.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * Target for an DMS action.
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DmsTarget extends DmsRequestContext {
    /**
     * COO of an object.
     */
    private String coo;

    public DmsTarget(final String coo, @NotBlank final String username, final String joboe, final String jobposition) {
        super(username, joboe, jobposition);
        this.coo = coo;
    }

    public DmsTarget(final String coo, @NotNull final DmsRequestContext context) {
        this(coo, context.getUsername(), context.getJoboe(), context.getJobposition());
        Objects.requireNonNull(context.getUsername(), "username must not be null");
    }

    /**
     * If the DmsTarget hat any value unequal to an empty string.
     *
     * @return If some value is set.
     */
    public boolean hasValues() {
        return StringUtils.isNotBlank(coo) || StringUtils.isNotBlank(getUsername()) ||
                StringUtils.isNotBlank(getJoboe()) || StringUtils.isNotBlank(getJobposition());
    }
}
