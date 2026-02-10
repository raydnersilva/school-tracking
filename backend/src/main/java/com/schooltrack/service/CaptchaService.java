package com.schooltrack.service;

import com.schooltrack.dto.CaptchaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CaptchaService {

    private final Map<String, CaptchaEntry> captchaStore = new ConcurrentHashMap<>();

    @Value("${app.captcha.expiration-seconds}")
    private int expirationSeconds;

    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
    private static final int CAPTCHA_LENGTH = 5;
    private static final int WIDTH = 200;
    private static final int HEIGHT = 60;

    public CaptchaResponse generateCaptcha() {
        String captchaId = UUID.randomUUID().toString();
        String text = generateRandomText();
        String svg = generateSvg(text);

        captchaStore.put(captchaId, new CaptchaEntry(text, System.currentTimeMillis()));
        cleanExpired();

        return new CaptchaResponse(captchaId, svg);
    }

    public boolean validateCaptcha(String captchaId, String answer) {
        CaptchaEntry entry = captchaStore.remove(captchaId);
        if (entry == null) return false;

        long elapsed = (System.currentTimeMillis() - entry.createdAt) / 1000;
        if (elapsed > expirationSeconds) return false;

        return entry.text.equalsIgnoreCase(answer);
    }

    private String generateRandomText() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    private String generateSvg(String text) {
        Random random = new Random();
        StringBuilder svg = new StringBuilder();
        svg.append(String.format("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"%d\" height=\"%d\">", WIDTH, HEIGHT));
        svg.append(String.format("<rect width=\"%d\" height=\"%d\" fill=\"#f0f0f0\" rx=\"5\"/>", WIDTH, HEIGHT));

        // Noise lines
        for (int i = 0; i < 5; i++) {
            svg.append(String.format(
                    "<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"#%s\" stroke-width=\"1\" opacity=\"0.3\"/>",
                    random.nextInt(WIDTH), random.nextInt(HEIGHT),
                    random.nextInt(WIDTH), random.nextInt(HEIGHT),
                    randomColor(random)
            ));
        }

        // Characters
        for (int i = 0; i < text.length(); i++) {
            int x = 20 + i * 35;
            int y = 35 + random.nextInt(15);
            int rotation = -15 + random.nextInt(30);
            svg.append(String.format(
                    "<text x=\"%d\" y=\"%d\" font-size=\"%d\" fill=\"#%s\" transform=\"rotate(%d %d %d)\" font-family=\"Arial, sans-serif\" font-weight=\"bold\">%c</text>",
                    x, y, 24 + random.nextInt(8), randomDarkColor(random), rotation, x, y, text.charAt(i)
            ));
        }

        // Noise dots
        for (int i = 0; i < 30; i++) {
            svg.append(String.format(
                    "<circle cx=\"%d\" cy=\"%d\" r=\"%d\" fill=\"#%s\" opacity=\"0.3\"/>",
                    random.nextInt(WIDTH), random.nextInt(HEIGHT), 1 + random.nextInt(2), randomColor(random)
            ));
        }

        svg.append("</svg>");
        return svg.toString();
    }

    private String randomColor(Random random) {
        return String.format("%02x%02x%02x", random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    private String randomDarkColor(Random random) {
        return String.format("%02x%02x%02x", random.nextInt(128), random.nextInt(128), random.nextInt(128));
    }

    private void cleanExpired() {
        long now = System.currentTimeMillis();
        captchaStore.entrySet().removeIf(e ->
                (now - e.getValue().createdAt) / 1000 > expirationSeconds * 2L
        );
    }

    private record CaptchaEntry(String text, long createdAt) {}
}
