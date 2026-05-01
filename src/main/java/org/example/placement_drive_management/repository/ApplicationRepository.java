package org.example.placement_drive_management.repository;

import jakarta.transaction.Transactional;
import org.example.placement_drive_management.entity.Applications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Applications, Long> {
    Page<Applications> findByDrive_DriveId(String driveId, Pageable pageable);

    Page<Applications> findByStudent_RollNo(String rollNo, Pageable pageable);

    Optional<Applications> findByDrive_DriveIdAndStudent_RollNo(
            String driveId,
            String rollNo
    );

    Page<Applications> findByDrive_DriveIdAndStatus(
            String driveId,
            String status,
            Pageable pageable);
    @Query("""
    SELECT a FROM Applications a
    WHERE a.studentProfile.student.rollNo = :rollNo
    AND a.status IN ('APPLIED', 'INPROCESS')
""")
    Page<Applications> findActiveApplications(
            @Param("rollNo") String rollNo,
            Pageable pageable
    );

    @Modifying
    @Transactional
    void deleteByDrive_DriveId(String driveId);
    boolean existsByStudent_RollNoAndDrive_Company_CompanyId(
            String rollNo, String companyId);

    @EntityGraph(attributePaths = {"drive", "drive.company"})
    Page<Applications> findByStudentRollNoAndStatus(
            String rollNo,
            String status,
            Pageable pageable
    );

@EntityGraph(attributePaths = {"drive", "drive.company"})
List<Applications> findByStudentRollNoAndStatus(
        String rollNo,
        String status
);}