package com.samadhan.identity.controller;

import com.samadhan.identity.dto.IdentifyRequest;
import com.samadhan.identity.dto.IdentifyResponse;
import com.samadhan.identity.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping("/identify")
    public ResponseEntity<IdentifyResponse> identify(@RequestBody IdentifyRequest request) {
        IdentifyResponse response = contactService.identify(request);
        return ResponseEntity.ok(response);
    }
}