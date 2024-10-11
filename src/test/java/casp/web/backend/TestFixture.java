package casp.web.backend;

import casp.web.backend.data.access.layer.dog.Dog;
import casp.web.backend.data.access.layer.member.Member;
import casp.web.backend.data.access.layer.member.MembershipFee;
import casp.web.backend.deprecated.dog.DogHasHandler;
import casp.web.backend.deprecated.event.calendar.Calendar;
import casp.web.backend.deprecated.event.options.DailyEventOption;
import casp.web.backend.deprecated.event.options.WeeklyEventOption;
import casp.web.backend.deprecated.event.options.WeeklyEventOptionRecurrence;
import casp.web.backend.deprecated.event.participants.CoTrainer;
import casp.web.backend.deprecated.event.participants.EventParticipant;
import casp.web.backend.deprecated.event.participants.ExamParticipant;
import casp.web.backend.deprecated.event.participants.Space;
import casp.web.backend.deprecated.event.types.BaseEvent;
import casp.web.backend.deprecated.event.types.Course;
import casp.web.backend.deprecated.event.types.Event;
import casp.web.backend.deprecated.event.types.Exam;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public final class TestFixture {
    private static final Validator VALIDATOR;

    static {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            VALIDATOR = validatorFactory.getValidator();
        }
    }

    private TestFixture() {
    }

    public static <T> Set<ConstraintViolation<T>> getViolations(final T object) {
        return VALIDATOR.validate(object);
    }

    public static WeeklyEventOptionRecurrence createWeeklyEventOptionRecurrence() {
        var weeklyEventOptionRecurrence = new WeeklyEventOptionRecurrence();
        weeklyEventOptionRecurrence.setDayOfWeek(DayOfWeek.MONDAY);
        weeklyEventOptionRecurrence.setStartTime(LocalTime.MIN);
        weeklyEventOptionRecurrence.setEndTime(LocalTime.MAX);
        return weeklyEventOptionRecurrence;
    }

    public static Event createEvent() {
        var member = createMember();
        var event = new Event();
        event.setName("Test Event");
        event.setMemberId(member.getId());
        event.setMember(member);
        return event;
    }

    public static Member createMember() {
        return createMember("John", "Doe");
    }

    public static Member createMember(final String firstName, final String lastName) {
        var member = new Member();
        member.setFirstName(firstName);
        member.setLastName(lastName);
        member.setEmail("%s@example.com".formatted(member.getId()));
        return member;
    }

    public static Calendar createCalendarEntry(final BaseEvent baseEvent) {
        var calendar = new Calendar();
        calendar.setEventFrom(LocalDateTime.now());
        calendar.setEventTo(LocalDateTime.now().plusHours(1));
        calendar.setBaseEvent(baseEvent);
        return calendar;
    }

    public static Calendar createCalendarEntry() {
        return createCalendarEntry(createEvent());
    }

    public static Exam createExam() {
        var member = createMember();
        var exam = new Exam();
        exam.setName("Exam 1");
        exam.setMemberId(member.getId());
        exam.setJudgeName("Judge");
        exam.setMember(member);
        return exam;
    }

    public static Course createCourse() {
        var member = createMember();
        var course = new Course();
        course.setName("Course Name");
        course.setMemberId(member.getId());
        course.setMember(member);
        return course;
    }

    public static Space createSpace() {
        return createSpace(createCourse());
    }

    public static Space createSpace(final Course course) {
        var space = new Space();
        var dogHasHandler = createDogHasHandler();
        space.setMemberOrHandlerId(dogHasHandler.getId());
        space.setDogHasHandler(dogHasHandler);
        space.setBaseEvent(course);
        return space;
    }

    public static DogHasHandler createDogHasHandler() {
        var member = createMember();
        var dog = createDog();
        return createDogHasHandler(dog, member);
    }

    public static DogHasHandler createDogHasHandler(final Dog dog, final Member member) {
        var dogHasHandler = new DogHasHandler();
        dogHasHandler.setDogId(dog.getId());
        dogHasHandler.setDog(dog);
        dogHasHandler.setMemberId(member.getId());
        dogHasHandler.setMember(member);
        return dogHasHandler;
    }

    public static Dog createDog() {
        var dog = new Dog();
        dog.setName("Riley");
        dog.setOwnerName("John Doe");
        dog.setOwnerAddress("123 Main St");
        return dog;
    }

    public static EventParticipant createEventParticipant() {
        return createEventParticipant(createEvent());
    }

    public static EventParticipant createEventParticipant(final Event event) {
        var eventParticipant = new EventParticipant();
        eventParticipant.setMemberOrHandlerId(createMember().getId());
        eventParticipant.setBaseEvent(event);
        return eventParticipant;
    }

    public static ExamParticipant createExamParticipant() {
        return createExamParticipant(createExam());
    }

    public static ExamParticipant createExamParticipant(final Exam exam) {
        var examParticipant = new ExamParticipant();
        examParticipant.setMemberOrHandlerId(createDogHasHandler().getId());
        examParticipant.setBaseEvent(exam);
        return examParticipant;
    }

    public static CoTrainer createCoTrainer() {
        return createCoTrainer(createCourse());
    }

    public static CoTrainer createCoTrainer(final Course course) {
        var coTrainer = new CoTrainer();
        coTrainer.setBaseEvent(course);
        coTrainer.setMemberOrHandlerId(createMember().getId());
        return coTrainer;
    }

    public static DailyEventOption createDailyEventOption() {
        var dailyEventOption = new DailyEventOption();
        dailyEventOption.setStartRecurrence(LocalDate.MIN);
        dailyEventOption.setEndRecurrence(LocalDate.MAX);
        dailyEventOption.setStartTime(LocalTime.MIN);
        dailyEventOption.setEndTime(LocalTime.MAX);
        return dailyEventOption;
    }

    public static WeeklyEventOption createWeeklyEventOption() {
        var weeklyEventOption = new WeeklyEventOption();
        weeklyEventOption.setStartRecurrence(LocalDate.MIN);
        weeklyEventOption.setEndRecurrence(LocalDate.MAX);
        weeklyEventOption.setOccurrences(List.of(createWeeklyEventOptionRecurrence()));
        return weeklyEventOption;
    }

    public static MembershipFee createMembershipFee() {
        var membershipFee = new MembershipFee();
        membershipFee.setPaid(true);
        membershipFee.setPaidDate(LocalDate.now());
        membershipFee.setPaidPrice(10.0);
        return membershipFee;
    }
}
