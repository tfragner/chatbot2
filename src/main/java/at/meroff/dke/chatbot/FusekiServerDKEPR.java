package at.meroff.dke.chatbot;

import at.meroff.dke.chatbot.rdf.LECTURE;
import at.meroff.dke.chatbot.xml.XMLQueryTemplate;
import at.meroff.dke.chatbot.xml.lvas.Teacher;
import at.meroff.dke.chatbot.xml.lvas.XmlLva;
import at.meroff.dke.chatbot.xml.lvas.XmlLvas;
import at.meroff.dke.chatbot.xml.subjects.SubjectType;
import at.meroff.dke.chatbot.xml.subjects.Subjects;
import openllet.jena.PelletReasonerFactory;
import org.apache.jena.fuseki.FusekiLogging;
import org.apache.jena.fuseki.embedded.FusekiServer;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.sparql.vocabulary.FOAF;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.stream.Collectors;

public class FusekiServerDKEPR {

    private Dataset ds;

    public FusekiServerDKEPR() {
        this.init();

    }

    private void init() {
        FusekiLogging.setLogging();




        //this.ds = DatasetFactory.createTxnMem();
        //this.ds.setDefaultModel(model);

        Reasoner reasoner = PelletReasonerFactory.theInstance().create();
        Model empty = ModelFactory.createDefaultModel();
        InfModel model = ModelFactory.createInfModel(reasoner, empty);

        //model.read("/Users/fragner/Desktop/dke-pr-ontology.owl");
        model.read(getClass().getResourceAsStream("/ontology/dke-pr-ontology.owl"),null);

        this.ds = DatasetFactory.create(model);
        //this.ds.setDefaultModel(model);

        FusekiServer server = FusekiServer.create()
                .add("/ds", ds)
                .setPort(3333)
                .build();
        server.start() ;
    }

    private void importCurriculum(int curID, String curName, int year, Semester semester) {
        Resource curriculum = createCurriculum(curID, curName);

        Set<Resource> subjects = importSubjects(curID, getSemesterString(year, semester));

        Model model = this.ds.getDefaultModel();

        subjects.forEach(resource -> {
            model.add(resource, LECTURE.isCourseFor, curriculum);
        });

        Set<Resource> lvas = importLvas(curID, getSemesterString(year, semester));

    }

    private Set<Resource> importLvas(int curID, String semester) {
        BufferedReader reader = null;

        InputStream in = getClass().getResourceAsStream("/xquery/lvas.xq");

        try {
            reader = new BufferedReader(new InputStreamReader(in));

            // read query stream
            String query = "";
            for (String line; (line = reader.readLine()) != null; query += (line + "\n"));

            // replace parameters
            query = query.replace("INSERTCURID", Integer.toString(curID));
            query = query.replace("INSERTYEARSEMESTER", semester);

            // transform xquery result into Subjects
            XMLQueryTemplate<XmlLvas> xmlQuerySubjects = new XMLQueryTemplate<>(query, XmlLvas.class);
            XmlLvas lvas = xmlQuerySubjects.getResult();

            return lvas.getLvas().stream().map(xmlLva -> {
                Resource lva = createLVA(xmlLva, semester);
                //Set<Resource> teachers = createTeachers(xmlLva.getTeachers());

                //createLinkToLva(teachers, lva);

                return lva;
            }).collect(Collectors.toSet());


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private Set<Resource> createTeachers(Set<Teacher> teachers) {

        Model model = this.ds.getDefaultModel();

        return teachers.stream().map(teacher -> {
            String identifier = teacher.getLastname().replaceAll(" ","") + teacher.getFirstname().replaceAll(" ","");
            return model.createResource(LECTURE.NS + identifier, FOAF.Person)
                    .addProperty(FOAF.name, teacher.getLastname() + " " + teacher.getFirstname())
                    .addProperty(FOAF.firstName, teacher.getFirstname())
                    .addProperty(FOAF.lastName, teacher.getLastname());
        }).collect(Collectors.toSet());
    }

    private Resource createLVA(XmlLva xmlLva, String semester) {
        Model model = this.ds.getDefaultModel();

        Resource lva = model.createResource(LECTURE.NS + xmlLva.getId() + "_" + semester, LECTURE.Lva)
                .addProperty(LECTURE.isLvaFor, model.getResource(LECTURE.NS + xmlLva.getName().replaceAll(" ", "") + "_" + xmlLva.getSubjectType()))
                .addProperty(LECTURE.year, Integer.toString(xmlLva.getTermYear()))
                .addProperty(LECTURE.semester, xmlLva.getTermSemester().toString());

        Set<Resource> teachers = createTeachers(xmlLva.getTeachers());

        teachers.forEach(resource -> {
            model.add(lva, LECTURE.teachedBy, resource);
        });

        return lva;
    }

    private Set<Resource> importSubjects(int curID, String semester) {
        BufferedReader reader = null;

        InputStream in = getClass().getResourceAsStream("/xquery/subjects.xq");

        try {
            reader = new BufferedReader(new InputStreamReader(in));

            // read query stream
            String query = "";
            for (String line; (line = reader.readLine()) != null; query += (line + "\n"));

            // replace parameters
            query = query.replace("INSERTCURID", Integer.toString(curID));
            query = query.replace("INSERTYEARSEMESTER", semester);

            // transform xquery result into Subjects
            XMLQueryTemplate<Subjects> xmlQuerySubjects = new XMLQueryTemplate<>(query, Subjects.class);
            Subjects subjects = xmlQuerySubjects.getResult();

            return subjects.getSubjects().stream().map(subject -> createSubject(subject.getSubjectName(), subject.getSubjectType())).collect(Collectors.toSet());


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return null;
    }

    private Resource createSubject(String subjectName, SubjectType subjectType) {
        Model model = this.ds.getDefaultModel();

        String identifier = subjectName.replaceAll(" ", "") + "_" + subjectType.toString();

        return model.createResource(LECTURE.NS + identifier,LECTURE.Course)
                .addProperty(LECTURE.name, subjectName)
                .addProperty(LECTURE.lvaType, subjectType.toString());
    }

    private Resource createCurriculum(int curID, String curName) {
        Model model = this.ds.getDefaultModel();

        return model.createResource(LECTURE.NS + curID, LECTURE.Curriculum)
                .addProperty(LECTURE.name, curName)
                .addProperty(LECTURE.curId, Integer.toString(curID));
    }


    private static String getSemesterString(int year, Semester semester) {
        String ret = "";

        ret += year;

        if (semester.equals(Semester.WS)) {ret += "W";} else {ret+="S";}

        return ret;
    }



    public static void main(String[] args) {

        FusekiServerDKEPR app = new FusekiServerDKEPR();
        app.importCurriculum(204, "Wirtschaftsinformatik Bachelor", 2018, Semester.SS);
        app.importCurriculum(205, "Wirtschaftsinformatik Master", 2018, Semester.SS);
        app.importCurriculum(217, "Recht und Wirtschaft für Techniker/innen", 2018, Semester.SS);
        //app.importCurriculum(219, "Recht und Wirtschaft für Techniker/innen", 2018, Semester.SS);

        //app.test();
        //app.test2();

    }
}
