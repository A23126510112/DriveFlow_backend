package org.example.placement_drive_management.repository;

import org.example.placement_drive_management.entity.Company;
import org.example.placement_drive_management.entity.Drive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DriveRepository extends JpaRepository<Drive,Long> {
    Optional<Drive> findByDriveId(String driveId);
    Boolean existsByDriveId(String driveId);
    Page<Drive> findByCompany_CompanyId(String companyId, Pageable pageable);
    List<Drive> findAllByIsActive(Boolean active);
    Page<Drive> findAllByIsActive(Boolean active, Pageable pageable);
    @Query("""
    SELECT COUNT(d) FROM Drive d
    WHERE d.company.companyId = :companyId
    AND d.isActive = true
""")
    Long countActiveDrivesByCompany(@Param("companyId") String companyId);

    @Query("""
    SELECT COUNT(d) FROM Drive d
    WHERE d.company.companyId = :companyId
    
""")
    Long countDrivesByCompany(@Param("companyId") String companyId);

    Optional<Drive> findByDriveIdAndCompanyCompanyIdAndIsActiveTrue(
            String driveId, String companyId);
}
