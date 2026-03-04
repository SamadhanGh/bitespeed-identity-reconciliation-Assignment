package com.samadhan.identity.service;

import com.samadhan.identity.dto.IdentifyRequest;
import com.samadhan.identity.dto.IdentifyResponse;
import com.samadhan.identity.model.Contact;
import com.samadhan.identity.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

    @Transactional
    public IdentifyResponse identify(IdentifyRequest request) {
        String email = request.getEmail();
        String phoneNumber = request.getPhoneNumber();

        // Step 1: Find all matching contacts by email or phone
        List<Contact> matchingContacts = contactRepository
                .findByEmailOrPhoneNumber(email, phoneNumber);

        // Step 2: No match found — create new primary contact
        if (matchingContacts.isEmpty()) {
            Contact newContact = Contact.builder()
                    .email(email)
                    .phoneNumber(phoneNumber)
                    .linkPrecedence(Contact.LinkPrecedence.primary)
                    .build();
            contactRepository.save(newContact);
            return buildResponse(newContact, new ArrayList<>());
        }

        // Step 3: Find all primary contacts from matches
        Set<Integer> primaryIds = new HashSet<>();
        for (Contact c : matchingContacts) {
            if (c.getLinkPrecedence() == Contact.LinkPrecedence.primary) {
                primaryIds.add(c.getId());
            } else {
                primaryIds.add(c.getLinkedId());
            }
        }

        // Step 4: Load all primaries and pick oldest as true primary
        List<Contact> primaries = primaryIds.stream()
                .map(id -> contactRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Contact::getCreatedAt))
                .collect(Collectors.toList());

        Contact truePrimary = primaries.get(0);

        // Step 5: Demote other primaries to secondary
        for (int i = 1; i < primaries.size(); i++) {
            Contact demoted = primaries.get(i);
            demoted.setLinkPrecedence(Contact.LinkPrecedence.secondary);
            demoted.setLinkedId(truePrimary.getId());
            contactRepository.save(demoted);

            // Re-link their secondaries to true primary
            List<Contact> theirSecondaries = contactRepository
                    .findByLinkedId(demoted.getId());
            for (Contact sec : theirSecondaries) {
                sec.setLinkedId(truePrimary.getId());
                contactRepository.save(sec);
            }
        }

        // Step 6: Get all secondaries of true primary
        List<Contact> allSecondaries = contactRepository
                .findByLinkedId(truePrimary.getId());

        // Step 7: Check if incoming request has new info — create secondary if so
        boolean emailExists = email == null ||
                truePrimary.getEmail() != null && truePrimary.getEmail().equals(email) ||
                allSecondaries.stream().anyMatch(c -> email.equals(c.getEmail()));

        boolean phoneExists = phoneNumber == null ||
                truePrimary.getPhoneNumber() != null && truePrimary.getPhoneNumber().equals(phoneNumber) ||
                allSecondaries.stream().anyMatch(c -> phoneNumber.equals(c.getPhoneNumber()));

        if (!emailExists || !phoneExists) {
            Contact newSecondary = Contact.builder()
                    .email(email)
                    .phoneNumber(phoneNumber)
                    .linkedId(truePrimary.getId())
                    .linkPrecedence(Contact.LinkPrecedence.secondary)
                    .build();
            contactRepository.save(newSecondary);
            allSecondaries.add(newSecondary);
        }

        return buildResponse(truePrimary, allSecondaries);
    }

    private IdentifyResponse buildResponse(Contact primary, List<Contact> secondaries) {
        // Emails — primary first, then secondaries (no duplicates)
        List<String> emails = new ArrayList<>();
        if (primary.getEmail() != null) emails.add(primary.getEmail());
        secondaries.stream()
                .map(Contact::getEmail)
                .filter(e -> e != null && !emails.contains(e))
                .forEach(emails::add);

        // Phone numbers — primary first, then secondaries (no duplicates)
        List<String> phones = new ArrayList<>();
        if (primary.getPhoneNumber() != null) phones.add(primary.getPhoneNumber());
        secondaries.stream()
                .map(Contact::getPhoneNumber)
                .filter(p -> p != null && !phones.contains(p))
                .forEach(phones::add);

        // Secondary IDs
        List<Integer> secondaryIds = secondaries.stream()
                .map(Contact::getId)
                .collect(Collectors.toList());

        return IdentifyResponse.builder()
                .contact(IdentifyResponse.ContactPayload.builder()
                        .primaryContatctId(primary.getId())
                        .emails(emails)
                        .phoneNumbers(phones)
                        .secondaryContactIds(secondaryIds)
                        .build())
                .build();
    }
}