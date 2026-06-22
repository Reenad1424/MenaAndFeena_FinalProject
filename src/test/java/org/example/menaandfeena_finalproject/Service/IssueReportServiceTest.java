package org.example.menaandfeena_finalproject.Service;

import org.example.menaandfeena_finalproject.DTO.In.IssueReportInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.IssueReportOutDTO;
import org.example.menaandfeena_finalproject.Model.IssueReport;
import org.example.menaandfeena_finalproject.Model.Neighborhood;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.IssueReportRepository;
import org.example.menaandfeena_finalproject.Repository.IssueReportTimelineRepository;
import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IssueReportServiceTest {

    @Mock
    private IssueReportRepository issueReportRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OpenAIService openAIService;
    @Mock
    private NominatimService nominatimService;
    @Mock
    private IssueReportTimelineRepository issueReportTimelineRepository;

    private IssueReportService issueReportService;

    private Neighborhood neighborhood;
    private User reporter;

    @BeforeEach
    void setUp() {
        neighborhood = new Neighborhood();
        neighborhood.setId(1);
        neighborhood.setName("Al Narjis");
        neighborhood.setCity("Riyadh");

        reporter = new User();
        reporter.setId(10);
        reporter.setFullName("Sara Alotaibi");
        reporter.setEmail("sara.issue@example.com");
        reporter.setPassword("Password123");
        reporter.setPhone("+966555555555");
        reporter.setNationalId("1000000001");
        reporter.setBirthDate(LocalDate.of(1995, 1, 1));
        reporter.setGender("FEMALE");
        reporter.setNeighborhood(neighborhood);
        reporter.setCreatedAt(LocalDate.of(2025, 1, 1));

        issueReportService = new IssueReportService(
                issueReportRepository,
                userRepository,
                null,
                null,
                openAIService,
                nominatimService,
                null,
                issueReportTimelineRepository
        );
    }

    @Test
    void createIssueReport_savesClassifiedReport() {
        IssueReportInDTO dto = new IssueReportInDTO("Street light is off", "King Salman Road", 24.79, 46.68);
        when(userRepository.findUserById(reporter.getId())).thenReturn(reporter);
        when(nominatimService.detectLocationFromCoordinates(24.79, 46.68))
                .thenReturn(Map.of("district", "Al Narjis", "street", "King Salman Road"));
        when(openAIService.askAI(anyString(), anyString())).thenReturn("Street light outage|LIGHTING|URGENT");
        when(issueReportRepository.save(any(IssueReport.class))).thenAnswer(invocation -> {
            IssueReport saved = invocation.getArgument(0);
            saved.setId(100);
            saved.setCreatedAt(LocalDateTime.now());
            return saved;
        });

        IssueReportOutDTO result = issueReportService.createIssueReport(reporter.getId(), dto);

        assertThat(result.getId()).isEqualTo(100);
        assertThat(result.getCategory()).isEqualTo("LIGHTING");
        assertThat(result.getPriority()).isEqualTo("URGENT");
        verify(userRepository).findUserById(reporter.getId());
        verify(issueReportRepository).save(any(IssueReport.class));
    }
}
