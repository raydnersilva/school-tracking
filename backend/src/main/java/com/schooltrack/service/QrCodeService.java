package com.schooltrack.service;

import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class QrCodeService {

    private final SecureRandom random = new SecureRandom();
    private final Map<String, Long> qrCodeToStudentMap = new ConcurrentHashMap<>();

    public String generateQrCode(Long studentId) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String code = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        qrCodeToStudentMap.put(code, studentId);
        return code;
    }

    public Long validateQrCode(String qrCode) {
        return qrCodeToStudentMap.get(qrCode);
    }

    public void invalidateQrCode(String qrCode) {
        qrCodeToStudentMap.remove(qrCode);
    }
}
