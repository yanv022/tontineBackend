package com.auth.controllers;

import com.auth.models.TontineGroup;
import com.auth.models.User;
import com.auth.payload.request.TontineGroupRequest;
import com.auth.payload.response.MessageResponse;
import com.auth.repository.TontineGroupRepository;
import com.auth.repository.UserRepository;
import com.auth.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tontine")
public class TontineController {

    @Autowired
    private TontineGroupRepository tontineGroupRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/groups")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> createGroup(@Valid @RequestBody TontineGroupRequest groupRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User creator = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        TontineGroup group = new TontineGroup();
        group.setName(groupRequest.getName());
        group.setAmount(groupRequest.getAmount());
        group.setCurrency(groupRequest.getCurrency());
        group.setFrequency(groupRequest.getFrequency());
        group.setCreator(creator);

        tontineGroupRepository.save(group);

        return ResponseEntity.ok(new MessageResponse("Tontine group created successfully!"));
    }

    @GetMapping("/groups")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getGroups() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        List<TontineGroup> groups;
        if (authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ADMIN"))) {
            groups = tontineGroupRepository.findAll();
        } else {
            groups = tontineGroupRepository.findByCreator(user);
        }

        return ResponseEntity.ok(groups);
    }

    @GetMapping("/groups/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getGroup(@PathVariable Long id) {
        TontineGroup group = tontineGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Tontine group not found."));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        if (!authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ADMIN")) &&
                !group.getCreator().getId().equals(userDetails.getId())) {
            return ResponseEntity.status(403)
                    .body(new MessageResponse("Error: You don't have permission to view this group."));
        }

        return ResponseEntity.ok(group);
    }

    @PutMapping("/groups/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateGroup(@PathVariable Long id,
                                         @Valid @RequestBody TontineGroupRequest groupRequest) {
        TontineGroup group = tontineGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Tontine group not found."));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        if (!authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ADMIN")) &&
                !group.getCreator().getId().equals(userDetails.getId())) {
            return ResponseEntity.status(403)
                    .body(new MessageResponse("Error: You don't have permission to update this group."));
        }

        group.setName(groupRequest.getName());
        group.setAmount(groupRequest.getAmount());
        group.setCurrency(groupRequest.getCurrency());
        group.setFrequency(groupRequest.getFrequency());

        tontineGroupRepository.save(group);

        return ResponseEntity.ok(new MessageResponse("Tontine group updated successfully!"));
    }

    @DeleteMapping("/groups/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteGroup(@PathVariable Long id) {
        TontineGroup group = tontineGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Tontine group not found."));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        if (!authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ADMIN")) &&
                !group.getCreator().getId().equals(userDetails.getId())) {
            return ResponseEntity.status(403)
                    .body(new MessageResponse("Error: You don't have permission to delete this group."));
        }

        tontineGroupRepository.delete(group);

        return ResponseEntity.ok(new MessageResponse("Tontine group deleted successfully!"));
    }
}