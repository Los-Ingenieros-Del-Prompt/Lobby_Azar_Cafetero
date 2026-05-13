package com.aguardientes.azarcafetero.lobby.infrastructure.sse;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BalanceSseService {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String userId) {
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(e -> emitters.remove(userId));
        emitters.put(userId, emitter);
        return emitter;
    }

    public void notifyBalanceUpdate(String userId, BigDecimal newBalance, BigDecimal diff) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                String diffFormatted = diff.compareTo(BigDecimal.ZERO) >= 0
                        ? "+" + diff.toPlainString()
                        : diff.toPlainString();
                emitter.send(SseEmitter.event()
                        .name("balance-update")
                        .data(Map.of(
                                "userId", userId,
                                "balance", newBalance,
                                "diff", diffFormatted
                        )));
            } catch (IOException e) {
                emitters.remove(userId);
            }
        }
    }
}