package casp.web.backend.business.logic.layer.dog;

import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EuropeNetTasks {
    Page<DogDto> registerDogsManually(@Nullable Pageable pageRequest);
}
