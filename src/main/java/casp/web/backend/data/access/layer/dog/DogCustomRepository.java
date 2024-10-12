package casp.web.backend.data.access.layer.dog;

import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DogCustomRepository {
    Page<Dog> findAllByNameOrOwnerName(@Nullable String dogName, @Nullable String ownerName, Pageable pageable);

    Page<Dog> findAllByEuropeNetStateNotChecked(Pageable pageable);
}
