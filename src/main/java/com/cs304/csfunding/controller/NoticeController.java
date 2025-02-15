package com.cs304.csfunding.controller;

import com.cs304.csfunding.api.NoticeDTO;
import com.cs304.csfunding.api.Result;
import com.cs304.csfunding.entity.Notice;
import com.cs304.csfunding.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class NoticeController {
    @Autowired
    private NoticeService noticeService;

    @PostMapping(value = "/register/notice")
    public String testAddNotice(@RequestBody NoticeDTO noticeDTO) {
        return noticeService.testInsert(noticeDTO);
    }

    @GetMapping("delete/notice")
    public Result testDeleteNotice(@RequestParam int uuid) {
        Notice notice = noticeService.testQueryByID(uuid);
        if (notice == null) {
            return new Result(404, "notice not found", null);
        } else {
            noticeService.testDeleteByID(uuid);
            return new Result(200,"success",null);
        }
    }

    @GetMapping("/getallnotices")
    public Result testGetAllNotice(){
        List<Notice> notices = noticeService.testQueryAll();
        if (notices == null) {
            return new Result(404, "notice not found", null);
        } else {
            return new Result(notices);
        }
    }

    @GetMapping("/get/notice")
    public Result testGetNoticeByID(@RequestParam int uuid){
        Notice notice = noticeService.testQueryByID(uuid);
        if (notice == null) {
            return new Result(404, "notice not found", null);
        } else {
            return new Result(200,"success",notice);
        }
    }
}
