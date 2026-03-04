package com.samadhan.identity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentifyResponse {

    private ContactPayload contact;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactPayload {
        private Integer primaryContatctId;
        private List<String> emails;
        private List<String> phoneNumbers;
        private List<Integer> secondaryContactIds;
    }
}