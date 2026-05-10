package com.digitalid.infrastructure.config;

import java.time.LocalDate;

import com.digitalid.application.port.out.CertificationRepository;
import com.digitalid.application.port.out.WorkerRepository;
import com.digitalid.domain.model.Certification;
import com.digitalid.domain.model.CertificationType;
import com.digitalid.domain.model.Region;
import com.digitalid.domain.model.Worker;
import com.digitalid.domain.model.WorkerStatus;

public class DataSeeder {

    private final WorkerRepository workerRepository;
    private final CertificationRepository certRepository;

    public DataSeeder(WorkerRepository workerRepository, CertificationRepository certRepository) {
        this.workerRepository = workerRepository;
        this.certRepository = certRepository;
    }

    public void seed() {
        // Only seed if no workers exist yet
        if (!workerRepository.listAll().isEmpty()) {
            return;
        }

        // Workers
        Worker w1 = new Worker("WK-US-1", "Josh Mantle", LocalDate.of(2007, 4, 12),
                "josh.mantle@gmail.com", Region.UNITED_STATES);
        Worker w2 = new Worker("WK-UK-2", "Joanne Binith", LocalDate.of(2006, 8, 5),
                "joanne.binith@gmail.com", Region.UNITED_KINGDOM);
        Worker w3 = new Worker("WK-DE-3", "Fred Lewis", LocalDate.of(2004, 2, 19),
                "fred.lewis@gmail.com", Region.GERMANY);
        Worker w4 = new Worker("WK-JP-4", "Kim Ellis", LocalDate.of(2007, 11, 30),
                "kim.ellis@gmail.com", Region.JAPAN);
        Worker w5 = new Worker("WK-SG-5", "Adhish Sunishkumar", LocalDate.of(2007, 6, 14),
                "adhish.sunishkumar@gmail.com", Region.SINGAPORE);
        Worker w6 = new Worker("WK-FR-6", "Eloise Sharpe", LocalDate.of(2006, 9, 3),
                "eloise.sharpe@gmail.com", Region.FRANCE);
        Worker w7 = new Worker("WK-HK-7", "Yi Long Cheong", LocalDate.of(2007, 1, 15),
                "yilong.cheong@gmail.com", Region.HONG_KONG);
        Worker w8 = new Worker("WK-UK-8", "Akifah Hussain", LocalDate.of(2005, 7, 11),
                "akifah.hussain@gmail.com", Region.UNITED_KINGDOM);
        Worker w9 = new Worker("WK-IT-9", "Qasim Hallim Charlie", LocalDate.of(2006, 12, 25),
                "qasim.charlie@gmail.com", Region.ITALY);
        Worker w10 = new Worker("WK-US-10", "Benjamin Newington", LocalDate.of(1990, 3, 8),
                "benjamin.newington@gmail.com", Region.UNITED_STATES);

        w10.changeStatus(WorkerStatus.SUSPENDED);
        w10.changeStatus(WorkerStatus.REVOKED);

        workerRepository.save(w1);
        workerRepository.save(w2);
        workerRepository.save(w3);
        workerRepository.save(w4);
        workerRepository.save(w5);
        workerRepository.save(w6);
        workerRepository.save(w7);
        workerRepository.save(w8);
        workerRepository.save(w9);
        workerRepository.save(w10);

        // Certifications — US workers
        Certification c1 = new Certification("CERT-001", "WK-US-1",
                CertificationType.US_FOOD_HANDLER, "State Health Department",
                "FH-44521", LocalDate.of(2025, 6, 1), LocalDate.of(2028, 6, 1));

        Certification c8 = new Certification("CERT-008", "WK-US-10",
                CertificationType.US_FOOD_HANDLER, "State Health Department",
                "FH-11098", LocalDate.of(2023, 6, 15), LocalDate.of(2026, 6, 15));

        // Certifications — UK workers (rich data for Fine Dining demo)
        Certification c2 = new Certification("CERT-002", "WK-UK-2",
                CertificationType.UK_LEVEL_2_FOOD_SAFETY, "CIEH / Highfield",
                "L2-90812", LocalDate.of(2025, 1, 10), LocalDate.of(2028, 1, 10));

        Certification c9 = new Certification("CERT-009", "WK-UK-2",
                CertificationType.UK_ALLERGEN_TRAINING, "FSA Approved Provider",
                "ALG-20451", LocalDate.of(2025, 3, 15), LocalDate.of(2027, 3, 15));

        // Joanne also holds a US cert from previous employment — demonstrates region filtering
        Certification c13 = new Certification("CERT-013", "WK-UK-2",
                CertificationType.US_FOOD_HANDLER, "State Health Department",
                "FH-67210", LocalDate.of(2024, 9, 1), LocalDate.of(2027, 9, 1));

        Certification c10 = new Certification("CERT-010", "WK-UK-8",
                CertificationType.UK_LEVEL_2_FOOD_SAFETY, "CIEH / Highfield",
                "L2-41099", LocalDate.of(2025, 8, 20), LocalDate.of(2028, 8, 20));

        Certification c11 = new Certification("CERT-011", "WK-UK-8",
                CertificationType.UK_DBS_CHECK, "Disclosure and Barring Service",
                "DBS-88234", LocalDate.of(2024, 11, 1), LocalDate.of(2027, 11, 1));

        Certification c12 = new Certification("CERT-012", "WK-UK-8",
                CertificationType.UK_ALLERGEN_TRAINING, "FSA Approved Provider",
                "ALG-33102", LocalDate.of(2025, 2, 5), LocalDate.of(2027, 2, 5));

        // Certifications — other regions
        Certification c3 = new Certification("CERT-003", "WK-DE-3",
                CertificationType.GERMANY_GESUNDHEITSZEUGNIS, "Gesundheitsamt",
                "GZ-33104", LocalDate.of(2023, 4, 20), null); // lifetime

        Certification c4 = new Certification("CERT-004", "WK-JP-4",
                CertificationType.JAPAN_FOOD_SANITATION_MANAGER, "Public Health Center",
                "FSM-55012", LocalDate.of(2024, 2, 1), null); // lifetime

        Certification c5 = new Certification("CERT-005", "WK-SG-5",
                CertificationType.SINGAPORE_FOOD_SAFETY_LEVEL_1, "Singapore Food Agency",
                "SFA-20198", LocalDate.of(2025, 3, 1), LocalDate.of(2030, 3, 1));

        Certification c6 = new Certification("CERT-006", "WK-HK-7",
                CertificationType.HONG_KONG_BASIC_FOOD_HYGIENE, "FEHD",
                "HK-77231", LocalDate.of(2024, 5, 10), null); // lifetime

        certRepository.save(c1);
        certRepository.save(c2);
        certRepository.save(c3);
        certRepository.save(c4);
        certRepository.save(c5);
        certRepository.save(c6);
        certRepository.save(c8);
        certRepository.save(c9);
        certRepository.save(c10);
        certRepository.save(c11);
        certRepository.save(c12);
        certRepository.save(c13);
    }

}
