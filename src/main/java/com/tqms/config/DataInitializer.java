package com.tqms.config;

import com.tqms.model.*;
import com.tqms.repository.QueueRecordRepository;
import com.tqms.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository, QueueRecordRepository queueRecordRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = new User("admin", "$2a$10$e8Qk3iV9e6gXJm3P8M", Role.ADMIN);
                User receptionist = new User("receptionist", "$2a$10$e8Qk3iV9e6gXJm3P8M", Role.RECEPTIONIST);
                User doctor = new User("doctor", "$2a$10$e8Qk3iV9e6gXJm3P8M", Role.DOCTOR);

                userRepository.save(admin);
                userRepository.save(receptionist);
                userRepository.save(doctor);
            }

            if (queueRecordRepository.count() == 0) {
                User staff = userRepository.findByUsername("receptionist").orElse(null);

                QueueRecord r1 = new QueueRecord("TQ-001", "Rajesh Khanna", "+91 9820112233", "General Medicine", Priority.NORMAL, staff);
                r1.setStatus(QueueStatus.COMPLETED);
                r1.setSymptoms("Seasonal viral fever and body ache");
                queueRecordRepository.save(r1);

                QueueRecord r2 = new QueueRecord("TQ-002", "Sunita Nair", "+91 9840223344", "Cardiology", Priority.URGENT, staff);
                r2.setStatus(QueueStatus.IN_CONSULTATION);
                r2.setSymptoms("Elevated blood pressure with dizziness");
                queueRecordRepository.save(r2);

                QueueRecord r3 = new QueueRecord("TQ-003", "Vikram Malhotra", "+91 9811334455", "Orthopedics", Priority.NORMAL, staff);
                r3.setStatus(QueueStatus.CALLED);
                r3.setSymptoms("Right ankle sprain after workout");
                queueRecordRepository.save(r3);

                QueueRecord r4 = new QueueRecord("TQ-004", "Ananya Deshmukh", "+91 9877445566", "Pediatrics", Priority.NORMAL, staff);
                r4.setStatus(QueueStatus.WAITING);
                r4.setSymptoms("Persistent dry cough in 5-year-old child");
                queueRecordRepository.save(r4);

                QueueRecord r5 = new QueueRecord("TQ-005", "Mohammed Farooqui", "+91 9899556677", "Dermatology", Priority.NORMAL, staff);
                r5.setStatus(QueueStatus.WAITING);
                r5.setSymptoms("Acute skin rash and allergic redness");
                queueRecordRepository.save(r5);

                QueueRecord r6 = new QueueRecord("TQ-006", "Kavita Sengupta", "+91 9833667788", "Cardiology", Priority.URGENT, staff);
                r6.setStatus(QueueStatus.WAITING);
                r6.setSymptoms("Shortness of breath on mild exertion");
                queueRecordRepository.save(r6);
            }
        };
    }
}
