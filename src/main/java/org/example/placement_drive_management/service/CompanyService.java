package org.example.placement_drive_management.service;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.example.placement_drive_management.dto.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CompanyService {

    String publishDriveRound(String driveId, DriveRoundDto driveRoundDto, String companyId);


    Page<DriveDto> getAllDrives(String companyId, int page, int size);

    Page<DriveRoundDto> getAllRounds(String driveId, String companyId, int page, int size);

    Page<ApplicationsDto> getAllApplications(
            String driveId, String companyId, int page, int size);


    Page<ApplicationRoundProjection> getApplicantsForDriveRound(
            String driveId, Integer roundNo, String companyId, int page, int size);

    String publishScoreForDriveRound(String driveId, String rollNo, Integer roundNo, Double score, String companyId);  // ← added companyId
    String publishFeedback(String driveId, String rollNo, Integer roundNo, String feedback, String companyId);
    String filterTopKStudents(String driveId, Integer roundNo, Integer topK, String companyId);  // ← added companyId

    String filterByCutOffMarks(String driveId, Integer roundNo, Double cutOffMarks, String companyId);  // ← added companyId
    Integer countFilterByCutOffMarks(String driveId, Integer roundNo,Double cutOffMarks, String companyId);


    // add to interface:
    ResponseEntity<byte[]> streamStudentResume(String rollNo, String companyEmail);
    Long countActiveDives(String companyId);
    Long totalDives(String companyId);

    String closeDrive(String companyId,String driveId);
}