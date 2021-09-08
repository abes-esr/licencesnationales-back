package fr.abes.licencesnationales.web.security.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import fr.abes.licencesnationales.core.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LoginAttemptService {

    private int maxAttempt = 10;

    private int nbMinutes = 15;

    private LoadingCache<String, Integer> attemptsCache;

    public LoginAttemptService() {
        super();
        attemptsCache = CacheBuilder.newBuilder().expireAfterWrite(nbMinutes, TimeUnit.MINUTES).build(new CacheLoader<String, Integer>() {
            @Override
            public Integer load(final String key) {
                return 0;
            }
        });
    }

    //

    public void loginSucceeded(final String key) {
        log.debug(Constant.ENTER_LOGIN_SUCCEED + key);
        attemptsCache.invalidate(key);
    }

    public void loginFailed(final String key) {
        log.debug(Constant.ENTER_LOGIN_FAILED);
        int attempts = 0;
        try {
            attempts = attemptsCache.get(key);
            log.info(Constant.NUMBER_IP_TENTATIVES + key + " est : " + attempts);
        } catch (final ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(key, attempts);
    }

    public boolean isBlocked(final String key) {
        try {
            log.info(Constant.ERROR_BLOCKED_IP + attemptsCache.get(key));
            return attemptsCache.get(key) >= maxAttempt;
        } catch (final ExecutionException e) {
            return false;
        }
    }
}

