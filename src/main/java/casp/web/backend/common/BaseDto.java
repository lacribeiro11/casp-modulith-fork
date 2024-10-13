package casp.web.backend.common;

import java.util.Objects;
import java.util.UUID;

abstract class BaseDto {
    protected UUID id;
    protected long version;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseDto baseDto)) return false;
        return Objects.equals(id, baseDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
