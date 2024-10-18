package casp.web.backend.deprecated.dtos.event.participants;

import casp.web.backend.deprecated.dtos.dog.DogHasHandlerDto;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public class SpaceReadDto extends BaseSpaceDto {
    private DogHasHandlerDto dogHasHandler;

    private SimpleCourseDto course;

    public DogHasHandlerDto getDogHasHandler() {
        return dogHasHandler;
    }

    public void setDogHasHandler(final DogHasHandlerDto dogHasHandler) {
        this.dogHasHandler = dogHasHandler;
    }

    public SimpleCourseDto getCourse() {
        return course;
    }

    public void setCourse(final SimpleCourseDto course) {
        this.course = course;
    }
}
