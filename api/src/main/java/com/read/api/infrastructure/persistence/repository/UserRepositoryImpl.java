package com.read.api.infrastructure.persistence.repository;

import com.read.api.api.dto.user.UserFilter;
import com.read.api.domain.model.UserModel;
import com.read.api.domain.repository.UserRepository;
import com.read.api.infrastructure.persistence.base.BaseRepositoryImpl;
import com.read.api.infrastructure.persistence.entity.UserEntity;
import com.read.api.infrastructure.persistence.mapper.UserMapperRepository;
import com.read.api.infrastructure.persistence.mongo.MongoUserRepository;
import com.read.api.infrastructure.persistence.utils.QueryUtils;
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
            UserMapperRepository mapper
    ) {
        super(template);
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
    public UserModel save(UserModel user) {
        return mapper.toModel(
                repository.save(mapper.toEntity(user))
        );
    }

    @Override
    public UserModel insert(UserModel user) {
        return mapper.toModel(
                repository.insert(mapper.toEntity(user))
        );
    }

    @Override
    public Optional<UserModel> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toModel);
    }

    @Override
    public boolean existsById(Long id) {
        return super.existsById(id);
    }

    @Override
    public int deleteById(Long id) {
        return super.deleteById(id);
    }

    @Override
    public Page<UserModel> findAll(UserFilter filter, Pageable pageable) {
        Query query = new Query();

        // Herda os filtros bases automaticamente (id, createdAt, updatedAt)
        applyBaseFilter(query, filter);

        // Aplica os filtros específicos de usuário usando a classe utilitária do projeto
        QueryUtils.addLike(query, "name", filter.getName());
        QueryUtils.addLike(query, "email", filter.getEmail());
        QueryUtils.addEquals(query, "active", filter.getActive());

        // Executa a paginação padronizada da classe abstrata
        return toPage(query, pageable);
    }

    @Override
    public Optional<UserModel> findByEmailIgnoreCase(String email) {
        return this.repository.findByEmailIgnoreCase(email)
                .map(mapper::toModel);
    }

    @Override
    public boolean existsByEmailIgnoreCase(String email) {
        return this.repository.existsByEmailIgnoreCase(email);
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        return this.repository.existsByNameIgnoreCase(name);
    }
}