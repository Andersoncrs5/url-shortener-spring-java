package com.read.api.infrastructure.persistence.repository;

import com.read.api.api.dto.user.UserFilter;
import com.read.api.domain.model.UserModel;
import com.read.api.domain.repository.UserRepository;
import com.read.api.infrastructure.persistence.base.BaseRepositoryImpl;
import com.read.api.infrastructure.persistence.entity.UserEntity;
import com.read.api.infrastructure.persistence.mapper.UserMapperRepository;
import com.read.api.infrastructure.persistence.mongo.MongoUserRepository;
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
public class UserRepositoryImpl
        extends BaseRepositoryImpl<UserModel, UserEntity, Long>
        implements UserRepository {

    MongoUserRepository repository;
    UserMapperRepository mapper;

    public UserRepositoryImpl(
            MongoTemplate template,
            MongoUserRepository repository,
            UserMapperRepository mapper,
            MongoRetryTranslation retryTranslator
    ) {
        super(template, retryTranslator);
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    protected Class<UserEntity> entityClass() {
        return UserEntity.class;
    }

    @Override
    protected UserEntity toEntity(UserModel model) {
        return mapper.toEntity(model);
    }

    @Override
    protected UserModel toModel(UserEntity entity) {
        return mapper.toModel(entity);
    }

    @Override
    @Retry(name = "database")
    public UserModel save(UserModel user) {
        return retryTranslator.execute(() ->mapper.toModel(
                repository.save(mapper.toEntity(user))
        ));
    }

    @Override
    @Retry(name = "database")
    public UserModel insert(UserModel user) {
        return retryTranslator.execute(() -> mapper.toModel(
                repository.insert(mapper.toEntity(user))
        ));
    }

    @Override
    @Retry(name = "database")
    public Optional<UserModel> findById(Long id) {
        return retryTranslator.execute(() ->
                repository.findById(id).map(mapper::toModel)
        );
    }

    @Override
    @Retry(name = "database")
    public boolean existsById(Long id) {
        return retryTranslator.execute(() ->
                        super.existsById(id)
                );
    }

    @Override
    @Retry(name = "database")
    public int deleteById(Long id) {
        return retryTranslator.execute(() ->(
                super.deleteById(id)
        ));
    }

    @Override
    @Retry(name = "database")
    public Page<UserModel> findAll(UserFilter filter, Pageable pageable) {
        return retryTranslator.execute(() -> {
            Query query = new Query();
            applyBaseFilter(query, filter);

            QueryUtils.addLike(query, "name", filter.getName());
            QueryUtils.addLike(query, "email", filter.getEmail());
            QueryUtils.addEquals(query, "active", filter.getActive());

            return toPage(query, pageable);
        });
    }
    @Override
    @Retry(name = "database")
    public Optional<UserModel> findByEmailIgnoreCase(String email) {
        return retryTranslator.execute(() ->
                this.repository.findByEmailIgnoreCase(email).map(mapper::toModel)
        );
    }

    @Override
    @Retry(name = "database")
    public boolean existsByEmailIgnoreCase(String email) {
        return retryTranslator.execute(() -> this.repository.existsByEmailIgnoreCase(email));
    }

    @Override
    @Retry(name = "database")
    public boolean existsByNameIgnoreCase(String name) {
        return retryTranslator.execute(() -> this.repository.existsByNameIgnoreCase(name));
    }
}