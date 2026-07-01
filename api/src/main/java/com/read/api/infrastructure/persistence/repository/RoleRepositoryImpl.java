package com.read.api.infrastructure.persistence.repository;

import com.read.api.api.dto.role.RoleFilter;
import com.read.api.domain.model.RoleModel;
import com.read.api.domain.repository.RoleRepository;
import com.read.api.infrastructure.persistence.base.BaseRepositoryImpl;
import com.read.api.infrastructure.persistence.entity.RoleEntity;
import com.read.api.infrastructure.persistence.mapper.RoleMapperRepository;
import com.read.api.infrastructure.persistence.mongo.MongoRoleRepository;
import com.read.api.infrastructure.persistence.shared.MongoRetryTranslation;
import com.read.api.infrastructure.persistence.utils.QueryUtils;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleRepositoryImpl
        extends BaseRepositoryImpl<RoleModel, RoleEntity, Long>
        implements RoleRepository {

    RoleMapperRepository mapper;
    MongoRoleRepository repository;

    public RoleRepositoryImpl(
            MongoTemplate template,
            RoleMapperRepository mapper,
            MongoRoleRepository repository,
            MongoRetryTranslation retryTranslator
    ) {
        super(template, retryTranslator);
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    protected Class<RoleEntity> entityClass() {
        return RoleEntity.class;
    }

    @Override
    protected RoleEntity toEntity(RoleModel model) {
        return mapper.toEntity(model);
    }

    @Override
    protected RoleModel toModel(RoleEntity entity) {
        return mapper.toModel(entity);
    }

    @Override
    @Retry(name = "database")
    public RoleModel save(RoleModel role) {
        return retryTranslator.execute(() -> mapper.toModel(
                repository.save(mapper.toEntity(role))
        ));
    }

    @Override
    @Retry(name = "database")
    public RoleModel insert(RoleModel role) {
        return retryTranslator.execute(() -> mapper.toModel(
                repository.insert(mapper.toEntity(role))
        ));
    }

    @Override
    @Retry(name = "database")
    public Optional<RoleModel> findById(Long id) {
        return retryTranslator.execute(() -> repository.findById(id).map(mapper::toModel));
    }

    @Override
    @Retry(name = "database")
    public boolean existsById(Long id) {
        return retryTranslator.execute(() -> super.existsById(id));
    }

    @Override
    @Retry(name = "database")
    public int deleteById(Long id) {
        return retryTranslator.execute(() -> super.deleteById(id));
    }

    @Override
    @Retry(name = "database")
    public Page<RoleModel> findAll(RoleFilter filter, Pageable pageable) {
        return retryTranslator.execute(() -> {
            Query query = new Query();

            // Aplica automaticamente os filtros de id, createdAt e updatedAt
            applyBaseFilter(query, filter);

            // Aplica os filtros específicos de Role usando o padrão QueryUtils do projeto
            QueryUtils.addLike(query, "name", filter.getName());
            QueryUtils.addLike(query, "description", filter.getDescription());
            QueryUtils.addEquals(query, "active", filter.getActive());

            // Faz o count, injeta o pageable, busca no banco e mapeia para Model automaticamente
            return toPage(query, pageable);
        });
    }
}