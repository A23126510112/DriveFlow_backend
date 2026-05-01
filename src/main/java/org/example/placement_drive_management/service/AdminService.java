package org.example.placement_drive_management.service;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.example.placement_drive_management.dto.*;
import org.example.placement_drive_management.dto.auth.ApiResponse;
import org.example.placement_drive_management.entity.Company;
import org.example.placement_drive_management.entity.Drive;
import org.example.placement_drive_management.entity.Student;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Service
public interface AdminService {
    Page<StudentProfileDto> getAllProfiles(int page, int size);

    StudentProfileDto getStudentProfileByRollNo(String rollNo);
    String createDrive(DriveDto driveDto);
    String createEligibility(EligibilityDto eligibilityDto);
    String updateEligibility(String driveId,EligibilityDto eligibilityDto);

    Page<StudentResponseDto> getAllStudents(int page, int size);

    String publishDrivesToEligibleStudents(String driveId);

    Page<DriveDto> getAllDrives(String companyId, int page, int size);


    Page<CompanyDto> getAllCompanies(int page, int size);

    Page<ApplicationsDto> getAllApplicationsForaStudent(String rollNo, int page, int size);

    String closeDrive(String driveId);


    Page<DriveRoundDto> getAllRounds(String driveId, int page, int size);

    Page<ApplicationsDto> getAllApplications(String driveId, int page, int size);


    Page<ApplicationRoundProjection> getApplicantsForDriveRound(String driveId, Integer roundNo, int page, int size);


    Page<DriveDto> viewAllActiveDrives(int page, int size);

    String removeDrive(String driveId);
    String extendDriveApplication(String driveId, LocalDate localDate);
    String deleteDrive(String driveId);
    String deleteAdmin(Long id);
    List<AdminDto> getAllAdmins();

    Long countStudents();
    Long countActiveDrives();
   Long countCompanies();
   Long countAdmins();
    // add to interface:
    ResponseEntity<byte[]> streamStudentResume(String rollNo);

    boolean isEligibilityPublished(String driveId);
}
