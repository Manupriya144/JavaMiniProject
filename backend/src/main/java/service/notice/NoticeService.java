package service.notice;

import dao.notice.NoticeDAO;
import dto.NoticeResponseDTO;
import dto.requestDto.notice.AddNoticeReqDTO;
import model.Notice;

import java.util.ArrayList;
import java.util.List;

public class NoticeService {
    private final NoticeDAO noticeDAO;

    public NoticeService(NoticeDAO noticeDAO) {
        this.noticeDAO = noticeDAO;
    }

    public Notice addNotice(AddNoticeReqDTO addNoticeReqDTO) {
        Notice notice = new Notice(
                addNoticeReqDTO.getTitle(),
                addNoticeReqDTO.getDescription(),
                addNoticeReqDTO.getCreated_by(),
                addNoticeReqDTO.getPdf_file_path()
        );

        return noticeDAO.createNotice(notice);
    }
    public List<NoticeResponseDTO> getAllNotices() {
        List<Notice> notices = noticeDAO.getAllNotices();
        List<NoticeResponseDTO> responseList = new ArrayList<>();

        for (Notice notice : notices) {
            responseList.add(new NoticeResponseDTO(
                    notice.getNotice_id(),
                    notice.getTitle(),
                    notice.getDescription(),
                    notice.getPdf_file_path(),
                    notice.getCreated_by(),
                    notice.getCreated_at() != null ? notice.getCreated_at().toString() : ""
            ));
        }

        return responseList;
    }
}