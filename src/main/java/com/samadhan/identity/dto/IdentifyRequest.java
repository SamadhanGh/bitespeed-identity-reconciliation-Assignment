package com.samadhan.identity.dto;

import lombok.Data;

@Data
public class IdentifyRequest {
    private String email;
    private String phoneNumber;
}