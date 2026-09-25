package de.muenchen.dbs.ticketing.dprs.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/password")
public class PrsController {

    private final PrsService prsService;

    @PostMapping("/reset")
    Mono<ResponseEntity<String>> resetPassword(
            @RequestParam(value = "ticketnumber", required = false) String ticketnumber,
            @RequestParam(value = "resetKey", required = false) String resetKey) {
        if ((ticketnumber == null || ticketnumber.isBlank()) && (resetKey == null || resetKey.isBlank())) {
            return Mono.just(ResponseEntity.badRequest().body("{}"));
        }

        Mono<Boolean> sent = ticketnumber != null && !ticketnumber.isBlank()
                ? prsService.sendPasswordResetMailByTicketNumber(ticketnumber)
                : prsService.sendPasswordResetMailByResetKey(resetKey);
        return sent.onErrorMap(exception -> {
            log.warn("Password reset request failed", exception);
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password reset request failed");
        })
                .map(success -> Boolean.TRUE.equals(success)
                        ? ResponseEntity.ok("{}")
                        : ResponseEntity.badRequest().body("{}"));
    }

    @GetMapping("/{resetKey}")
    Mono<ResponseEntity<PasswordDTO>> getPassword(@PathVariable("resetKey") String resetKey) {
        return prsService.getPassword(resetKey)
                .map(optionalPasswordDTO -> {
                    return optionalPasswordDTO
                            .map(passwordDTO -> ResponseEntity.ok().body(passwordDTO))
                            .orElseGet(() -> ResponseEntity.notFound().build());
                })
                .onErrorResume(exception -> {
                    log.warn("Password retrieval failed", exception);
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

}
