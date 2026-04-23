package service.courseMaterial;

import dao.courseMaterial.CourseMaterialDAO;
import model.CourseMaterial;

import java.time.LocalDate;
import java.util.List;

public class CourseMaterialService {

    private final CourseMaterialDAO courseMaterialDAO;

    public CourseMaterialService(CourseMaterialDAO courseMaterialDAO) {
        this.courseMaterialDAO = courseMaterialDAO;
    }

    public CourseMaterial addCourseMaterial(String courseId, String lecturerId, String title, String filePath) {
        return addCourseMaterial(courseId, lecturerId, title, filePath, null);
    }

    public CourseMaterial addCourseMaterial(String courseId, String lecturerId, String title, String filePath, LocalDate deadline) {
        if (isBlank(courseId) || isBlank(lecturerId) || isBlank(title) || isBlank(filePath)) {
            return null;
        }

        CourseMaterial material = new CourseMaterial(
                courseId.trim(),
                lecturerId.trim(),
                title.trim(),
                filePath.trim(),
                deadline
        );
        return courseMaterialDAO.createCourseMaterial(material);
    }

    public List<CourseMaterial> getCourseMaterials(String courseId, String lecturerId) {
        if (isBlank(courseId) || isBlank(lecturerId)) {
            return List.of();
        }
        return courseMaterialDAO.getMaterialsByCourseAndLecturer(courseId.trim(), lecturerId.trim());
    }

    public boolean updateDeadline(int materialId, String lecturerId, String deadlineText) {
        if (materialId <= 0 || isBlank(lecturerId)) {
            return false;
        }

        LocalDate deadline = parseDate(deadlineText);
        if (deadlineText != null && !deadlineText.isBlank() && deadline == null) {
            return false;
        }

        return courseMaterialDAO.updateDeadline(materialId, lecturerId.trim(), deadline);
    }

    public boolean deleteCourseMaterial(int materialId, String lecturerId) {
        if (materialId <= 0 || isBlank(lecturerId)) {
            return false;
        }
        return courseMaterialDAO.deleteCourseMaterial(materialId, lecturerId.trim());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private LocalDate parseDate(String value) {
        if (isBlank(value)) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (Exception e) {
            return null;
        }
    }
}