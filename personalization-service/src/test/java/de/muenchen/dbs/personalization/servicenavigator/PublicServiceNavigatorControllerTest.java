package de.muenchen.dbs.personalization.servicenavigator;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.muenchen.dbs.personalization.checklist.domain.ChecklistItemServiceNavigatorDTO;
import de.muenchen.dbs.personalization.checklist.domain.ChecklistMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class PublicServiceNavigatorControllerTest {

    private static final String SN_SERVICE_ID = "10483346";

    private ChecklistMapper checklistMapper;
    private ServiceNavigatorService serviceNavigatorService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        checklistMapper = mock(ChecklistMapper.class);
        serviceNavigatorService = mock(ServiceNavigatorService.class);
        final PublicServiceNavigatorController controller = new PublicServiceNavigatorController(
                checklistMapper, serviceNavigatorService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void givenLifeSituationIdAndLanguage_thenReturnLifeSituation() throws Exception {
        final String serviceName = "I'm coming here from abroad.";
        final ServiceNavigatorResponse serviceNavigatorResponse = new ServiceNavigatorResponse(
                serviceName,
                "https://example.com",
                serviceName,
                "10483467",
                "en",
                true,
                null,
                null,
                null,
                List.of());
        when(serviceNavigatorService.getServiceNavigatorService(SN_SERVICE_ID, "en"))
                .thenReturn(Optional.of(serviceNavigatorResponse));

        final ChecklistItemServiceNavigatorDTO checklistItem = new ChecklistItemServiceNavigatorDTO();
        checklistItem.setTitle(serviceName);
        checklistItem.setServiceID("10483467");
        when(checklistMapper.toChecklistItemServiceNavigatorDTO(serviceNavigatorResponse)).thenReturn(checklistItem);

        mockMvc.perform(get("/public/servicenavigator")
                .queryParam("ids", SN_SERVICE_ID)
                .queryParam("lang", "en"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].title", is(serviceName)))
                .andExpect(jsonPath("$.[0].serviceID", is("10483467")));
    }

    @Test
    void givenUnsupportedLanguage_thenReturnBadRequest() throws Exception {
        mockMvc.perform(get("/public/servicenavigator")
                .queryParam("ids", SN_SERVICE_ID)
                .queryParam("lang", "unsupported"))
                .andExpect(status().isBadRequest());
    }
}
