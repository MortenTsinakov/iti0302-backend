package ee.taltech.iti0302_veebiarendus_backend.album.controller;

import ee.taltech.iti0302_veebiarendus_backend.album.dto.laterListenDto.LaterListenRequest;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.laterListenDto.LaterListenResponse;
import ee.taltech.iti0302_veebiarendus_backend.album.dto.laterListenDto.MyLaterListenDto;
import ee.taltech.iti0302_veebiarendus_backend.album.service.LaterListenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@CrossOrigin
@RestController
@RequestMapping("/api/later-listen")
public class LaterListenController {

    private final LaterListenService laterListenService;

    @PostMapping
    public ResponseEntity<LaterListenResponse> addLaterListen(HttpServletRequest request, @RequestBody LaterListenRequest laterListenRequest) {
        return laterListenService.addLaterListen(request, laterListenRequest);
    }

    @DeleteMapping
    public ResponseEntity<LaterListenResponse> removeLaterListen(HttpServletRequest request, @RequestBody LaterListenRequest removeLaterListenRequest) {
        return laterListenService.removeLaterListen(request, removeLaterListenRequest);
    }

    @GetMapping("/my-later-listens")
    public ResponseEntity<List<MyLaterListenDto>> getAllLaterListens(HttpServletRequest request) {
        return laterListenService.getAllLaterListens(request);
    }
}