package service.notice;

import dao.notice.NoticeDAO;
import dto.requestDto.notice.AddNoticeReqDTO;
import model.Notice;

public class AddNoticeService {
    private final NoticeDAO noticeDAO;

    public AddNoticeService(NoticeDAO noticeDAO) {
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
}