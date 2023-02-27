package br.com.alura.school.course;

import br.com.alura.school.user.User;
import br.com.alura.school.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;


@RestController
class CourseController {

    private final CourseRepository courseRepository;

    private final UserRepository userRepository;
    private final EntityManager entityManager;

    CourseController(CourseRepository courseRepository, UserRepository userRepository, EntityManager entityManager) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    @GetMapping(value = "/courses")
    ResponseEntity<List<CourseResponse>> allCourses() {
        List<Course> courses = courseRepository.findAll();
        List<CourseResponse> courseResponses = courses.stream().map(CourseResponse::new).collect(Collectors.toList());
        return new ResponseEntity<>(courseResponses, HttpStatus.OK);
    }

    @GetMapping("/courses/{code}")
    ResponseEntity<CourseResponse> courseByCode(@PathVariable("code") String code) {
        Course course = courseRepository.findByCode(code).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, format("Course with code %s not found", code)));
        return ResponseEntity.ok(new CourseResponse(course));
    }

    @PostMapping("/courses")
    ResponseEntity<Void> newCourse(@RequestBody @Valid NewCourseRequest newCourseRequest) {
        courseRepository.save(newCourseRequest.toEntity());
        URI location = URI.create(format("/courses/%s", newCourseRequest.getCode()));
        return ResponseEntity.created(location).build();
    }

    @PostMapping(value = "/courses/{code}/enroll", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> newEnroll(@PathVariable("code") String code, @RequestBody @Valid NewEnrollmentRequest newEnrollmentRequest) throws Exception {
        User user = userRepository.findByUsername(newEnrollmentRequest.getUsername()).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
        Course course = courseRepository.findByCode(code).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, format("Course with code %s not found", code)));
        course.addUser(user);
        if (course.getEnrolledUsers().contains(user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        entityManager.clear();
        courseRepository.save(course);
        return ResponseEntity.status(CREATED).build();
    }
}
