package br.com.alura.school.course;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CourseEnrollmentId
        implements Serializable {

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "user_id")
    private Long userId;

    private CourseEnrollmentId() {}

    public CourseEnrollmentId(
            Long courseId,
            Long userId) {
        this.courseId = courseId;
        this.userId = userId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        CourseEnrollmentId that = (CourseEnrollmentId) o;
        return Objects.equals(courseId, that.courseId) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, userId);
    }
}