package org.example.placement_drive_management.service;

import lombok.AllArgsConstructor;
import org.example.placement_drive_management.dto.ApplicationRoundDto;
import org.example.placement_drive_management.dto.ApplicationsDto;
import org.example.placement_drive_management.dto.DriveRoundDto;
import org.example.placement_drive_management.dto.StudentProfileDto;
import org.example.placement_drive_management.entity.Applications;
import org.example.placement_drive_management.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface StudentProfileService {
    String createStudentProfile(StudentProfileDto studentProfileDto,String rollNumberInContext);
    String updateStudentProfile(StudentProfileDto studentProfileDto,String rollNumberInContext);
    StudentProfileDto getStudentProfile(String rollNumberInContext);

    Page<ApplicationsDto> getAllApplicationsForStudent(String studentRollNo, int page, int size);

    Page<ApplicationRoundDto> getAllApplicationRoundsForStudentAndDriveId(String driveId, String rollNumberInContext, int page, int size);
    String applyDrive(String driveId, String rollNoInContext);

    Page<ApplicationsDto> getAllEligibleApplications(String rollNo, int page, int size);

    String uploadResume(MultipartFile file, String rollNo);
    ResponseEntity<byte[]> streamResume(String email);
    Long getEligibleDrivesCount(String rollNo);

    Long getInProcessDrivesCount(String rollNo);

    Long getSelectedDrivesCount(String rollNo);

    Long getAppliedDrivesCount(String rollNo);
}
