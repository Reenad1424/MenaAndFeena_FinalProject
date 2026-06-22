package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.IssueReport;
import org.example.menaandfeena_finalproject.Model.Neighborhood;
import org.example.menaandfeena_finalproject.Model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class IssueReportRepositoryTest {

    @Autowired
    private IssueReportRepository issueReportRepository;

    @Autowired
    private NeighborhoodRepository neighborhoodRepository;

    @Autowired
    private UserRepository userRepository;

    private IssueReport issueReport;

    @BeforeEach
    void setUp() {
        Neighborhood neighborhood = neighborhoodRepository.save(neighborhood());
        User reporter = userRepository.save(user(neighborhood));
        issueReport = issueReportRepository.save(issueReport(reporter, neighborhood));
    }

    @Test
    void findIssueReportById_returnsReport() {
        IssueReport result = issueReportRepository.findIssueReportById(issueReport.getId());

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Broken walkway light");
    }

    private Neighborhood neighborhood() {
        Neighborhood neighborhood = new Neighborhood();
        neighborhood.setName("Al Narjis");
        neighborhood.setCity("Riyadh");
        neighborhood.setLatitude(24.8391);
        neighborhood.setLongitude(46.7244);
        return neighborhood;
    }

    private User user(Neighborhood neighborhood) {
        User user = new User();
        user.setFullName("Noura Al Harbi");
        user.setEmail("noura.repo@example.com");
        user.setPassword("Password123");
        user.setPhone("+966555555555");
        user.setNationalId("1000000001");
        user.setBirthDate(LocalDate.of(1995, 1, 1));
        user.setGender("FEMALE");
        user.setNeighborhood(neighborhood);
        user.setCreatedAt(LocalDate.of(2025, 1, 1));
        return user;
    }

    private IssueReport issueReport(User reporter, Neighborhood neighborhood) {
        IssueReport issueReport = new IssueReport();
        issueReport.setTitle("Broken walkway light");
        issueReport.setDescription("The walkway light beside the mosque is not working");
        issueReport.setCategory("LIGHTING");
        issueReport.setPriority("URGENT");
        issueReport.setStatus("OPEN");
        issueReport.setLatitude(24.8391);
        issueReport.setLongitude(46.7244);
        issueReport.setReportedStreetName("Abi Bakr Street");
        issueReport.setDetectedDistrictName("Al Narjis");
        issueReport.setDetectedStreetName("Abi Bakr Street");
        issueReport.setReporter(reporter);
        issueReport.setReportNeighborhood(neighborhood);
        return issueReport;
    }
}
