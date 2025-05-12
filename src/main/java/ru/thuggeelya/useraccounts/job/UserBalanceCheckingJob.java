package ru.thuggeelya.useraccounts.job;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.thuggeelya.useraccounts.service.system.UserBalanceService;

@Component
@RequiredArgsConstructor
public class UserBalanceCheckingJob {

    private final UserBalanceService userBalanceService;

    @Scheduled(cron = "${spring.application.job.user-balance.cron}")
    public void checkUserBalance() {
        userBalanceService.findForUpdate().forEach(userBalanceService::incrementBalance);
    }
}
