package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.IssueReport;
import org.example.menaandfeena_finalproject.Model.Neighborhood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface IssueReportRepository extends JpaRepository<IssueReport, Integer> {

    IssueReport findIssueReportById(Integer id);

    List<IssueReport> findIssueReportsByStatus(String status);

    List<IssueReport> findIssueReportsByPriority(String priority);

    List<IssueReport> findIssueReportsByCategory(String category);

    List<IssueReport> findIssueReportsByReporterId(Integer reporterId);

    List<IssueReport> findIssueReportsByReportNeighborhoodId(Integer neighborhoodId);

    @Query("""
        select i from IssueReport i
        where lower(i.title) like lower(concat('%', :keyword, '%'))
        or lower(i.description) like lower(concat('%', :keyword, '%'))
        or lower(i.detectedDistrictName) like lower(concat('%', :keyword, '%'))
        or lower(i.detectedStreetName) like lower(concat('%', :keyword, '%'))
    """)
    List<IssueReport> searchIssueReports(@Param("keyword") String keyword);

    Integer countByReporterIdAndStatus(Integer id, String status);

    List<IssueReport> findByReporterId(Integer id);

    List<IssueReport> findTop3ByReportNeighborhoodOrderByCreatedAtDesc(Neighborhood neighborhood);

    Integer countByReportNeighborhoodAndStatus(Neighborhood neighborhood, String status);

    Integer countByReportNeighborhood(Neighborhood neighborhood);


    List<IssueReport> findByReporter_Neighborhood_Id(Integer neighborhoodId);

    Integer countByReporter_Neighborhood_Id(Integer neighborhoodId);

    List<IssueReport> findByReportNeighborhood_Id(Integer neighborhoodId);
}