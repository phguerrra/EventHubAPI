package com.grupo1.backGrupo1.controller;

import com.grupo1.backGrupo1.dto.EventDTO;
import com.grupo1.backGrupo1.exception.BusinessRuleException;
import com.grupo1.backGrupo1.model.Event;
import com.grupo1.backGrupo1.service.EventsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

@RestController
@Tag(name="Events", description="Operações relacionadas a eventos")
@RequestMapping("/events")
public class EventsController {

    private final EventsService service;

    public EventsController(EventsService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todos os eventos")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Lista de eventos retornada com sucesso") })
    public List<Event> listAll() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter evento por ID")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Evento encontrado"), @ApiResponse(responseCode = "404", description = "Evento não encontrado") })
    public Event getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @Operation(summary = "Criar um novo evento", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Evento criado"), @ApiResponse(responseCode = "400", description = "Dados inválidos") })
    public Event create(@RequestBody @Valid EventDTO dto, HttpSession session) {
        validarAdmin(session);

        Event event = new Event();
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setDate(dto.getDate());
        event.setTime(dto.getTime());
        event.setLocation(dto.getLocation());
        event.setMaxParticipants(dto.getMaxParticipants());
        event.setMajority18(Boolean.TRUE.equals(dto.getMajority18()));

        return service.saveEvent(event);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar evento existente", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Evento atualizado"), @ApiResponse(responseCode = "404", description = "Evento não encontrado") })
    public Event update(@PathVariable Long id, @RequestBody @Valid EventDTO dto, HttpSession session) {
        validarAdmin(session);

        Event event = new Event();
        event.setId(id);
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setDate(dto.getDate());
        event.setTime(dto.getTime());
        event.setLocation(dto.getLocation());
        event.setMaxParticipants(dto.getMaxParticipants());
        event.setMajority18(Boolean.TRUE.equals(dto.getMajority18()));

        return service.saveEvent(event);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover evento (soft delete)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({ @ApiResponse(responseCode = "204", description = "Evento removido"), @ApiResponse(responseCode = "404", description = "Evento não encontrado") })
    public void delete(@PathVariable Long id, HttpSession session) {
        validarAdmin(session);
        service.deleteById(id);
    }

    private void validarAdmin(HttpSession session) {
        Object userId = session.getAttribute("userId");
        Object userRole = session.getAttribute("userRole");

        if (userId == null) {
            throw new BusinessRuleException("Usuário não está logado");
        }

        if (!"ADMIN".equals(userRole)) {
            throw new BusinessRuleException("Apenas administradores podem realizar esta ação");
        }
    }
}
