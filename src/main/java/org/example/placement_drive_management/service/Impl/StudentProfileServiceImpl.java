package org.example.placement_drive_management.service.Impl;

import jakarta.transaction.Transactional;
import org.example.placement_drive_management.dto.*;
import org.example.placement_drive_management.entity.*;
import org.example.placement_drive_management.exceptions.ResourceNotFoundException;
import org.example.placement_drive_management.mappers.ApplicationRoundMapper;
import org.example.placement_drive_management.mappers.ApplicationsMapper;
import org.example.placement_drive_management.mappers.StudentProfileMapper;
import org.example.placement_drive_management.repository.*;
import org.example.placement_drive_management.service.StudentProfileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.time.LocalDate;

@Service
@Transactional
public class StudentProfileServiceImpl implements StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;
    private final StudentRepository studentRepository;
    private final ApplicationRepository applicationRepository;
    private final DriveRepository driveRepository;
    private final ApplicationRoundRepository applicationRoundRepository;
    private final CloudinaryService cloudinaryService;
    public StudentProfileServiceImpl(StudentProfileRepository studentProfileRepository, StudentRepository studentRepository,ApplicationRepository applicationRepository,DriveRepository driveRepository, ApplicationRoundRepository applicationRoundRepository, CloudinaryService cloudinaryService) {
        this.studentProfileRepository = studentProfileRepository;
        this.studentRepository = studentRepository;
        this.applicationRepository=applicationRepository;
        this.driveRepository = driveRepository;
        this.applicationRoundRepository=applicationRoundRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public String createStudentProfile( StudentProfileDto studentProfileDto,String  rollNo) {
        Student student = studentRepository.findByRollNo(rollNo)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student with RollNo "
                                + rollNo + " not found"));

        StudentProfile studentProfile = new StudentProfile();
                studentProfile.setStudent((student));
                studentProfile.setDepartment(studentProfileDto.getDepartment());
                studentProfile.setCurrentSemester(studentProfileDto.getCurrentSemester());
                studentProfile.setHasbackloghistory(studentProfileDto.getHasBacklogHistory());
                studentProfile.setBacklogCount(studentProfileDto.getBacklogCount());
                studentProfile.setCurrentCgpa(studentProfileDto.getCurrentCgpa());
                studentProfile.setTenthPercentage(studentProfileDto.getTenthPercentage());
                studentProfile.setDiplomaPercentage(studentProfileDto.getDiplomaPercentage());
                studentProfile.setTwelthPercentage(studentProfileDto.getTwelthPercentage());
                studentProfile.setGender(studentProfileDto.getGender());
                studentProfile.setPassingYear(studentProfileDto.getPassingYear());
        studentProfileRepository.save(studentProfile);

        return "Profile Created Successfully";
    }



    @Override
    public String updateStudentProfile(
                                       StudentProfileDto studentProfileDto,String rollNo) {

        StudentProfile existingProfile =
                studentProfileRepository.findByStudentRollNo(rollNo)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Student with RollNo "
                                        + rollNo + " not found"));

        existingProfile.setDepartment(studentProfileDto.getDepartment());
        existingProfile.setTenthPercentage(studentProfileDto.getTenthPercentage());
        existingProfile.setTwelthPercentage(studentProfileDto.getTwelthPercentage());
        existingProfile.setDiplomaPercentage(studentProfileDto.getDiplomaPercentage());
        existingProfile.setCurrentCgpa(studentProfileDto.getCurrentCgpa());
        existingProfile.setCurrentSemester(studentProfileDto.getCurrentSemester());
        existingProfile.setBacklogCount(studentProfileDto.getBacklogCount());
        existingProfile.setHasbackloghistory(studentProfileDto.getHasBacklogHistory());
        existingProfile.setGender(studentProfileDto.getGender());
        existingProfile.setPassingYear(studentProfileDto.getPassingYear());
        studentProfileRepository.save(existingProfile);

        return "Profile Updated Successfully";
    }
    @Override
    public StudentProfileDto getStudentProfile(String rollNo) {
        StudentProfile studentProfile=studentProfileRepository.findByStudentRollNo(rollNo)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student with RollNo "
                                + rollNo + " not found"));
        return StudentProfileMapper.maptoStudentProfileDto(studentProfile);
    }
    @Override
    public Page<ApplicationsDto> getAllApplicationsForStudent(String studentRollNo, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<Applications> applicationsPage =
                applicationRepository.findActiveApplications(
                        studentRollNo, pageable);

        return applicationsPage.map(application -> {

            ApplicationsDto dto = ApplicationsMapper.mapToApplicationDto(application);

            Drive drive = application.getDrive();
            DriveInfoDto driveInfoDto = new DriveInfoDto();

            driveInfoDto.setCompanyName(drive.getCompany().getCompanyName());
            driveInfoDto.setRole(drive.getJobRole());
            driveInfoDto.setPackageAmount(drive.getPackageOffered());

            dto.setDriveInfo(driveInfoDto);

            return dto;
        });
    }

    @Override
    public Page<ApplicationRoundDto> getAllApplicationRoundsForStudentAndDriveId(
            String driveId,
            String rollNumberInContext,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);

        return applicationRoundRepository
                .findAllRoundDetails(driveId, rollNumberInContext, pageable)
                .map(ApplicationRoundMapper::maptoApplicationRoundDto);
    }

    @Override
    public String applyDrive(String driveId, String rollNo) {
        Applications application = applicationRepository
                .findByDrive_DriveIdAndStudent_RollNo(driveId, rollNo)
                .orElse(null);
        Drive drive = driveRepository.findByDriveId(driveId)
                .orElseThrow(() -> new ResourceNotFoundException("Drive not found"));
        LocalDate today = LocalDate.now();
        if (today.isBefore(drive.getRegistrationStartDate()) ||
                today.isAfter(drive.getRegistrationEndDate())) {
            return "Application time is over";
        }
        if (application == null) {
            // create new if not exists
            application = new Applications();
            application.setDrive(drive);
            application.setStudent(studentRepository.findByRollNo(rollNo)
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found")));
        }
        if ("APPLIED".equals(application.getStatus())) {
            return "You have already applied";
        }
        application.setAppliedDate(today);
        application.setCurrentRoundNumber(0);
        application.setStatus("APPLIED");
        application.setExternalApplied(false);
        applicationRepository.save(application);
        return "Application applied successfully";
    }

    @Override
    public Page<ApplicationsDto> getAllEligibleApplications(String rollNo, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<Applications> applicationsPage =
                applicationRepository.findByStudentRollNoAndStatus(
                        rollNo,
                        "ELIGIBLE",
                        pageable
                );

        return applicationsPage.map(application -> {

            ApplicationsDto dto = ApplicationsMapper.mapToApplicationDto(application);

            Drive drive = application.getDrive();
            DriveInfoDto driveInfoDto = new DriveInfoDto();

            driveInfoDto.setCompanyName(drive.getCompany().getCompanyName());
            driveInfoDto.setRole(drive.getJobRole());
            driveInfoDto.setPackageAmount(drive.getPackageOffered());

            dto.setDriveInfo(driveInfoDto);
            return dto;
        });
    }
    private Student getStudentByEmail(String email) {
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found with email: " + email));
    }
    private StudentProfile getProfileByEmail(String email) {
        Student student = getStudentByEmail(email);
        return studentProfileRepository.findByStudentRollNo(student.getRollNo())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Profile not found for student: " + email));
    }
    @Override
    public String uploadResume(MultipartFile file, String email) {
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("File must not be empty");

        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf"))
            throw new IllegalArgumentException("Only PDF files are accepted");

        if (file.getSize() > 5 * 1024 * 1024)
            throw new IllegalArgumentException("File size must be under 5 MB");

        StudentProfile profile = getProfileByEmail(email);
        Student student = profile.getStudent();

        try {
            // resource_type=raw → PDF stored verbatim, never garbled on download
            // public_id=rollNo  → re-uploads overwrite same file, no duplicates
            String secureUrl = cloudinaryService.uploadResume(file, student.getRollNo());
            profile.setResumeUrl(secureUrl);
            studentProfileRepository.save(profile);
            return "Resume uploaded successfully";
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to upload resume: " + e.getMessage(), e);
        }
    }

    @Override
    public ResponseEntity<byte[]> streamResume(String email) {
        StudentProfile profile = getProfileByEmail(email);
        if (profile.getResumeUrl() == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            byte[] bytes = cloudinaryService.fetchResumeBytes(profile.getResumeUrl());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // inline = render in browser, not download
            headers.setContentDispositionFormData("inline", "resume.pdf");
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"resume.pdf\"");
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch resume: " + e.getMessage(), e);
        }
    }

    @Override
    public Long getEligibleDrivesCount(String rollNo) {
        return (long) applicationRepository.findByStudentRollNoAndStatus(rollNo, "ELIGIBLE").size();
    }
    @Override
    public Long getInProcessDrivesCount(String rollNo) {
        return (long) applicationRepository.findByStudentRollNoAndStatus(rollNo, "INPROCESS").size();
    }
    @Override
    public Long getSelectedDrivesCount(String rollNo) {
        return (long) applicationRepository.findByStudentRollNoAndStatus(rollNo, "SELECTED").size();
    }
    @Override
    public Long getAppliedDrivesCount(String rollNo) {
        return (long) applicationRepository.findByStudentRollNoAndStatus(rollNo, "APPLIED").size();
    }
}
