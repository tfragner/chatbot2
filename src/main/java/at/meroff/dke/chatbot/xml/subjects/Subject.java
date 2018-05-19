package at.meroff.dke.chatbot.xml.subjects;

import java.io.Serializable;
import java.util.Objects;

/**
 * A Subject.
 */

public class Subject implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String subjectName;

    private SubjectType subjectType;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public Subject subjectName(String subjectName) {
        this.subjectName = subjectName;
        return this;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public SubjectType getSubjectType() {
        return subjectType;
    }

    public Subject subjectType(SubjectType subjectType) {
        this.subjectType = subjectType;
        return this;
    }

    public void setSubjectType(SubjectType subjectType) {
        this.subjectType = subjectType;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Subject subject = (Subject) o;
        if (subject.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), subject.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Subject{" +
            "id=" + getId() +
            ", subjectName='" + getSubjectName() + "'" +
            ", subjectType='" + getSubjectType() + "'" +
            "}";
    }
}
