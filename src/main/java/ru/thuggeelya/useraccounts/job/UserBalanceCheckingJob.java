package ru.thuggeelya.useraccounts.job;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.thuggeelya.useraccounts.dao.repository.system.UserRepository;
import ru.thuggeelya.useraccounts.service.system.UserBalanceService;

@Component
@RequiredArgsConstructor
public class UserBalanceCheckingJob {

    private final UserRepository userRepository;
    private final UserBalanceService userBalanceService;

    @Scheduled(cron = "${spring.application.job.user-balance.cron}")
    public void checkUserBalance() {
        userRepository.findRelevantBalanceHistories().forEach(userBalanceService::processBalance);
    }
}
