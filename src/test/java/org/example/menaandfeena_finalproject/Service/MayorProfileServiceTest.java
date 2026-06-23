package org.example.menaandfeena_finalproject.Service;

import org.example.menaandfeena_finalproject.DTO.In.MayorProfileInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.MayorAnalyticsDTO;
import org.example.menaandfeena_finalproject.DTO.Out.MayorReportsDTO;
import org.example.menaandfeena_finalproject.Model.*;
import org.example.menaandfeena_finalproject.Repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MayorProfileServiceTest {

    @InjectMocks
    MayorProfileService mayorProfileService;

    @Mock
    MayorProfileRepository mayorProfileRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    NeighborhoodRepository neighborhoodRepository;
    @Mock
    IssueReportRepository issueReportRepository;
    @Mock
    EventRepository eventRepository;
    @Mock
    InitiativeRepository initiativeRepository;
    @Mock
    ReviewRepository reviewRepository;
    @Mock
    OpenAIService openAIService;
    @Mock
    PdfService pdfService;
    @Mock
    EmailService emailService;
    @Mock
    WhatsAppService whatsAppService;

    User mayor;
    Neighborhood neighborhood;
    MayorProfile mayorProfile;
    MayorProfileInDTO dto;
    IssueReport report1, report2;
    List<IssueReport> reports;

    @BeforeEach
    void setUp() {
        neighborhood = new Neighborhood();
        neighborhood.setId(1);
        neighborhood.setName("النرجس");
        neighborhood.setCity("الرياض");

        mayor = new User();
        mayor.setId(1);
        mayor.setFullName("Reenad");
        mayor.setEmail("mayor@test.com");
        mayor.setStatus("MAYOR");
        mayor.setNeighborhood(neighborhood);

        mayorProfile = new MayorProfile();
        mayorProfile.setId(1);
        mayorProfile.setUser(mayor);
        mayorProfile.setNeighborhood(neighborhood);
        mayorProfile.setStatus("ACTIVE");
        mayorProfile.setStartDate(LocalDate.now());
        mayorProfile.setEndDate(LocalDate.now().plusYears(1));

        dto = new MayorProfileInDTO();
        dto.setUserId(mayor.getId());
        dto.setNeighborhoodId(neighborhood.getId());

        report1 = new IssueReport();
        report1.setId(1);
        report1.setTitle("إنارة معطلة");
        report1.setCategory("LIGHTING");
        report1.setPriority("URGENT");
        report1.setStatus("IN_PROGRESS");

        report2 = new IssueReport();
        report2.setId(2);
        report2.setTitle("نظافة شارع");
        report2.setCategory("CLEANLINESS");
        report2.setPriority("NON_URGENT");
        report2.setStatus("COMPLETED");

        reports = new ArrayList<>();
        reports.add(report1);
        reports.add(report2);
    }

    @Test
    public void getAllMayorProfilesTest() {
        when(mayorProfileRepository.findAll()).thenReturn(List.of(mayorProfile));

        List<MayorProfile> result = mayorProfileService.getAllMayorProfiles();

        Assertions.assertEquals(1, result.size());

        verify(mayorProfileRepository, times(1)).findAll();
    }

    @Test
    public void addMayorProfileTest() {
        when(userRepository.findUserById(dto.getUserId())).thenReturn(mayor);
        when(neighborhoodRepository.findNeighborhoodById(dto.getNeighborhoodId())).thenReturn(neighborhood);
        when(mayorProfileRepository.findTopByNeighborhoodIdAndStatusOrderByStartDateDesc(neighborhood.getId(), "ACTIVE")).thenReturn(null);

        mayorProfileService.addMayorProfile(dto);

        Assertions.assertEquals("MAYOR", mayor.getStatus());
        Assertions.assertTrue(mayor.getMayorActive());

        verify(userRepository, times(1)).findUserById(dto.getUserId());
        verify(neighborhoodRepository, times(1)).findNeighborhoodById(dto.getNeighborhoodId());
        verify(mayorProfileRepository, times(1)).save(any(MayorProfile.class));
    }

    @Test
    public void updateMayorProfileTest() {
        when(mayorProfileRepository.findMayorProfileById(mayorProfile.getId())).thenReturn(mayorProfile);
        when(userRepository.findUserById(dto.getUserId())).thenReturn(mayor);
        when(neighborhoodRepository.findNeighborhoodById(dto.getNeighborhoodId())).thenReturn(neighborhood);

        mayorProfileService.updateMayorProfile(mayorProfile.getId(), dto);

        verify(mayorProfileRepository, times(1)).findMayorProfileById(mayorProfile.getId());
        verify(userRepository, times(1)).findUserById(dto.getUserId());
        verify(neighborhoodRepository, times(1)).findNeighborhoodById(dto.getNeighborhoodId());
        verify(mayorProfileRepository, times(1)).save(mayorProfile);
    }

    @Test
    public void deleteMayorProfileTest() {
        when(mayorProfileRepository.findMayorProfileById(mayorProfile.getId())).thenReturn(mayorProfile);

        mayorProfileService.deleteMayorProfile(mayorProfile.getId());

        Assertions.assertEquals("RESIDENT", mayor.getStatus());
        Assertions.assertFalse(mayor.getMayorActive());

        verify(mayorProfileRepository, times(1)).findMayorProfileById(mayorProfile.getId());
        verify(mayorProfileRepository, times(1)).delete(mayorProfile);
    }

    @Test
    public void getMayorAnalyticsTest() {
        when(userRepository.findUserById(mayor.getId())).thenReturn(mayor);
        when(mayorProfileRepository.findTopByUserIdOrderByStartDateDesc(mayor.getId())).thenReturn(mayorProfile);

        MayorAnalyticsDTO result = mayorProfileService.getMayorAnalytics(mayor.getId());

        Assertions.assertEquals(3, result.getTotalReports());
        Assertions.assertEquals("Reenad", result.getBasicInfo().getFullName());

        verify(userRepository, times(1)).findUserById(mayor.getId());
        verify(mayorProfileRepository, times(1)).findTopByUserIdOrderByStartDateDesc(mayor.getId());
    }

    @Test
    public void getMayorReportsTest() {
        when(userRepository.findUserById(mayor.getId())).thenReturn(mayor);
        when(issueReportRepository.findByReporter_Neighborhood_Id(neighborhood.getId())).thenReturn(reports);

        MayorReportsDTO result = mayorProfileService.getMayorReports(mayor.getId());

        Assertions.assertEquals(1, result.getUrgentReports().size());
        Assertions.assertEquals(1, result.getNonUrgentReports().size());

        verify(userRepository, times(1)).findUserById(mayor.getId());
        verify(issueReportRepository, times(1)).findByReporter_Neighborhood_Id(neighborhood.getId());
    }

    @Test
    public void sendWeeklyReportTest() {
        File file = new File("weekly.pdf");

        when(userRepository.findUserById(mayor.getId())).thenReturn(mayor);
        when(issueReportRepository.findByReportNeighborhood_Id(neighborhood.getId())).thenReturn(reports);
        when(openAIService.askAI(anyString(), anyString())).thenReturn("AI weekly report");
        when(pdfService.createPdf(anyString(), anyString())).thenReturn(file);

        mayorProfileService.sendWeeklyReport(mayor.getId());

        verify(userRepository, times(1)).findUserById(mayor.getId());
        verify(issueReportRepository, times(1)).findByReportNeighborhood_Id(neighborhood.getId());
        verify(openAIService, times(1)).askAI(anyString(), anyString());
        verify(pdfService, times(1)).createPdf(anyString(), anyString());
        verify(emailService, times(1)).sendEmailWithAttachments(mayor.getEmail(), "التقرير الأسبوعي للحي", "", file);
    }

    @Test
    public void sendPerformanceReportTest() {
        File file = new File("performance.pdf");

        when(userRepository.findUserById(mayor.getId())).thenReturn(mayor);
        when(issueReportRepository.findByReportNeighborhood_Id(neighborhood.getId())).thenReturn(reports);
        when(eventRepository.findByNeighborhood_Id(neighborhood.getId())).thenReturn(List.of(new Event()));
        when(initiativeRepository.findByNeighborhood_Id(neighborhood.getId())).thenReturn(List.of(new Initiative()));
        when(openAIService.askAI(anyString(), anyString())).thenReturn("AI performance report");
        when(pdfService.createPdf(anyString(), anyString())).thenReturn(file);

        mayorProfileService.sendPerformanceReport(mayor.getId());

        verify(userRepository, times(1)).findUserById(mayor.getId());
        verify(issueReportRepository, times(1)).findByReportNeighborhood_Id(neighborhood.getId());
        verify(eventRepository, times(1)).findByNeighborhood_Id(neighborhood.getId());
        verify(initiativeRepository, times(1)).findByNeighborhood_Id(neighborhood.getId());
        verify(openAIService, times(1)).askAI(anyString(), anyString());
        verify(pdfService, times(1)).createPdf(anyString(), anyString());
        verify(emailService, times(1)).sendEmailWithAttachments(mayor.getEmail(), "تقرير أداء الحي", "", file);
    }

    @Test
    public void sendSatisfactionReportTest() {
        File file = new File("satisfaction.pdf");

        Review review = new Review();
        review.setRating(5);

        when(userRepository.findUserById(mayor.getId())).thenReturn(mayor);
        when(reviewRepository.findByUser_Neighborhood_Id(neighborhood.getId())).thenReturn(List.of(review));
        when(issueReportRepository.findByReportNeighborhood_Id(neighborhood.getId())).thenReturn(reports);
        when(openAIService.askAI(anyString(), anyString())).thenReturn("AI satisfaction report");
        when(pdfService.createPdf(anyString(), anyString())).thenReturn(file);

        mayorProfileService.sendSatisfactionReport(mayor.getId());

        verify(userRepository, times(1)).findUserById(mayor.getId());
        verify(reviewRepository, times(1)).findByUser_Neighborhood_Id(neighborhood.getId());
        verify(issueReportRepository, times(1)).findByReportNeighborhood_Id(neighborhood.getId());
        verify(openAIService, times(1)).askAI(anyString(), anyString());
        verify(pdfService, times(1)).createPdf(anyString(), anyString());
        verify(emailService, times(1)).sendEmailWithAttachments(mayor.getEmail(), "تقرير رضا السكان", "", file);
    }

    @Test
    public void getInitiativeSuggestionsTest() {
        when(userRepository.findUserById(mayor.getId())).thenReturn(mayor);
        when(issueReportRepository.findByReportNeighborhood_Id(neighborhood.getId())).thenReturn(reports);
        when(reviewRepository.findByUser_Neighborhood_Id(neighborhood.getId())).thenReturn(List.of());
        when(eventRepository.findByNeighborhood_Id(neighborhood.getId())).thenReturn(List.of());
        when(initiativeRepository.findByNeighborhood_Id(neighborhood.getId())).thenReturn(List.of());
        when(openAIService.askAI(anyString(), anyString())).thenReturn("Suggested initiatives");

        String result = mayorProfileService.getInitiativeSuggestions(mayor.getId());

        Assertions.assertEquals("Suggested initiatives", result);

        verify(openAIService, times(1)).askAI(anyString(), anyString());
    }

    @Test
    public void resendMayorAppointmentEmailTest() {
        when(userRepository.findUserById(mayor.getId())).thenReturn(mayor);
        when(mayorProfileRepository.findTopByUserIdAndStatusOrderByStartDateDesc(mayor.getId(), "ACTIVE")).thenReturn(mayorProfile);

        mayorProfileService.resendMayorAppointmentEmail(mayor.getId());

        verify(userRepository, times(1)).findUserById(mayor.getId());
        verify(mayorProfileRepository, times(1)).findTopByUserIdAndStatusOrderByStartDateDesc(mayor.getId(), "ACTIVE");
        verify(emailService, times(1)).sendMayorAppointmentEmail(mayor, mayorProfile, 0);
    }
}