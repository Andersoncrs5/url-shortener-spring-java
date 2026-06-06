package com.write.api.application.service.urlRedirectRule;

import com.write.api.application.dto.urlRedirectRule.UpdateUrlRedirectRuleDTO;
import com.write.api.application.mapper.urlRedirectRule.UpdateUrlRedirectRuleServiceMapper;
import com.write.api.application.shared.Result;
import com.write.api.application.shared.annotations.TrackExecutionTime;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.UrlRedirectRuleModel;
import com.write.api.ports.in.urlRedirectRule.UpdateUrlRedirectRuleUseCase;
import com.write.api.ports.out.repository.IUrlRedirectRuleRepository;
import com.write.api.shared.db.DatabaseConstraintHandler;
import com.write.api.shared.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UpdateUrlRedirectRuleService implements UpdateUrlRedirectRuleUseCase {

    IUrlRedirectRuleRepository repository;
    UpdateUrlRedirectRuleServiceMapper mapper;

    @Override
    @ResultTransaction
    @TrackExecutionTime("url.redirect.update")
    public Result<UrlRedirectRuleModel> execute(Long id, UpdateUrlRedirectRuleDTO dto) {
        var rule = this.repository.findById(id).orElse(null);
        if (rule == null) return Result.failure(404, "Rule not found");

        mapper.update(dto, rule);

        try {

            String hash = this.buildRuleHash(rule);
            rule.setRuleHash(hash);
            UrlRedirectRuleModel saved = repository.save(rule);

            return Result.success(saved, 200);
        } catch (DataIntegrityViolationException e) {
            String message =
                    e.getMostSpecificCause().getMessage();

            if (message == null) return DatabaseConstraintHandler.handle(e);

            if (message.contains("uk_url_redirect_rules_hash")) {
                return Result.failure(
                        "Rule already present in url",
                        409
                );
            }

            return DatabaseConstraintHandler.handle(e);
        } catch (Exception e) {
            throw new InternalServerErrorException(
                    e.getMessage()
            );
        }
    }

    private String buildRuleHash(UrlRedirectRuleModel entity) {
        try {
            String raw = String.join("|",
                    String.valueOf(entity.getUrlId()),
                    String.valueOf(entity.getCountryCode()),
                    String.valueOf(entity.getRegion()),
                    String.valueOf(entity.getContinent()),
                    String.valueOf(entity.getOs()),
                    String.valueOf(entity.getBrowser()),
                    String.valueOf(entity.getMatchType()),
                    String.valueOf(entity.getRedirectUrl()),
                    String.valueOf(entity.getPriority())
            );

            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException("Failed to build rule hash", e);
        }
    }


}
