package com.read.api.infrastructure.persistence.repository;

import com.read.api.api.dto.urlAccessRule.UrlAccessRuleFilter;
import com.read.api.domain.enums.UrlAccessRuleTypeEnum;
import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.domain.repository.UrlAccessRuleRepository;
import com.read.api.infrastructure.persistence.base.BaseRepositoryImpl;
import com.read.api.infrastructure.persistence.entity.UrlAccessRuleEntity;
import com.read.api.infrastructure.persistence.mapper.UrlAccessRuleMapperRepository;
import com.read.api.infrastructure.persistence.mongo.MongoUrlAccessRuleRepository;
import com.read.api.infrastructure.persistence.utils.QueryUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UrlAccessRuleRepositoryImpl
        extends BaseRepositoryImpl<
        UrlAccessRuleModel,
        UrlAccessRuleEntity,
        Long
        >
        implements UrlAccessRuleRepository {

    UrlAccessRuleMapperRepository mapper;
    MongoUrlAccessRuleRepository repository;

    public UrlAccessRuleRepositoryImpl(
            MongoTemplate template,
            UrlAccessRuleMapperRepository mapper,
            MongoUrlAccessRuleRepository repository
    ) {
        super(template);
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    protected Class<UrlAccessRuleEntity> entityClass() {
        return UrlAccessRuleEntity.class;
    }

    @Override
    protected UrlAccessRuleEntity toEntity(
            UrlAccessRuleModel model
    ) {
        return mapper.toEntity(model);
    }

    @Override
    protected UrlAccessRuleModel toModel(
            UrlAccessRuleEntity entity
    ) {
        return mapper.toModel(entity);
    }

    @Override
    public UrlAccessRuleModel save(
            UrlAccessRuleModel model
    ) {
        return mapper.toModel(
                repository.save(
                        mapper.toEntity(model)
                )
        );
    }

    @Override
    public UrlAccessRuleModel insert(
            UrlAccessRuleModel model
    ) {
        return mapper.toModel(
                repository.insert(
                        mapper.toEntity(model)
                )
        );
    }

    @Override
    public Optional<UrlAccessRuleModel> findById(
            Long id
    ) {
        return repository.findById(id)
                .map(mapper::toModel);
    }

    @Override
    public Page<UrlAccessRuleModel> findAll(
            UrlAccessRuleFilter filter,
            Pageable pageable
    ) {

        Query query = new Query();

        applyBaseFilter(query, filter);

        QueryUtils.addEquals(
                query,
                "urlId",
                filter.getUrlId()
        );

        QueryUtils.addEquals(
                query,
                "type",
                filter.getType()
        );

        QueryUtils.addLike(
                query,
                "ruleValue",
                filter.getRuleValue()
        );

        QueryUtils.addEquals(
                query,
                "active",
                filter.getActive()
        );

        QueryUtils.addEquals(
                query,
                "assignedByUserId",
                filter.getAssignedByUserId()
        );

        QueryUtils.addRange(
                query,
                "expiresAt",
                filter.getExpiresAtAfter(),
                filter.getExpiresAtBefore()
        );

        return toPage(
                query,
                pageable
        );
    }

    @Override
    public boolean existsById(
            Long id
    ) {
        return super.existsById(id);
    }

    @Override
    public int deleteById(
            Long id
    ) {
        return super.deleteById(id);
    }

    @Override
    public List<UrlAccessRuleModel> findAllByUrlId(Long urlId) {
        return repository
                .findAllByUrlIdAndActiveTrueAndExpiresAtAfter(
                        urlId,
                        LocalDateTime.now()
                )
                .stream()
                .map(mapper::toModel)
                .toList();
    }

    @Override
    public boolean existsUnique(
            Long urlId,
            UrlAccessRuleTypeEnum type,
            String ruleValue
    ) {
        return repository.existsByUrlIdAndTypeAndRuleValue(
                urlId,
                type,
                ruleValue
        );
    }
}