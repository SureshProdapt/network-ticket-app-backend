package com.networkticket.ticket.service;

import com.networkticket.ticket.model.Ticket;
import com.networkticket.ticket.repository.TicketRepository;
import com.networkticket.user.model.User;
import com.networkticket.user.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {
    
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    
    public TicketService(TicketRepository ticketRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }
    
    public Ticket createTicket(Ticket ticket) {
        // Get authenticated user email from security context
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Load user entity
        User createdByUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Set createdBy and createdAt
        ticket.setCreatedBy(createdByUser);
        ticket.setCreatedAt(Instant.now());
        
        // Set customerId from the authenticated user's userId (prevent spoofing)
        ticket.setCustomerId(createdByUser.getUserId());
        
        return ticketRepository.save(ticket);
    }
    
    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }
    
    public List<Ticket> findByCustomerId(Long customerId) {
        return ticketRepository.findByCustomerId(customerId);
    }
    
    public Ticket updateTicket(Long id, Ticket patch) {
        Ticket existing = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        // Update allowed fields
        if (patch.getStatus() != null) {
            existing.setStatus(patch.getStatus());
        }
        if (patch.getPriority() != null) {
            existing.setPriority(patch.getPriority());
        }
        if (patch.getSlaStartAt() != null) {
            existing.setSlaStartAt(patch.getSlaStartAt());
        }
        if (patch.getSlaDueAt() != null) {
            existing.setSlaDueAt(patch.getSlaDueAt());
        }
        if (patch.getSlaBreached() != null) {
            existing.setSlaBreached(patch.getSlaBreached());
        }
        
        return ticketRepository.save(existing);
    }
}