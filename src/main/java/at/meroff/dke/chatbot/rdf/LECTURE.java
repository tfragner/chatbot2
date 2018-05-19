package at.meroff.dke.chatbot.rdf;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public class LECTURE {

    private static final Model M_MODEL = ModelFactory.createDefaultModel();
    public static final String NS = "http://jku.at/lecture#";
    public static final Resource NAMESPACE;
    public static final Resource Curriculum;
    public static final Resource Semester;
    public static final Resource Subject;
    public static final Resource SubjectType;
    public static final Resource Course;
    public static final Resource Lva;
    public static final Property curId;
    public static final Property name;
    public static final Property year;
    public static final Property lvaType;
    public static final Property semester;
    public static final Property isLvaType;
    public static final Property belongsToCurriculum;
    public static final Property courseNr;
    public static final Property belongsToSemester;
    public static final Property isTeaching;
    public static final Property hasLecture;
    public static final Property belongsToSubject;
    public static final Property hasSubject;
    public static final Property hasTeacher;
    public static final Property isCourseFor;
    public static final Property isLvaFor;
    public static final Property teachedBy;



    public LECTURE() {

    }

    public static String getURI() {
        return NS;
    }

    static {
        NAMESPACE = M_MODEL.createResource(NS);
        Curriculum = M_MODEL.createResource(NS + "Curriculum");
        Lva = M_MODEL.createResource(NS + "Lva");
        Semester = M_MODEL.createResource(NS + "Semester");
        Subject = M_MODEL.createResource(NS + "Subject");
        SubjectType = M_MODEL.createResource(NS + "SubjectType");
        Course = M_MODEL.createResource(NS + "Course");
        curId = M_MODEL.createProperty(NS + "curId");
        name = M_MODEL.createProperty(NS + "name");
        year = M_MODEL.createProperty(NS + "year");
        semester = M_MODEL.createProperty(NS + "semester");
        isLvaType = M_MODEL.createProperty(NS + "isType");
        belongsToCurriculum = M_MODEL.createProperty(NS + "belongsToCurriculum");
        courseNr = M_MODEL.createProperty(NS + "courseNr");
        belongsToSemester = M_MODEL.createProperty(NS + "belongsToSemester");
        isTeaching = M_MODEL.createProperty(NS + "isTeaching");
        hasLecture = M_MODEL.createProperty(NS + "hasLecture");
        belongsToSubject = M_MODEL.createProperty(NS + "belongsToSubject");
        hasSubject = M_MODEL.createProperty(NS + "hasSubject");
        hasTeacher = M_MODEL.createProperty(NS + "hasTeacher");
        isCourseFor = M_MODEL.createProperty(NS + "isCourseFor");
        isLvaFor = M_MODEL.createProperty(NS + "isLvaFor");
        teachedBy = M_MODEL.createProperty(NS + "teachedBy");
        lvaType = M_MODEL.createProperty(NS + "lvaType");


    }

}
