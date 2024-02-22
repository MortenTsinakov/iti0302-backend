package ee.taltech.iti0302_veebiarendus_backend.later_listen.controller;

import ee.taltech.iti0302_veebiarendus_backend.later_listen.dto.LaterListenRequest;
import ee.taltech.iti0302_veebiarendus_backend.later_listen.dto.LaterListenResponse;
import ee.taltech.iti0302_veebiarendus_backend.later_listen.dto.MyLaterListenDto;
import ee.taltech.iti0302_veebiarendus_backend.later_listen.service.LaterListenService;
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
    public ResponseEntity<LaterListenResponse> addLaterListen(@RequestBody LaterListenRequest laterListenRequest) {
        return laterListenService.addLaterListen(laterListenRequest);
    }

    @DeleteMapping
    public ResponseEntity<LaterListenResponse> removeLaterListen(@RequestBody LaterListenRequest removeLaterListenRequest) {
        return laterListenService.removeLaterListen(removeLaterListenRequest);
    }

    @GetMapping("/my-later-listens")
    public ResponseEntity<List<MyLaterListenDto>> getAllLaterListens() {
        return laterListenService.getAllLaterListens();
    }
}
