package service.courseMeterial;

import dao.lecturerMeterial.CourseMaterialDAO;
import dto.requestDto.courseMeterial.AddCourseMaterialReqDTO;
import model.CourseMaterial;

import java.util.List;

public class CourseMaterialService {

    private final CourseMaterialDAO courseMaterialDAO;
    public CourseMaterialService(CourseMaterialDAO courseMaterialDAO){
        this.courseMaterialDAO = courseMaterialDAO;
    }

    public CourseMaterial addMaterial(AddCourseMaterialReqDTO dto, String lecturerId) {
        CourseMaterial material = new CourseMaterial();

        material.setCourseId(dto.getCourseId());
        material.setLecturerId(lecturerId);
        material.setTitle(dto.getTitle());
        material.setFilePath(dto.getFilePath());

        return courseMaterialDAO.addMaterial(material);
    }

    public List<CourseMaterial> getMaterialsByCourse(String courseId, String lecturerId) {
        return courseMaterialDAO.getMaterialsByCourse(courseId, lecturerId);
    }
}